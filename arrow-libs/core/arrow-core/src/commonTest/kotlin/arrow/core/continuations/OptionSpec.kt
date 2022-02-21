package arrow.core.continuations

import arrow.core.None
import arrow.core.Some
import arrow.core.toOption
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.orNull
import io.kotest.property.checkAll

class OptionSpec : StringSpec({
  "ensureNotNull in option computation" {
    fun square(i: Int): Int = i * i
    checkAll(Arb.int().orNull()) { i: Int? ->
      option {
        val ii = i
        ensureNotNull(ii)
        square(ii) // Smart-cast by contract
      } shouldBe i.toOption().map(::square)
    }
  }

  "short circuit null" {
    option {
      val number: Int = "s".length
      val x = ensureNotNull(number.takeIf { it > 1 })
      x
      throw IllegalStateException("This should not be executed")
    } shouldBe None
  }

  "ensureNotNull in eager option computation" {
    fun square(i: Int): Int = i * i
    checkAll(Arb.int().orNull()) { i: Int? ->
      option {
        val ii = i
        ensureNotNull(ii)
        square(ii) // Smart-cast by contract
      } shouldBe i.toOption().map(::square)
    }
  }

  "eager short circuit null" {
    option.eager {
      val number: Int = "s".length
      val x = ensureNotNull(number.takeIf { it > 1 })
      x
      throw IllegalStateException("This should not be executed")
    } shouldBe None
  }

  "simple case" {
    option.eager {
      "s".length.bind()
    }.orNull() shouldBe 1
  }

  "multiple types" {
    option.eager {
      val number = "s".length
      val string = number.toString().bind()
      string
    }.orNull() shouldBe "1"
  }

  "short circuit" {
    option.eager {
      val number: Int = "s".length
      (number.takeIf { it > 1 }?.toString()).bind()
      throw IllegalStateException("This should not be executed")
    }.orNull() shouldBe null
  }

  "short circuit option" {
    option.eager {
      val number = Some("s".length)
      number.filter { it > 1 }.map(Int::toString).bind()
      throw IllegalStateException("This should not be executed")
    }.orNull() shouldBe null
  }

  "when expression" {
    option.eager {
      val number = "s".length.bind()
      val string = when (number) {
        1 -> number.toString()
        else -> null
      }.bind()
      string
    }.orNull() shouldBe "1"
  }

  "if expression" {
    option.eager {
      val number = "s".length.bind()
      val string = if (number == 1) {
        number.toString()
      } else {
        null
      }.bind()
      string
    }.orNull() shouldBe "1"
  }

  "if expression short circuit" {
    option.eager {
      val number = "s".length.bind()
      val string = if (number != 1) {
        number.toString()
      } else {
        null
      }.bind()
      string
    }.orNull() shouldBe null
  }
})
