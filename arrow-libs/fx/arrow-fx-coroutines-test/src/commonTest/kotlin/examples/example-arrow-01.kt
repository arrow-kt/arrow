// This file was automatically generated from predef-test.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.test.examples.exampleArrow01

import io.kotest.assertions.throwables.shouldThrow

val exception = shouldThrow<IllegalArgumentException> {
    throw IllegalArgumentException("Talk to a duck")
}
assertEquals("Talk to a duck", exception.message)
