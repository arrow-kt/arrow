// This file was automatically generated from either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEitherComputations01

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

suspend fun main() {
  either<String, Int> {
    ensure(true) { "" }
    println("ensure(true) passes")
    ensure(false) { "failed" }
    1
  }
  .let(::println)
}
// println: "ensure(true) passes"
// res: Either.Left("failed")
