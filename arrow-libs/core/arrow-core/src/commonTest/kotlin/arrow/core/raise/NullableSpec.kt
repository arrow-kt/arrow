package arrow.core.raise

import arrow.core.Either
import arrow.core.Some
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.orNull
import io.kotest.property.checkAll
import kotlin.test.Test
import kotlinx.coroutines.test.runTest

class NullableSpec {
  @Test fun ensureNullInNullableComputation() = runTest {
    checkAll(Arb.boolean(), Arb.int()) { predicate, i ->
      nullable {
        ensure(predicate)
        i
      } shouldBe if (predicate) i else null
    }
  }

  @Test fun ensureNotNullInNullableComputation() = runTest {
    fun square(i: Int): Int = i * i
    checkAll(Arb.int().orNull()) { i: Int? ->
      nullable {
        ensureNotNull(i)
        square(i) // Smart-cast by contract
      } shouldBe i?.let(::square)
    }
  }

  @Test fun shortCircuitNull() = runTest {
    nullable {
      val number: Int = "s".length
      (number.takeIf { it > 1 }?.toString()).bind()
      throw IllegalStateException("This should not be executed")
    } shouldBe null
  }

  @Test fun ensureNotNullShortCircuit() = runTest {
    nullable {
      val number: Int = "s".length
      ensureNotNull(number.takeIf { it > 1 })
      throw IllegalStateException("This should not be executed")
    } shouldBe null
  }

  @Test fun simpleCase() = runTest {
    nullable {
      "s".length.bind()
    } shouldBe 1
  }

  @Test fun multipleTypes() = runTest {
    nullable {
      val number = "s".length
      val string = number.toString().bind()
      string
    } shouldBe "1"
  }

  @Test fun bindingOptionInNullable() = runTest {
    nullable {
      val number = Some("s".length)
      val string = number.map(Int::toString).bind()
      string
    } shouldBe "1"
  }

  @Test fun bindingEitherInNullableIgnoreErrors() = runTest {
    nullable {
      val number = Either.Right("s".length) as Either<Boolean, Int>
      val string = ignoreErrors { number.map(Int::toString).bind() }
      string
    } shouldBe "1"
  }

  @Test fun bindingEitherInNullable() = runTest {
    nullable {
      val number = Either.Right("s".length)
      val string = number.map(Int::toString).bind()
      string
    } shouldBe "1"
  }

  @Test fun shortCircuitOption() = runTest {
    nullable {
      val number = Some("s".length)
      number.filter { it > 1 }.map(Int::toString).bind()
      throw IllegalStateException("This should not be executed")
    } shouldBe null
  }

  @Test fun whenExpression() = runTest {
    nullable {
      val number = "s".length.bind()
      val string = when (number) {
        1 -> number.toString()
        else -> null
      }.bind()
      string
    } shouldBe "1"
  }

  @Test fun ifExpression() = runTest {
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

  @Test fun ifExpressionShortCircuit() = runTest {
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

  @Test fun eitherOfNothingAndSomethingCanBeBound() = runTest {
    nullable {
      val either: Either<Nothing, Int> = Either.Right(4)
      either.bind() + 3
    } shouldBe 7
  }

  @Test fun recoverWorksAsExpected() = runTest {
    nullable {
      val one: Int = recover({ null.bind<Int>() }) { 1 }
      val two = 2.bind()
      one + two
    } shouldBe 3
  }
}
