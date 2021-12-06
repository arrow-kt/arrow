// This file was automatically generated from Semiring.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleSemiring03

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

import arrow.typeclasses.Semiring

fun main(args: Array<String>) {
  val result =
  //sampleStart
  Semiring.int().run {
     1 + 2
  }
  //sampleEnd
  println(result)
}
