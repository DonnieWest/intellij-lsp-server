package com.ruin.lsp.commands.document.symbol

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiRecursiveElementVisitor
import java.lang.UnsupportedOperationException
import org.eclipse.lsp4j.jsonrpc.CancelChecker

internal class DocumentSymbolPsiVisitor(
    private val psiFile: PsiFile,
    private val cancelToken: CancelChecker?,
    private val onElement: (PsiElement) -> Unit
) : PsiRecursiveElementVisitor() {

    fun visit() {
        visitElement(psiFile)
    }

    override fun visitElement(element: PsiElement) {
        cancelToken?.checkCanceled()
        onElement(element)
        super.visitElement(element)
    }

    override fun visitFile(file: PsiFile?) {
        throw UnsupportedOperationException("Use visit() instead.")
    }
}
