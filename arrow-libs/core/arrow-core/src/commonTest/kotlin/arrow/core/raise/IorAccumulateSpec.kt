package arrow.core.raise

import arrow.core.Either
import arrow.core.Ior
import arrow.core.NonEmptyList
import arrow.core.left
import arrow.core.right
import arrow.core.test.nonEmptyList
import arrow.core.toIorNel
import arrow.core.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlin.test.Test
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.runTest

@Suppress(
  "UNREACHABLE_CODE",
  "IMPLICIT_NOTHING_TYPE_ARGUMENT_IN_RETURN_POSITION",
  "UNUSED_VARIABLE"
)
@OptIn(ExperimentalRaiseAccumulateApi::class)
class IorAccumulateSpec {
  @Test fun accumulates() = runTest {
    iorAccumulate(String::plus) {
      val one = Ior.Both("Hello", 1).bind()
      val two = Ior.Both(", World!", 2).bind()
      one + two
    } shouldBe Ior.Both("Hello, World!", 3)
  }

  @Test fun accumulatesAndShortCircuitsWithLeft() = runTest {
    iorAccumulate(String::plus) {
      val one = Ior.Both("Hello", 1).bind()
      val two: Int = Ior.Left(", World!").bind()
      one + two
    } shouldBe Ior.Left("Hello, World!")
  }

  @Test fun accumulatesWithEither() = runTest {
    iorAccumulate(String::plus) {
      val one = Ior.Both("Hello", 1).bind()
      val two: Int = Either.Left(", World!").bind<Int>()
      one + two
    } shouldBe Ior.Left("Hello, World!")
  }

  @Test fun concurrentArrowIorBind() = runTest {
    checkAll(Arb.nonEmptyList(Arb.int(), range = 0 .. 20)) { xs ->
      iorAccumulate(List<Int>::plus) {
        xs.mapIndexed { index, s -> async { Ior.Both(listOf(s), index).bind() } }.awaitAll()
      }
        .mapLeft { it.toSet() } shouldBe Ior.Both(xs.toSet(), xs.indices.toList())
    }
  }

  @Test fun accumulatesEagerly() = runTest {
    iorAccumulate(String::plus) {
      val one = Ior.Both("Hello", 1).bind()
      val two = Ior.Both(", World!", 2).bind()
      one + two
    } shouldBe Ior.Both("Hello, World!", 3)
  }

  @Test fun accumulatesWithEitherEagerly() = runTest {
    iorAccumulate(String::plus) {
      val one = Ior.Both("Hello", 1).bind()
      val two: Int = Either.Left(", World!").bind<Int>()
      one + two
    } shouldBe Ior.Left("Hello, World!")
  }

  @Test fun accumulatesAndShortCircuits() = runTest {
    iorAccumulate(String::plus) {
      Ior.Both("Hello", Unit).bind()
      raise(" World")
    } shouldBe Ior.Left("Hello World")
  }

  @Test fun iorRethrowsException() = runTest {
    val boom = RuntimeException("Boom!")
    shouldThrow<RuntimeException> {
      iorAccumulate(String::plus) {
       throw boom
      }
    }.message shouldBe "Boom!"
  }

  @Test fun recoverWorksAsExpected() = runTest {
    iorAccumulate(String::plus) {
      val one = recover({
        Ior.Both("H", Unit).bind()
        Ior.Both("i", Unit).bind()
        Ior.Left("Hello").bind()
      }) {
        it shouldBe "Hello"
        1
      }
      val two = Ior.Right(2).bind()
      val three = Ior.Both(", World", 3).bind()
      one + two + three
    } shouldBe Ior.Both("Hi, World", 6)
  }

  @Test fun recoverWithThrow() = runTest {
    iorAccumulate(String::plus) {
      val one = try {
        recover({
          Ior.Both("H", Unit).bind()
          Ior.Both("i", Unit).bind()
          throw RuntimeException("Hello")
        }) {
          unreachable()
        }
      } catch (e: RuntimeException) {
        1
      }
      val two = Ior.Right(2).bind()
      val three = Ior.Both(", World", 3).bind()
      one + two + three
    } shouldBe Ior.Both("Hi, World", 6)
  }

  @Test fun recoverWithRaiseIsNoOp() = runTest {
    iorAccumulate(String::plus) {
      val one: Int =
        recover({
          Ior.Both("Hi", Unit).bind()
          Ior.Left(", Hello").bind()
        }) {
          raise(it)
        }
      val two = Ior.Right(2).bind()
      val three = Ior.Both(", World", 3).bind()
      one + two + three
    } shouldBe Ior.Left("Hi, Hello")
  }

  @Test fun tryCatchRecoverRaise() = runTest {
    iorAccumulate(String::plus) {
      val one = try {
        Ior.Both("Hi", Unit).bind()
        Ior.Left("Hello").bind()
      } catch (e: Throwable) {
        1
      }
      val two = Ior.Right(2).bind()
      val three = Ior.Both(", World", 3).bind()
      one + two + three
    } shouldBe Ior.Both("Hi, World", 6)
  }

  @Test fun tryCatchRecoverRaiseInsideAccumulating() = runTest {
    iorAccumulate(String::plus) {
      accumulating {
        val one = try {
          Ior.Both("Hi", Unit).bind()
          Ior.Left("Hello").bind()
        } catch (e: Throwable) {
          1
        }
        val two = Ior.Right(2).bind()
        val three = Ior.Both(", World", 3).bind()
        one + two + three
      }.value
    } shouldBe Ior.Both("Hi, World", 6)
  }

  @Test fun iorNelAccumulates() = runTest {
    iorAccumulate(NonEmptyList<String>::plus) {
      val one = Ior.Both("ErrorOne", 1).toIorNel().bind()
      val two = Ior.Both("ErrorTwo", 2).toIorNel().bind()
      one + two
    } shouldBe Ior.Both(listOf("ErrorOne", "ErrorTwo"), 3)
  }

  @Test fun accumulateErrorManually() {
    iorAccumulate(String::plus) {
      accumulate("nonfatal")
      "output"
    } shouldBe Ior.Both("nonfatal", "output")
  }

  @Test fun getOrAccumulateRightEither() {
    iorAccumulate(String::plus) {
      val result = "success".right().getOrAccumulate { "failed" }
      "output: $result"
    } shouldBe Ior.Right("output: success")
  }

  @Test fun getOrAccumulateLeftEither() {
    iorAccumulate(String::plus) {
      val result = "nonfatal".left().getOrAccumulate { "failed" }
      "output: $result"
    } shouldBe Ior.Both("nonfatal", "output: failed")
  }

  @Test fun preservesAccumulatedErrorsInAccumulating() {
    iorAccumulate(String::plus) {
      accumulating {
        accumulate("nonfatal")
        "output: failed"
      }.value
    } shouldBe Ior.Both("nonfatal", "output: failed")
  }

  @Test fun nestedAccumulating() {
    iorAccumulate(String::plus) {
      accumulating {
        accumulating { raise("nonfatal") }.value
      }
      "output: failed"
    } shouldBe Ior.Both("nonfatal", "output: failed")
  }
}
