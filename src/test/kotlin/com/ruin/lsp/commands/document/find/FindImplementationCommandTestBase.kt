package com.ruin.lsp.commands.document.find

import org.eclipse.lsp4j.Position

abstract class FindImplementationCommandTestBase : FindCommandTestBase() {
    override fun command(at: Position, uri: String) =
        FindImplementationCommand(at)
}
