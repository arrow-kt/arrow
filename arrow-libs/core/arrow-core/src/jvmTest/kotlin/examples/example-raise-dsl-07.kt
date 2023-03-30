// This file was automatically generated from Raise.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleRaiseDsl07

import arrow.core.raise.recover
import arrow.core.raise.Raise
import io.kotest.matchers.shouldBe

fun test() {
  recover(
    { raise("failed") },
    { str -> str.length }
  ) { t -> t.message ?: -1 } shouldBe 6

  fun Raise<String>.boom(): Int = throw RuntimeException("BOOM")

  recover(
    { boom() },
    { str -> str.length }
  ) { t -> t.message?.length ?: -1 } shouldBe 4
}
