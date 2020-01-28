package arrow.free

import arrow.free.extensions.fx
import arrow.test.UnitSpec
import io.kotlintest.shouldBe

class TrampolineTest : UnitSpec() {

  init {
    "trampoline over 10000 should return false and not break the stack" {
      odd(10000).runT() shouldBe false
    }

    "trampoline over 10001 should return true and not break the stack" {
      odd(10001).runT() shouldBe true
    }

    "trampoline should support fx syntax" {
      tryfxsyntax(10000).runT() shouldBe true
    }
  }

  private fun odd(n: Int): TrampolineF<Boolean> {
    return when (n) {
      0 -> Trampoline.done(false)
      else -> {
        Trampoline.defer { even(n - 1) }
      }
    }
  }

  private fun even(n: Int): TrampolineF<Boolean> {
    return when (n) {
      0 -> Trampoline.done(true)
      else -> {
        Trampoline.defer { odd(n - 1) }
      }
    }
  }

  private fun tryfxsyntax(n: Int): TrampolineF<Boolean> =
    TrampolineF.fx {
      val x = Trampoline.defer { odd(10000) }.bind()
      val y = Trampoline.defer { even(10000) }.bind()
      x xor y
    }
}
