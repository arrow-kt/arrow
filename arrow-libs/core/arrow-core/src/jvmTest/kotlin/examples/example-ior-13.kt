// This file was automatically generated from Ior.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIor13

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
  Ior.Both(5, 12).exists { it > 10 } // Result: true
  Ior.Right(12).exists { it > 10 }   // Result: true
  Ior.Right(7).exists { it > 10 }    // Result: false
  val left: Ior<Int, Int> = Ior.Left(12)
  left.exists { it > 10 }      // Result: false
}
