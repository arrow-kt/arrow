// This file was automatically generated from Cont.kt by Knit tool. Do not edit.
package example.exampleCont01

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

suspend fun test() {
  cont<String, Int> {
    val x = Either.Right(1).bind()
    val y = Validated.Valid(2).bind()
    val z = Option(3).bind { "Option was empty" }
    x + y + z
  }.fold({ fail("Shift can never be the result") }, { it shouldBe 6 })

  cont<String, Int> {
    val x = Either.Right(1).bind()
    val y = Validated.Valid(2).bind()
    val z: Int = None.bind { "Option was empty" }
    x + y + z
  }.fold({ it shouldBe "Option was empty" }, { fail("Int can never be the result") })
}
