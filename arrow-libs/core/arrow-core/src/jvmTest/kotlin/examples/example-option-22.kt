// This file was automatically generated from Option.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleOption22

import arrow.core.None
import arrow.core.Some
import io.kotest.matchers.shouldBe

fun test() {
  Some(12).getOrNull() shouldBe 12
  None.getOrNull() shouldBe null
}
