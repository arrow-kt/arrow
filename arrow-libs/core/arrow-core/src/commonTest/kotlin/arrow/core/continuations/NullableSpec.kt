package arrow.core.continuations

import arrow.core.Some
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.orNull
import io.kotest.property.checkAll

class NullableSpec : StringSpec({
  "ensure null in nullable computation" {
    checkAll(Arb.boolean(), Arb.int()) { predicate, i ->
      nullable<Any?, Int> {
        ensure(predicate)
        i
      } shouldBe if (predicate) i else null
    }
  }

  "ensureNotNull in nullable computation" {
    fun square(i: Int): Int = i * i
    checkAll(Arb.int().orNull()) { i: Int? ->
      nullable<Any?, Int> {
        val ii = i
        ensureNotNull(ii)
        square(ii) // Smart-cast by contract
      } shouldBe i?.let(::square)
    }
  }

  "short circuit null" {
    nullable<Any?, Nothing> {
      val number: Int = "s".length
      val x = ensureNotNull(number.takeIf { it > 1 })
      x
      throw IllegalStateException("This should not be executed")
    } shouldBe arrow.core.None
  }

  "simple case" {
    nullable<Any?, Int> {
      "s".length.bind()
    } shouldBe 1
  }

  "multiple types" {
    nullable<Any?, String> {
      val number = "s".length
      val string = number.toString().bind()
      string
    } shouldBe "1"
  }

  "binding option in nullable" {
    nullable<Any?, String> {
      val number = Some("s".length)
      val string = number.map(Int::toString).bind()
      string
    } shouldBe "1"
  }

  "short circuit null" {
    nullable<Any?, Nothing> {
      val number: Int = "s".length
      (number.takeIf { it > 1 }?.toString()).bind()
      throw IllegalStateException("This should not be executed")
    } shouldBe null
  }

  "short circuit option" {
    nullable<Any?, Nothing> {
      val number = Some("s".length)
      number.filter { it > 1 }.map(Int::toString).bind()
      throw IllegalStateException("This should not be executed")
    } shouldBe null
  }

  "when expression" {
    nullable<Any?, String> {
      val number = "s".length.bind()
      val string = when (number) {
        1 -> number.toString()
        else -> null
      }.bind()
      string
    } shouldBe "1"
  }

  "if expression" {
    nullable<Any?, String> {
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
    nullable<Any?, String> {
      val number = "s".length.bind()
      val string = if (number != 1) {
        number.toString()
      } else {
        null
      }.bind()
      string
    } shouldBe null
  }
})
