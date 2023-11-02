package arrow.core.raise

import arrow.core.None
import arrow.core.Some
import arrow.core.some
import arrow.core.toOption
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.orNull
import io.kotest.property.checkAll
import kotlin.test.Test
import kotlinx.coroutines.test.runTest

class OptionSpec {

  @Test fun ensure() = runTest {
    checkAll(Arb.boolean(), Arb.int()) { b, i ->
      option {
        ensure(b)
        i
      } shouldBe if (b) i.some() else None
    }
  }

  @Test fun ensureNotNullInOptionComputation() = runTest {
    fun square(i: Int): Int = i * i
    checkAll(Arb.int().orNull()) { i: Int? ->
      option {
        ensureNotNull(i)
        square(i) // Smart-cast by contract
      } shouldBe i.toOption().map(::square)
    }
  }

  @Test fun shortCircuitOption() = runTest {
    @Suppress("UNREACHABLE_CODE")
    option {
      ensureNotNull<Int>(null)
      throw IllegalStateException("This should not be executed")
    } shouldBe None
  }

  @Test fun RecoverWorksAsExpected() = runTest {
    option {
      val one: Int = recover({ None.bind<Int>() }) { 1 }
      val two = Some(2).bind()
      one + two
    } shouldBe Some(3)
  }
}
