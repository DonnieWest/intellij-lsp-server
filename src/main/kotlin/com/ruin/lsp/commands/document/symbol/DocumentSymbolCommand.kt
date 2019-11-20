package com.ruin.lsp.commands.document.symbol

import com.intellij.psi.PsiElement
import com.ruin.lsp.commands.DocumentCommand
import com.ruin.lsp.commands.ExecutionContext
import com.ruin.lsp.model.LanguageServerException
import com.ruin.lsp.util.*
import org.eclipse.lsp4j.DocumentSymbol
import org.eclipse.lsp4j.SymbolInformation
import org.eclipse.lsp4j.TextDocumentIdentifier
import org.eclipse.lsp4j.jsonrpc.messages.Either

class DocumentSymbolCommand(
    private val textDocumentIdentifier: TextDocumentIdentifier
) : DocumentCommand<MutableList<Either<SymbolInformation, DocumentSymbol>>> {

    override fun execute(ctx: ExecutionContext): MutableList<Either<SymbolInformation, DocumentSymbol>> {
        val uri = textDocumentIdentifier.uri
        val document = getDocument(ctx.file) ?: throw LanguageServerException("No document found.")
        val symbols = mutableListOf<SymbolInformation>()
        DocumentSymbolPsiVisitor(ctx.file, ctx.cancelToken) { element ->
            val kind = element.symbolKind()
            val name = element.symbolName()
            if (kind != null && name != null) {
                symbols.add(SymbolInformation(name, kind, element.location(uri, document), element.containerName()))
            }
        }.visit()
        symbols.sortBy { it.location.range.start.toOffset(document) }
        return symbols.map {
            Either.forLeft<SymbolInformation, DocumentSymbol>(it)
        }.toMutableList()
    }
}

private fun PsiElement.containerName(): String? =
    generateSequence(parent, { it.parent })
        .firstOrNull { it.symbolKind() != null && it.symbolName() != null }
        ?.symbolName()
