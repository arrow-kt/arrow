// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither13

import arrow.core.Either

fun potentialThrowingCode(): String = throw RuntimeException("Blow up!")

suspend fun makeSureYourLogicDoesNotHaveSideEffects(): Either<Error, String> =
  Either.catch { potentialThrowingCode() }.mapLeft { Error.SpecificError }
suspend fun main() {
  println("makeSureYourLogicDoesNotHaveSideEffects().isLeft() = ${makeSureYourLogicDoesNotHaveSideEffects().isLeft()}")
}

sealed class Error {
  object SpecificError : Error()
}
