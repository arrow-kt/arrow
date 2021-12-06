// This file was automatically generated from Control.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleCont04

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

fun <E, A> Either<E, A>.toCont(): Control<E, A> = control {
  fold({ e -> shift(e) }, ::identity)
}

suspend fun test() = checkAll(Arb.either(Arb.string(), Arb.int())) { either ->
  control<String, Int> {
    val x: Int = either.toCont().bind()
    x
  }.toEither() shouldBe either
}
