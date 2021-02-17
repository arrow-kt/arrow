package arrow.core.computations

import arrow.core.Some
import arrow.core.test.UnitSpec
import io.kotlintest.shouldBe

class NullableTest : UnitSpec() {

  init {
    "simple case" {
      nullable {
        "s".length.bind()
      } shouldBe 1
    }
    "multiple types" {
      nullable {
        val number = "s".length
        val string = number.toString().bind()
        string
      } shouldBe "1"
    }
    "binding option in nullable" {
      nullable {
        val number = Some("s".length)
        val string = number.map(Int::toString).bind()
        string
      } shouldBe "1"
    }
    "short circuit" {
      nullable {
        val number: Int = "s".length
        (number.takeIf { it > 1 }?.toString()).bind()
        throw IllegalStateException("This should not be executed")
      } shouldBe null
    }
    "short circuit option" {
      nullable {
        val number = Some("s".length)
        number.filter { it > 1 }.map(Int::toString).bind()
        throw IllegalStateException("This should not be executed")
      } shouldBe null
    }
    "when expression" {
      nullable {
        val number = "s".length.bind()
        val string = when (number) {
          1 -> number.toString()
          else -> null
        }.bind()
        string
      } shouldBe "1"
    }
    "if expression" {
      nullable {
        val number = "s".length.bind()
        val string = if (number == 1) {
          number.toString()
        } else {
          null
        }.bind()
        string
      } shouldBe "1"
    }
    "if expression short circuit" {
      nullable {
        val number = "s".length.bind()
        val string = if (number != 1) {
          number.toString()
        } else {
          null
        }.bind()
        string
      } shouldBe null
    }
  }
}
