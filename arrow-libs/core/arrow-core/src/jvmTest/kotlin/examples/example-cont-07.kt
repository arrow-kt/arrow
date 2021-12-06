// This file was automatically generated from Control.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleCont07

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

private val default = "failed"
suspend fun test() = checkAll(Arb.result(Arb.int())) { result ->
  control<String, Int> {
    val x: Int = result.bind { _: Throwable -> default }
    x
  }.fold({ default }, ::identity) shouldBe result.getOrElse { default }
}
