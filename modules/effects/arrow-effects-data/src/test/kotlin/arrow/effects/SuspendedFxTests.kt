package arrow.effects


import arrow.effects.extensions.fx.unsafeRun.runBlocking
import arrow.effects.suspended.fx.Fx
import arrow.effects.suspended.fx.flatMap
import arrow.effects.suspended.fx.just
import arrow.effects.suspended.fx.map
import arrow.test.UnitSpec
import arrow.unsafe
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.shouldBe
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.junit.runner.RunWith

@ObsoleteCoroutinesApi
@Suppress("RedundantSuspendModifier")
@RunWith(KotlinTestRunner::class)
class SuspendedFxTests : UnitSpec() {

  init {

    "Fx `map` stack safe" {
      val size = 500000
      suspend fun mapStackSafe(): suspend () -> Int =
        (0 until size).fold(suspend { 0 }) { acc, _ -> acc.map { it + 1 } }
      unsafe { runBlocking { Fx { mapStackSafe()() } } } shouldBe size
    }

    "Fx `flatMap` stack safe" {
      val size = 500000
      suspend fun flatMapStackSafe(): suspend () -> Int =
        (0 until size).fold(suspend { 0 }) { acc, _ -> acc.flatMap { just(it + 1) } }
      unsafe { runBlocking { Fx { flatMapStackSafe()() } } } shouldBe size
    }

  }

}