// This file was automatically generated from Iterable.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIterable03

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

val left = listOf(1, 2).leftPadZip(listOf("a")) { l, r -> l to r }      // Result: [Pair(1, "a")]
val right = listOf(1).leftPadZip(listOf("a", "b")) { l, r -> l to r }   // Result: [Pair(1, "a"), Pair(null, "b")]
val both = listOf(1, 2).leftPadZip(listOf("a", "b")) { l, r -> l to r } // Result: [Pair(1, "a"), Pair(2, "b")]

fun main() {
  println("left = $left")
  println("right = $right")
  println("both = $both")
}
