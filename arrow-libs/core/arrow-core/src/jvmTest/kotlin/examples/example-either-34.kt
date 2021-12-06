// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither34

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

import arrow.core.*

fun main() {
  fun possiblyFailingOperation(): Either.Right<Int> =
    Either.Right(1)
  //sampleStart
  val result: Either<Exception, Int> = possiblyFailingOperation()
  result.fold(
       { println("operation failed with $it") },
       { println("operation succeeded with $it") }
  )
  //sampleEnd
}
