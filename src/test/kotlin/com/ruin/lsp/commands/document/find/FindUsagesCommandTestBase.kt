package com.ruin.lsp.commands.document.find

import org.eclipse.lsp4j.Position

abstract class FindUsagesCommandTestBase : FindCommandTestBase() {
    override fun command(at: Position, uri: String) =
        FindUsagesCommand(at)
}
