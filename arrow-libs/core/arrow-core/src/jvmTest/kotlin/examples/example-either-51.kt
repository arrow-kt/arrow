// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither51

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

import arrow.core.Either.*
import arrow.core.leftIfNull

fun main() {
  Right(12).leftIfNull({ -1 })   // Result: Right(12)
  Right(null).leftIfNull({ -1 }) // Result: Left(-1)

  Left(12).leftIfNull({ -1 })    // Result: Left(12)
}
