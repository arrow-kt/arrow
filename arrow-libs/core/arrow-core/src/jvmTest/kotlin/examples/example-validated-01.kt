// This file was automatically generated from Validated.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleValidated01

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

fun main(args: Array<String>) {
  //sampleStart
  val f = Validated.lift(String::toUpperCase, Int::inc)
  val res1 = f("test".invalid())
  val res2 = f(1.valid())
  //sampleEnd
  println("res1: $res1")
  println("res2: $res2")
}
