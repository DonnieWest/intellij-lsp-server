package org.lsp.javaproject

internal class Constants {
    private val notConstant = "notConstant"
    var yes = true
    var no = false
    var maybe: Boolean? = null

    companion object {
        const val INT = 42
        private const val STRING = "string"
        val ENUM_TYPE = EnumType.FOO
        var sNotConstant = 12
    }
}
