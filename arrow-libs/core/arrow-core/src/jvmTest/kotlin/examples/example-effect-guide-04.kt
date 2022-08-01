// This file was automatically generated from Effect.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffectGuide04

import arrow.core.Either
import arrow.core.continuations.Effect
import arrow.core.continuations.effect
import arrow.core.continuations.handleError
import arrow.core.continuations.handleErrorWith
import arrow.core.continuations.redeem
import arrow.core.continuations.attempt
import arrow.core.continuations.toEither
import arrow.core.identity
import io.kotest.matchers.shouldBe

val failed: Effect<String, Int> =
  effect { shift("failed") }

val resolved: Effect<Nothing, Int> =
  failed.handleError { it.length }

val newError: Effect<List<Char>, Int> =
  failed.handleErrorWith { str ->
    effect { shift(str.reversed().toList()) }
  }

val redeemed: Effect<Nothing, Int> =
  failed.redeem({ str -> str.length }, ::identity)

val captured: Effect<String, Result<Int>> =
  effect<String, Int> { 1 }.attempt()

suspend fun main() {
  failed.toEither() shouldBe Either.Left("failed")
  resolved.toEither() shouldBe Either.Right(6)
  newError.toEither() shouldBe Either.Left(listOf('d', 'e', 'l', 'i', 'a', 'f'))
  redeemed.toEither() shouldBe Either.Right(6)
  captured.toEither() shouldBe Either.Right(Result.success(1))
}
