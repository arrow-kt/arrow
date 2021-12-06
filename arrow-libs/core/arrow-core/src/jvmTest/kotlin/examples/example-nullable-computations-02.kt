// This file was automatically generated from nullable.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleNullableComputations02

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

import arrow.core.computations.nullable
import arrow.core.computations.ensureNotNull

suspend fun main() {
  nullable<Int> {
    val x: Int? = 1
    ensureNotNull(x)
    println(x)
    ensureNotNull(null)
  }
  .let(::println)
}
// println: "1"
// res: null
