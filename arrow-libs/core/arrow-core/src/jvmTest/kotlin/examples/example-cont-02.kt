// This file was automatically generated from Control.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleCont02

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

suspend fun test() {
  val shift = control<String, Int> {
    shift("Hello, World!")
  }.fold({ str: String -> str }, { int -> int.toString() })
  shift shouldBe "Hello, World!"

  val res = control<String, Int> {
    1000
  }.fold({ str: String -> str.length }, { int -> int })
  res shouldBe 1000
}
