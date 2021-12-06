// This file was automatically generated from Sequence.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleSequence09

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

import arrow.core.rightPadZip

val left = sequenceOf(1, 2).rightPadZip(sequenceOf(3)) { l, r -> l + (r?:0) }    // Result: [4, 2]
val right = sequenceOf(1).rightPadZip(sequenceOf(3, 4)) { l, r -> l + (r?:0) }   // Result: [4]
val both = sequenceOf(1, 2).rightPadZip(sequenceOf(3, 4)) { l, r -> l + (r?:0) } // Result: [4, 6]

fun main() {
  println("left = $left")
  println("right = $right")
  println("both = $both")
}
