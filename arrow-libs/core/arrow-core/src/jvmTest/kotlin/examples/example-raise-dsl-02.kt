// This file was automatically generated from Raise.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleRaiseDsl02

import arrow.core.Either
import arrow.core.Ior
import arrow.core.raise.Effect
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.effect
import arrow.core.raise.ior
import arrow.core.raise.toEither
import arrow.typeclasses.Semigroup
import io.kotest.matchers.shouldBe

fun Raise<String>.failure(): Int = raise("failed")

suspend fun test() {
  val either: Either<String, Int> =
    either { failure() }

  val effect: Effect<String, Int> =
    effect { failure() }

  val ior: Ior<String, Int> =
    ior(String::plus) { failure() }

  either shouldBe Either.Left("failed")
  effect.toEither() shouldBe Either.Left("failed")
  ior shouldBe Ior.Left("failed")
}
