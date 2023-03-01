package arrow.core.raise

import arrow.core.None
import arrow.core.some
import arrow.core.toOption
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.orNull
import io.kotest.property.checkAll

class OptionSpec : StringSpec({

  "ensure" {
    checkAll(Arb.boolean(), Arb.int()) { b, i ->
      option {
        ensure(b)
        i
      } shouldBe if (b) i.some() else None
    }
  }

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
    @Suppress("UNREACHABLE_CODE")
    option {
      ensureNotNull<Int>(null)
      throw IllegalStateException("This should not be executed")
    } shouldBe None
  }
})
