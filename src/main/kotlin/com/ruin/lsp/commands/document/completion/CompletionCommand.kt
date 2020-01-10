package com.ruin.lsp.commands.document.completion

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.completion.impl.CamelHumpMatcher
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.util.Consumer
import com.ruin.lsp.commands.DocumentCommand
import com.ruin.lsp.commands.ExecutionContext
import com.ruin.lsp.model.CompletionResolveIndex
import com.ruin.lsp.model.PreviousCompletionCacheService
import com.ruin.lsp.util.withEditor
import java.util.*
import org.eclipse.lsp4j.CompletionItem
import org.eclipse.lsp4j.CompletionList
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.jsonrpc.CancelChecker
import org.eclipse.lsp4j.jsonrpc.messages.Either

class CompletionCommand(
    val position: Position,
    private val snippetSupport: Boolean
) : DocumentCommand<Either<MutableList<CompletionItem>, CompletionList>> {
    override fun execute(ctx: ExecutionContext): Either<MutableList<CompletionItem>, CompletionList> {
        var lookupElements: Array<LookupElement> = arrayOf()

        val completionCache = PreviousCompletionCacheService.getInstance()
        val completionId = completionCache.incrementId()

        withEditor(this, ctx.file, position) { editor ->
            val params = makeCompletionParameters(editor, ctx.project) ?: return@withEditor
            val prefix = CompletionData.findPrefixStatic(params.position, params.offset)
            val matcher = CamelHumpMatcher(prefix, false)
            val sorter = CompletionService.getCompletionService().defaultSorter(params, matcher)
            val contributors = CompletionContributor.forParameters(params)

            lookupElements = performCompletion(params, contributors, sorter, matcher, ctx.cancelToken)
        }

        val result = lookupElements.mapIndexedNotNull { i, it ->
            val dec = CompletionDecorator.from(it, snippetSupport)
            dec?.completionItem?.apply {
                this.sortText = i.toString()
                this.data = CompletionResolveIndex(completionId, i)
            }
        }
        completionCache.cacheCompletion(ctx.file, lookupElements)

        return Either.forRight(CompletionList(false, result.toMutableList()))
    }
}

fun performCompletion(
    parameters: CompletionParameters,
    contributors: List<CompletionContributor>,
    sorter: CompletionSorter,
    matcher: PrefixMatcher,
    cancelToken: CancelChecker?
): Array<LookupElement> {
    val lookupSet = LinkedHashSet<LookupElement>()
    getVariantsFromContributors(parameters, contributors, sorter, matcher, null, cancelToken, Consumer { result ->
        lookupSet.add(result.lookupElement)
    })
    return lookupSet.toTypedArray()
}

/**
 * Run all contributors until any of them returns false or the list is exhausted. If from parameter is not null, contributors
 * will be run starting from the next one after that.
 */
fun getVariantsFromContributors(
    parameters: CompletionParameters,
    contributors: List<CompletionContributor>,
    sorter: CompletionSorter,
    matcher: PrefixMatcher,
    from: CompletionContributor?,
    cancelToken: CancelChecker?,
    consumer: Consumer<CompletionResult>
) {
    (contributors.indexOf(from) + 1 until contributors.size).forEach { i ->
        val contributor = contributors[i]
        cancelToken?.checkCanceled()

        val result = CompletionResultSetImpl(consumer, parameters.offset, matcher, contributor, parameters, sorter, null, cancelToken, contributors)

        contributor.fillCompletionVariants(parameters, result)
        if (result.isStopped) {
            return
        }
    }
}
