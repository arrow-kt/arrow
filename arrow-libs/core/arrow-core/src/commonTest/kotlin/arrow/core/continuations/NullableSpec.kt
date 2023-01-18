package arrow.core.continuations

import arrow.core.Either
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
      nullable {
        ensure(predicate)
        i
      } shouldBe if (predicate) i else null
    }
  }

  "ensureNotNull in nullable computation" {
    fun square(i: Int): Int = i * i
    checkAll(Arb.int().orNull()) { i: Int? ->
      nullable {
        ensureNotNull(i)
        square(i) // Smart-cast by contract
      } shouldBe i?.let(::square)
    }
  }

  "short circuit null" {
    nullable {
      val number: Int = "s".length
      (number.takeIf { it > 1 }?.toString()).bind()
      throw IllegalStateException("This should not be executed")
    } shouldBe null
  }

  "ensureNotNull short circuit" {
    nullable {
      val number: Int = "s".length
      val x = ensureNotNull(number.takeIf { it > 1 })
      x
      throw IllegalStateException("This should not be executed")
    } shouldBe null
  }

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

  "Either<Nothing, A> can be bind" {
    nullable {
      val either: Either<Nothing, Int> = Either.Right(4)
      either.bind() + 3
    } shouldBe 7
  }
})
