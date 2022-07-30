package arrow.core.continuations

import arrow.core.Maybe
import arrow.core.map
import arrow.core.toMaybe
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.orNull
import io.kotest.property.checkAll

class MaybeSpec :
  StringSpec({
    "ensureNotNull in maybe computation" {
      fun square(i: Int): Int = i * i
      checkAll(Arb.int().orNull()) { i: Int? ->
        maybe {
          val ii = i
          ensureNotNull(ii)
          square(ii) // Smart-cast by contract
        } shouldBe i.toMaybe().map(::square)
      }
    }

    "short circuit maybe" {
      maybe {
        val number: Int = "s".length
        ensureNotNull(number.takeIf { it > 1 })
        throw IllegalStateException("This should not be executed")
      } shouldBe Maybe.Nothing
    }

    "ensureNotNull in eager maybe computation" {
      fun square(i: Int): Int = i * i
      checkAll(Arb.int().orNull()) { i: Int? ->
        maybe {
          val ii = i
          ensureNotNull(ii)
          square(ii) // Smart-cast by contract
        } shouldBe i.toMaybe().map(::square)
      }
    }

    "eager short circuit null" {
      maybe.eager {
        val number: Int = "s".length
        ensureNotNull(number.takeIf { it > 1 })
        throw IllegalStateException("This should not be executed")
      } shouldBe Maybe.Nothing
    }
  })
