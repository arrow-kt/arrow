package arrow.core.computations

import arrow.core.test.UnitSpec
import io.kotlintest.matchers.types.shouldBeTypeOf
import io.kotlintest.shouldBe
import java.lang.RuntimeException

class ResultEffectTest : UnitSpec() {

  private fun successString() = Result.success("success")

  init {
    "simple case" {
      result {
        successString().bind()
      }.getOrThrow() shouldBe "success"
    }
    "multiple types" {
      result {
        val number = "s".length
        val string = Result.success(number.toString()).bind()
        string
      }.getOrThrow() shouldBe "1"
    }
    "binding mapped result" {
      result {
        val number = Result.success("s".length)
        val string = number.map(Int::toString).bind()
        string
      }.getOrThrow() shouldBe "1"
    }
    "short circuit" {
      result {
        val number = "s"
        runCatching { number.toInt() }.bind()
        throw IllegalStateException("This should not be executed")
      }.exceptionOrNull().shouldBeTypeOf<NumberFormatException>()
    }
    "short circuit option" {
      result {
        val number = Result.success("s".length)
        number.mapCatching { error("fail") }.map(Int::toString).bind()
        throw IllegalStateException("This should not be executed")
      }.exceptionOrNull()?.message shouldBe "fail"
    }
    "when expression" {
      result {
        val number = "s".length
        val string = when (number) {
          1 -> Result.success(number.toString())
          else -> Result.failure(RuntimeException())
        }.bind()
        string
      }.getOrThrow() shouldBe "1"
    }
    "if expression" {
      result {
        val number = "s".length
        val string = if (number == 1) {
          Result.success(number.toString())
        } else {
          Result.failure(RuntimeException())
        }.bind()
        string
      }.getOrThrow() shouldBe "1"
    }
    "if expression short circuit" {
      result {
        val number = "s".length
        val string = if (number != 1) {
          Result.success(number.toString())
        } else {
          Result.failure(RuntimeException("fail"))
        }.bind()
        string
      }.exceptionOrNull()?.message shouldBe "fail"
    }
  }
}
