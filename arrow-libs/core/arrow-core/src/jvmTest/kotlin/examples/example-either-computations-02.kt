// This file was automatically generated from either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEitherComputations02

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

import arrow.core.computations.either
import arrow.core.computations.ensureNotNull

suspend fun main() {
  either<String, Int> {
    val x: Int? = 1
    ensureNotNull(x) { "passes" }
    println(x)
    ensureNotNull(null) { "failed" }
  }
  .let(::println)
}
// println: "1"
// res: Either.Left("failed")
