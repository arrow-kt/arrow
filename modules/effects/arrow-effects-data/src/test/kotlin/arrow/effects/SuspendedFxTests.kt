package arrow.effects


import arrow.effects.extensions.fx.unsafeRun.runBlocking
import arrow.effects.suspended.fx.Fx
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
      fun mapStackSafe(): Fx<Int> =
        (0 until size).fold(Fx.just(0)) { acc, _ -> acc.map { it + 1 } }
      unsafe { runBlocking { mapStackSafe() } } shouldBe size
    }

    "Fx `flatMap` stack safe" {
      val size = 500000
      fun flatMapStackSafe(): Fx<Int> =
        (0 until size).fold(Fx { 0 }) { acc, _ -> acc.flatMap { Fx.just(it + 1) } }
      unsafe { runBlocking { flatMapStackSafe() } } shouldBe size
    }

  }

}