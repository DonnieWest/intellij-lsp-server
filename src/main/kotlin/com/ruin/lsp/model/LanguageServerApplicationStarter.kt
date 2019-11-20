package com.ruin.lsp.model

import com.intellij.ide.CliResult
import com.intellij.openapi.application.ApplicationStarter
import java.util.concurrent.Future

class LspApplicationStarter : ApplicationStarter {
    override fun getCommandName(): String = "lang-server"
    override fun isHeadless(): Boolean = true
    override fun canProcessExternalCommandLine(): Boolean = true

    override fun processExternalCommandLineAsync(args: Array<out String>, currentDirectory: String?): Future<out CliResult> {
        return super.processExternalCommandLineAsync(args, currentDirectory)
    }

    override fun premain(args: Array<out String>) {
        // pass
    }

    override fun main(a: Array<out String>) {
        val runner = LanguageServerRunnerImpl.getInstance()
        val port = LanguageServerConfig.getInstance().portNumber
        runner.run(port)
    }
}
