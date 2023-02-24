package arrow.core.raise

import arrow.core.None
import arrow.core.map
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
        ensureNotNull(i)
        square(i) // Smart-cast by contract
      } shouldBe i.toOption().map(::square)
    }
  }

  "short circuit option" {
    option<Any?> {
      val number: Int = "s".length
      ensureNotNull(number.takeIf { it > 1 })
      throw IllegalStateException("This should not be executed")
    } shouldBe None
  }

  "ensureNotNull in eager option computation" {
    fun square(i: Int): Int = i * i
    checkAll(Arb.int().orNull()) { i: Int? ->
      option {
        ensureNotNull(i)
        square(i) // Smart-cast by contract
      } shouldBe i.toOption().map(::square)
    }
  }

  "eager short circuit null" {
    option<Any?> {
      val number: Int = "s".length
      ensureNotNull(number.takeIf { it > 1 })
      throw IllegalStateException("This should not be executed")
    } shouldBe None
  }
})
