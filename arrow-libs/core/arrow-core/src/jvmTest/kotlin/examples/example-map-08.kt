// This file was automatically generated from map.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleMap08

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
   mapOf("first" to "A:1", "second" to "B:2", "third" to "C:3").unzip { (_, e) ->
     e.split(":").let {
       it.first() to it.last()
     }
   }
  //sampleEnd
  println(result)
}
