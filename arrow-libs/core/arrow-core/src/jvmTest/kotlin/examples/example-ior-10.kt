// This file was automatically generated from Ior.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIor10

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

import arrow.core.Ior

fun main() {
  val right = Ior.Right(12).orNull()         // Result: 12
  val left = Ior.Left(12).orNull()           // Result: null
  val both = Ior.Both(12, "power").orNull()  // Result: "power"

  println("right = $right")
  println("left = $left")
  println("both = $both")
}
