// This file was automatically generated from Cont.kt by Knit tool. Do not edit.
package example.test

import io.kotest.core.spec.style.StringSpec
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class ContTest : StringSpec({
    "ExampleCont01".config(timeout= Duration.seconds(1)) {
      example.exampleCont01.test()
    }

    "ExampleCont02".config(timeout= Duration.seconds(1)) {
      example.exampleCont02.test()
    }

    "ExampleCont03".config(timeout= Duration.seconds(1)) {
      example.exampleCont03.test()
    }

})
