// This file was automatically generated from README.md by Knit tool. Do not edit.
package example.exampleReadme12

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

suspend fun test() = checkAll(Arb.string(), Arb.string(), Arb.int()) { errorA, errorB, int ->
  cont<String, Int> {
    coroutineScope<Int> {
      launch { shift(errorA) }
      launch { shift(errorB) }
      int
    }
  }.fold({ fail("Shift can never finish") }, { it shouldBe int })
}
