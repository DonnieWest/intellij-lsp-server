package com.ruin.lsp.commands.document.lens

// Can't seem to test this, gutters are never found even after copying the tests in RunLineMarkerTest verbatim

// class CodeLensCommandTestCase : CodeLensCommandTestBase() {
//    @Ignore
//    fun `test finds main function`() =
//        doTest("MainTest.java", """
//            public class MainTest {
//               public static void <caret>foo(String[] args) {
//               }
//               public static void main(String[] args) {
//               }
//            }
//            """.trimIndent(), listOf(
//            CodeLens(range(0,0,0,0), Command(), RunConfigurationData(
//                RunConfigurationDescription("","",""),
//                RunConfigurationState.Run
//            ))
//        ))
// }
