// This file was automatically generated from map.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleMap06

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
  val result =
     mapOf("1" to 1, "2" to 2, "3" to 3)
       .unalign { it.leftIor() }
  //sampleEnd
  println(result)
}
