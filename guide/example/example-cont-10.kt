// This file was automatically generated from Cont.kt by Knit tool. Do not edit.
package example.exampleCont10

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

suspend fun test() = checkAll(Arb.string(), Arb.int().orNull()) { failure, int: Int? ->
  cont<String, Int> {
    ensureNotNull(int) { failure }
  }.toEither() shouldBe (int?.right() ?: failure.left())
}
