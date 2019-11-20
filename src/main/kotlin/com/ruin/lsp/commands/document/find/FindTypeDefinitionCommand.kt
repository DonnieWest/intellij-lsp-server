package com.ruin.lsp.commands.document.find

import com.intellij.codeInsight.navigation.actions.GotoTypeDeclarationAction
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import com.ruin.lsp.commands.DocumentCommand
import com.ruin.lsp.commands.ExecutionContext
import com.ruin.lsp.model.LanguageServerException
import com.ruin.lsp.util.getDocument
import com.ruin.lsp.util.toOffset
import com.ruin.lsp.util.withEditor
import org.eclipse.lsp4j.Location
import org.eclipse.lsp4j.LocationLink
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.jsonrpc.messages.Either

class FindTypeDefinitionCommand(val position: Position) : DocumentCommand<Either<MutableList<out Location>, MutableList<out LocationLink>>> {
    override fun execute(ctx: ExecutionContext): Either<MutableList<out Location>, MutableList<out LocationLink>> {
        val doc = getDocument(ctx.file)
            ?: throw LanguageServerException("No document found.")

        val offset = position.toOffset(doc)

        val ref = Ref<Array<PsiElement>>(arrayOf())
        withEditor(this, ctx.file, offset) { editor ->
            val symbolTypes = GotoTypeDeclarationAction.findSymbolTypes(editor, offset)
            ref.set(symbolTypes)
        }
        val result = ref.get()

        return Either.forLeft(result?.map(PsiElement::sourceLocationIfPossible)?.toMutableList()
            ?: mutableListOf())
    }
}
