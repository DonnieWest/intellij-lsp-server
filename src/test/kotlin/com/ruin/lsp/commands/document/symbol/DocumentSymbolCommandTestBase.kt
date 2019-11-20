package com.ruin.lsp.commands.document.symbol

import com.ruin.lsp.BaseTestCase
import com.ruin.lsp.model.invokeCommandAndWait
import com.ruin.lsp.util.getURIForFile
import com.ruin.lsp.util.resolvePsiFromUri
import org.eclipse.lsp4j.*
import org.eclipse.lsp4j.jsonrpc.messages.Either
import java.io.File

abstract class DocumentSymbolCommandTestBase : BaseTestCase() {
    protected fun getSymbols(filePath: String): List<SymbolInformation> {
        val file = resolvePsiFromUri(project, getURIForFile(File(filePath)))!!
        val command = DocumentSymbolCommand(TextDocumentIdentifier(getURIForFile(file)))
        return invokeCommandAndWait<MutableList<Either<SymbolInformation, DocumentSymbol>>>(command, project, file)
    }

    protected fun List<SymbolInformation>.assertHasSymbol(
        expectedName: String,
        expectedParent: String?,
        expectedKind: SymbolKind,
        expectedRange: Range
    ): List<SymbolInformation> = apply {
        val symbol = asSequence().firstOrNull {
            it.name == expectedName && it.containerName == expectedParent
        }
        assertNotNull("$expectedName($expectedParent) is missing from $this", symbol)
        assertEquals("$expectedName($expectedParent): unexpected SymbolKind", expectedKind, symbol!!.kind)
        assertEquals("$expectedName($expectedParent): unexpected Range", expectedRange, symbol.location.range)
    }
}
