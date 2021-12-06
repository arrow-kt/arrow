// This file was automatically generated from memoization.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleMemoization01

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

import arrow.core.memoize
fun someWorkIntensiveFunction(someParam: Int): String = "$someParam"

fun main() {
  //sampleStart
  val memoizedF = ::someWorkIntensiveFunction.memoize()

  // The first invocation will store the argument and result in a cache inside the `memoizedF` reference.
  val value1 = memoizedF(42)
  // This second invocation won't really call the `someWorkIntensiveFunction` function
  //but retrieve the result from the previous invocation instead.
  val value2 = memoizedF(42)

  //sampleEnd
  println("$value1 $value2")
}
