// This file was automatically generated from nonFatalOrThrow.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleNonfatalorthrow01

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

import arrow.*
import arrow.core.*

fun unsafeFunction(i: Int): String =
   when (i) {
        1 -> throw IllegalArgumentException("Non-Fatal")
        2 -> throw OutOfMemoryError("Fatal")
        else -> "Hello"
   }

fun main(args: Array<String>) {
  val nonFatal: Either<Throwable, String> =
  //sampleStart
  try {
     Either.Right(unsafeFunction(1))
  } catch (t: Throwable) {
      Either.Left(t.nonFatalOrThrow())
  }
  //sampleEnd
  println(nonFatal)
}
