// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither13

import arrow.*
import arrow.core.*
import arrow.core.coroutines.*
import kotlinx.coroutines.*
import io.kotest.matchers.collections.*
import io.kotest.assertions.*
import io.kotest.matchers.*
import io.kotest.matchers.types.*
import kotlin.coroutines.cancellation.CancellationException
import io.kotest.property.*
import io.kotest.property.arbitrary.*
import arrow.core.test.generators.*

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
