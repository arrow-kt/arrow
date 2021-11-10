// This file was automatically generated from README.md by Knit tool. Do not edit.
package example.exampleReadme10

import arrow.*
import arrow.core.*
import arrow.fx.coroutines.*
import kotlinx.coroutines.*
import io.kotest.matchers.collections.*
import io.kotest.assertions.*
import io.kotest.matchers.*
import io.kotest.matchers.types.*
import kotlin.coroutines.cancellation.CancellationException
import io.kotest.property.*
import io.kotest.property.arbitrary.*
import arrow.core.test.generators.*

suspend fun test() {
  coroutineScope {
    cont<Int, String> {
      val fa = async<String> { shift(1) }
      val fb = async<String> { shift(2) }
      fa.await() + fb.await()
    }.fold(::identity, ::identity) shouldBeIn listOf(1, 2)
  }
}
