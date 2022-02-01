// This file was automatically generated from Control.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleControl04

import arrow.core.Either
import arrow.core.continuations.Effect
import arrow.core.continuations.control
import arrow.core.identity
import io.kotest.matchers.shouldBe

fun <E, A> Either<E, A>.toCont(): Effect<E, A> = control {
  fold({ e -> shift(e) }, ::identity)
}

suspend fun main() {
  val either = Either.Left("failed")
  control<String, Int> {
    val x: Int = either.toCont().bind()
    x
  }.toEither() shouldBe either
}
