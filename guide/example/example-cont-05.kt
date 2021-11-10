// This file was automatically generated from Cont.kt by Knit tool. Do not edit.
package example.exampleCont05

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

suspend fun test() = checkAll(Arb.either(Arb.string(), Arb.int())) { either ->
  cont<String, Int> {
    val x: Int = either.bind()
    x
  }.toEither() shouldBe either
}
