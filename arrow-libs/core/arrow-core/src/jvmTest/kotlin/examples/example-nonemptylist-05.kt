// This file was automatically generated from NonEmptyList.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleNonemptylist05

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

import arrow.core.NonEmptyList
import arrow.core.nonEmptyListOf

val nelOne: NonEmptyList<Int> = nonEmptyListOf(1, 2, 3)
val nelTwo: NonEmptyList<Int> = nonEmptyListOf(4, 5)

val value = nelOne.flatMap { one ->
 nelTwo.map { two ->
   one + two
 }
}
fun main() {
 println("value = $value")
}
