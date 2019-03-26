package arrow.effects

import arrow.effects.extensions.fx2.fx.unsafeRun.runBlocking
import arrow.effects.extensions.io.monad.flatMap
import arrow.effects.internal.Platform
import arrow.effects.suspended.fx2.Fx
import arrow.test.UnitSpec
import arrow.test.laws.equalUnderTheLaw
import arrow.unsafe
import io.kotlintest.Result
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.should
import io.kotlintest.shouldBe
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class Fx2Test : UnitSpec() {

  fun mapFusion(depth: Int): Fx<Int> {
    fun Fx<Int>.go(i: Int): Fx<Int> =
      if (i != depth) this.map { it + 1 }.go(i + 1)
      else this

    return Fx.just(0).go(0)
  }

  fun <A> Matcher(failMessage: String? = null, negatedFailMessage: String? = null, p: (A) -> Boolean): io.kotlintest.Matcher<A> = object : io.kotlintest.Matcher<A> {
    override fun test(value: A): Result = Result(
      p(value),
      failMessage ?: "Expected result $value did not pass the test predicate",
      negatedFailMessage ?: "Expected result $value passed the test predicate"
    )
  }

  init {

    "Simple map fusion works" {
      mapFusion(2) should Matcher { fx: Fx<Int> ->
        fx is Fx.Mapped<*, *> && fx.index == 2
      }
    }

    "Simple map fusion should yield correct result" {
      unsafe {
        runBlocking { mapFusion(3) }
      } shouldBe 3
    }

    "1 stack depth deep map fusion" {
      mapFusion(Platform.maxStackDepthSize + 1) should Matcher { fx ->
        fx is Fx.Mapped<*, *> && fx.index == 0 &&
          fx.source is Fx.Mapped<*, *> && fx.source.index == 127 &&
          fx.source.source is Fx.Single
      }
    }

    "1 stack depth deep map fusion should yield correct result" {
      unsafe {
        runBlocking { mapFusion(Platform.maxStackDepthSize + 1) }
      } shouldBe Platform.maxStackDepthSize + 1
    }

    "multiple level stack depth deep map fusion" {
      mapFusion(2 * (Platform.maxStackDepthSize + 1)) should Matcher { fx ->
        fx is Fx.Mapped<*, *> && fx.index == 0 &&
          fx.source is Fx.Mapped<*, *> && fx.source.index == 127 &&
          fx.source.source is Fx.Mapped<*, *> && fx.source.source.index == 127 &&
          fx.source.source.source is Fx.Single
      }
    }

    "Fx should be stack safe" {
      fun Fx<Int>.loop(count: Int): Fx<Int> =
        flatMap { i ->
          if (i == count) Fx.just(i)
          else Fx.just(i + 1).loop(count)
        }

      val count = 10000
      Fx.just(0).loop(count).invoke() shouldBe count
    }

  }

}