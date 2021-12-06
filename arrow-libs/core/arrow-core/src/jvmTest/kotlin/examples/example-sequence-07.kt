// This file was automatically generated from Sequence.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleSequence07

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

import arrow.core.padZip

val padRight = sequenceOf(1, 2).padZip(sequenceOf("a"))       // Result: [Pair(1, "a"), Pair(2, null)]
val padLeft = sequenceOf(1).padZip(sequenceOf("a", "b"))      // Result: [Pair(1, "a"), Pair(null, "b")]
val noPadding = sequenceOf(1, 2).padZip(sequenceOf("a", "b")) // Result: [Pair(1, "a"), Pair(2, "b")]

fun main() {
  println("padRight = $padRight")
  println("padLeft = $padLeft")
  println("noPadding = $noPadding")
}
