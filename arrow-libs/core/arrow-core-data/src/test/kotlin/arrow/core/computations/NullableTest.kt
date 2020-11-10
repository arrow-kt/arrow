package arrow.core.computations

import arrow.core.test.UnitSpec
import io.kotlintest.shouldBe

class NullableTest : UnitSpec() {

  init {
    "simple case" {
      nullable {
        "s".length().invoke()
      } shouldBe 1
    }
    "multiple types" {
      nullable {
        val number = "s".length()
        val string = number.toString()()
        string
      } shouldBe "1"
    }
    "short circuit" {
      nullable {
        val number: Int = "s".length()
        (number.takeIf { it > 1 }?.toString())()
        throw IllegalStateException("This should not be executed")
      } shouldBe null
    }
    "when expression" {
      nullable {
        val number = "s".length()
        val string = when (number) {
          1 -> number.toString()
          else -> null
        }.invoke()
        string
      } shouldBe "1"
    }
    "if expression" {
      nullable {
        val number = "s".length()
        val string = if (number == 1) {
          number.toString()
        } else {
          null
        }.invoke()
        string
      } shouldBe "1"
    }
    "if expression short circuit" {
      nullable {
        val number = "s".length()
        val string = if (number != 1) {
          number.toString()
        } else {
          null
        }()
        string
      } shouldBe null
    }
  }
}
