// This file was automatically generated from predef-test.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.test.examples.examplePredefTest01

import arrow.fx.coroutines.assertThrowable

fun main() {
  val exception = assertThrowable<IllegalArgumentException> {
    throw IllegalArgumentException("Talk to a duck")
  }
  require("Talk to a duck" == exception.message)
}
