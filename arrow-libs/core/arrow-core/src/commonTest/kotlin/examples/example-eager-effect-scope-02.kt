// This file was automatically generated from EagerEffectScope.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEagerEffectScope02

import arrow.core.Either
import arrow.core.continuations.EagerEffect
import arrow.core.continuations.eagerEffect
import arrow.core.identity
import io.kotest.matchers.shouldBe

fun <E, A> Either<E, A>.toEagerEffect(): EagerEffect<E, A> = eagerEffect {
  fold({ e -> shift(e) }, ::identity)
}

fun main() {
  val either = Either.Left("failed")
  eagerEffect<String, Int> {
    val x: Int = either.toEagerEffect().bind()
    x
  }.toEither() shouldBe either
}
