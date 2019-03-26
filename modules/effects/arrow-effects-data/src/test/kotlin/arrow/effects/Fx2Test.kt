package arrow.effects

import arrow.effects.extensions.fx2.fx.unsafeRun.runBlocking
import arrow.effects.internal.Platform
import arrow.effects.suspended.fx2.Fx
import arrow.test.UnitSpec
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
        fx is Fx.Single && fx.index == 2
      }
    }

    "Simple map fusion should yield correct result" {
      unsafe {
        runBlocking { mapFusion(3) }
      } shouldBe 3
    }

    "1 stack depth deep map fusion" {
      mapFusion(Platform.maxStackDepthSize + 1) should Matcher { fx ->
        fx is Fx.FlatMap<*, *> && fx.left is Fx.Single && fx.left.index == 127
      }
    }

    "1 stack depth deep map fusion should yield correct result" {
      unsafe {
        runBlocking { mapFusion(Platform.maxStackDepthSize + 1) }
      } shouldBe Platform.maxStackDepthSize + 1
    }

    "multiple level stack depth deep map fusion" {
      mapFusion(2 * (Platform.maxStackDepthSize + 1)) should Matcher { fx ->
        fx is Fx.FlatMap<*, *> && fx.left is Fx.FlatMap<*, *>
      }
    }

  }

}