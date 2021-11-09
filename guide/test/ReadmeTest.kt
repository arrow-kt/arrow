// This file was automatically generated from README.md by Knit tool. Do not edit.
package example.test

import org.junit.Test
import kotlinx.knit.test.*

class ReadmeTest {
    @Test
    fun testExampleReadme02() {
        captureOutput("ExampleReadme02") { example.exampleReadme02.main() }.verifyOutputLines(
            "Either.Left(EmptyPath)",
            "Validated.Invalid(FileNotFound(path=not-found))",
            "Ior.Left(FileNotFound(path=gradle.properties))",
            "Option.None",
            "null"
        )
    }

    @Test
    fun testExampleReadme04() {
        captureOutput("ExampleReadme04") { example.exampleReadme04.main() }.verifyOutputLines(
            "Either.Left(failed)",
            "Either.Right(6)",
            "Either.Left([d, e, l, i, a, f])",
            "Either.Right(6)",
            "Either.Right(Failure(java.lang.RuntimeException: Boom))"
        )
    }
}
