// This file was automatically generated from Option.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleOption18

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
import arrow.core.Some
import arrow.core.none
import arrow.core.Option

suspend fun value(): Option<Int> =
 option {
   val x = none<Int>().bind()
   val y = Some(1 + x).bind()
   val z = Some(1 + y).bind()
   x + y + z
 }
suspend fun main() {
 println(value())
}
