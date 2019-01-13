package arrow.effects

import arrow.Kind
import arrow.effects.extensions.io.concurrent.concurrent
import arrow.effects.typeclasses.ConcurrentEffects
import arrow.test.UnitSpec
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import org.junit.runner.RunWith

suspend fun <F> ConcurrentEffects<F>.addOne(fa: Kind<F, Int>): Int {
  val (a) = fa
  return a + 1
}

suspend fun <F> ConcurrentEffects<F>.addTwo(fa: Kind<F, Int>): Int {
  val (a) = fa
  return a + 2
}

suspend fun <F> ConcurrentEffects<F>.app(): Int =
  addOne(1.just()) + addTwo(1.just())

@RunWith(KTestJUnitRunner::class)
class EffectsSuspendDSLTests : UnitSpec() {

  init {
    "Suspended algebras can be composed and interpreted" {
      val result: IO<Int> = concurrent { app() }
      result.unsafeRunSync() shouldBe 5
    }
  }

}
