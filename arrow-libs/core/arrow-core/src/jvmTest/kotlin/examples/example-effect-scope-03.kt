// This file was automatically generated from EffectScope.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffectScope03

import arrow.core.Either
import arrow.core.continuations.EagerEffect
import arrow.core.continuations.eagerEffect
import arrow.core.continuations.effect
import arrow.core.continuations.toEither
import arrow.core.identity
import io.kotest.matchers.shouldBe

suspend fun <E, A> Either<E, A>.toEagerEffect(): EagerEffect<E, A> = eagerEffect {
  fold({ e -> shift(e) }, ::identity)
}

suspend fun main() {
  val either = Either.Left("failed")
  effect<String, Int> {
    val x: Int = either.toEagerEffect().bind()
    x
  }.toEither() shouldBe either
}
