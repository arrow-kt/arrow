// This file was automatically generated from option.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleOptionComputations01

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

import arrow.core.computations.option

suspend fun main() {
  option<Int> {
    ensure(true)
    println("ensure(true) passes")
    ensure(false)
    1
  }
  .let(::println)
}
// println: "ensure(true) passes"
// res: None
