// This file was automatically generated from Cont.kt by Knit tool. Do not edit.
package example.test

import org.junit.Test
import kotlinx.knit.test.*

class ContTest {
    @Test
    fun testExampleCont01() {
        captureOutput("ExampleCont01") { example.exampleCont01.main() }.verifyOutputLines(
            "6",
            "Option was empty"
        )
    }

    @Test
    fun testExampleCont02() {
        captureOutput("ExampleCont02") { example.exampleCont02.main() }.verifyOutputLines(
            "Hello, World!",
            "1000"
        )
    }
}
