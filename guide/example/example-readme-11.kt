// This file was automatically generated from README.md by Knit tool. Do not edit.
package example.exampleReadme11

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

suspend fun test() = checkAll(Arb.string(), Arb.string()) { errorA, errorB ->
  coroutineScope {
    cont<String, Int> {
      val fa = async<Int> { shift(errorA) }
      val fb = async<Int> { shift(errorB) }
      fa.await() + fb.await()
    }.fold({ error -> error shouldBeIn listOf(errorA, errorB) }, { fail("Int can never be the result") }) 
  }
}
