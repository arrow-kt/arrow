package arrow.core.raise

import arrow.core.*
import arrow.core.test.any
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.orNull
import io.kotest.property.checkAll
import kotlin.test.Test
import kotlinx.coroutines.test.runTest

@Suppress("IMPLICIT_NOTHING_TYPE_ARGUMENT_IN_RETURN_POSITION")
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
    option {
      ensureNotNull<Int>(null)
      throw IllegalStateException("This should not be executed")
    } shouldBe None
  }

  @Test fun raisingInIgnoreErrorsReturnsNone() = runTest {
    checkAll(Arb.any()) { a ->
      option {
        ignoreErrors { raise(a) }
      } shouldBe None
    }
  }

  @Test fun RecoverWorksAsExpected() = runTest {
    option {
      val one: Int = recover({ None.bind<Int>() }) { 1 }
      val two = Some(2).bind()
      one + two
    } shouldBe Some(3)
  }
}
