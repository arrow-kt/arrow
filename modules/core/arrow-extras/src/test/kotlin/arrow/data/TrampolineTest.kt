package arrow.data

import arrow.free.Trampoline
import arrow.free.TrampolineF
import arrow.free.runT
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith
import arrow.test.UnitSpec
import io.kotlintest.shouldBe

@RunWith(KotlinTestRunner::class)
class TrampolineTest : UnitSpec() {

  init {
    "trampoline over 10000 should return false and not break the stack" {
      odd(10000).runT() shouldBe false
    }

    "trampoline over 10001 should return true and not break the stack" {
      odd(10001).runT() shouldBe true
    }
  }

  fun odd(n: Int): TrampolineF<Boolean> {
    return when (n) {
      0 -> Trampoline.done(false)
      else -> {
        Trampoline.defer { even(n - 1) }
      }
    }
  }

  fun even(n: Int): TrampolineF<Boolean> {
    return when (n) {
      0 -> Trampoline.done(true)
      else -> {
        Trampoline.defer { odd(n - 1) }
      }
    }
  }
}
