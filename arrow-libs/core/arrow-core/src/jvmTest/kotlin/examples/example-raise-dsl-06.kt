// This file was automatically generated from Raise.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleRaiseDsl06

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.recover
import io.kotest.matchers.shouldBe

fun test() {
  recover({ raise("failed") }) { str -> str.length } shouldBe 6

  either<Int, String> {
    recover({ raise("failed") }) { str -> raise(-1) }
  } shouldBe Either.Left(-1)
}
