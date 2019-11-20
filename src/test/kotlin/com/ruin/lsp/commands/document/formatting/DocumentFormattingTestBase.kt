package com.ruin.lsp.commands.document.formatting

import com.intellij.openapi.application.runUndoTransparentWriteAction
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.PsiDocumentManager
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase
import com.ruin.lsp.model.applyTextEditChanges
import com.ruin.lsp.model.invokeCommandAndWait
import com.ruin.lsp.model.sortTextEditChanges
import com.ruin.lsp.util.getDocument
import org.eclipse.lsp4j.FormattingOptions
import org.eclipse.lsp4j.Range

abstract class DocumentFormattingTestBase : LightPlatformCodeInsightFixtureTestCase() {
    abstract val fileType: FileType

    protected fun checkDocumentFormat(before: String, after: String, options: FormattingOptions, range: Range? = null) {
        myFixture.configureByText(fileType, before)
        val doc = getDocument(myFixture.file)!!
        val command = DocumentFormattingCommand(options, range)
        val result = invokeCommandAndWait(command, project, myFixture.file)
        runUndoTransparentWriteAction {
            applyTextEditChanges(doc, sortTextEditChanges(result))
            PsiDocumentManager.getInstance(project).commitDocument(doc)
        }
        assertEquals(after, doc.text)
    }
}
