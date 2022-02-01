// This file was automatically generated from Control.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffect04

import arrow.core.Either
import arrow.core.continuations.Effect
import arrow.core.continuations.effect
import arrow.core.identity
import io.kotest.matchers.shouldBe

fun <E, A> Either<E, A>.toCont(): Effect<E, A> = effect {
  fold({ e -> shift(e) }, ::identity)
}

suspend fun main() {
  val either = Either.Left("failed")
  effect<String, Int> {
    val x: Int = either.toCont().bind()
    x
  }.toEither() shouldBe either
}
