// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither47

import arrow.core.Either
import arrow.core.getOrElse
import io.kotest.matchers.shouldBe

fun test() {
  Either.Left(12).getOrElse { it + 5 } shouldBe 17
}
