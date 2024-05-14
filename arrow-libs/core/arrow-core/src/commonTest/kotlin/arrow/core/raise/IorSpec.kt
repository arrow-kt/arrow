package arrow.core.raise

import arrow.core.Either
import arrow.core.Ior
import arrow.core.left
import arrow.core.right
import arrow.core.test.nonEmptyList
import arrow.core.toIorNel
import io.kotest.assertions.throwables.shouldThrow
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
class IorSpec {
  @Test fun accumulates() = runTest {
    ior(String::plus) {
      val one = Ior.Both("Hello", 1).bind()
      val two = Ior.Both(", World!", 2).bind()
      one + two
    } shouldBe Ior.Both("Hello, World!", 3)
  }

  @Test fun accumulatesAndShortCircuitsWithLeft() = runTest {
    ior(String::plus) {
      val one = Ior.Both("Hello", 1).bind()
      val two: Int = Ior.Left(", World!").bind()
      one + two
    } shouldBe Ior.Left("Hello, World!")
  }

  @Test fun accumulatesWithEither() = runTest {
    ior(String::plus) {
      val one = Ior.Both("Hello", 1).bind()
      val two: Int = Either.Left(", World!").bind<Int>()
      one + two
    } shouldBe Ior.Left("Hello, World!")
  }

  @Test fun concurrentArrowIorBind() = runTest {
    checkAll(Arb.nonEmptyList(Arb.int(), range = 0 .. 20)) { xs ->
      ior(List<Int>::plus) {
        xs.mapIndexed { index, s -> async { Ior.Both(listOf(s), index).bind() } }.awaitAll()
      }
        .mapLeft { it.toSet() } shouldBe Ior.Both(xs.toSet(), xs.indices.toList())
    }
  }

  @Test fun accumulatesEagerly() = runTest {
    ior(String::plus) {
      val one = Ior.Both("Hello", 1).bind()
      val two = Ior.Both(", World!", 2).bind()
      one + two
    } shouldBe Ior.Both("Hello, World!", 3)
  }

  @Test fun accumulatesWithEitherEagerly() = runTest {
    ior(String::plus) {
      val one = Ior.Both("Hello", 1).bind()
      val two: Int = Either.Left(", World!").bind<Int>()
      one + two
    } shouldBe Ior.Left("Hello, World!")
  }

  @Test fun accumulatesAndShortCircuits() = runTest {
    ior(String::plus) {
      Ior.Both("Hello", Unit).bind()
      raise(" World")
    } shouldBe Ior.Left("Hello World")
  }

  @Test fun iorRethrowsException() = runTest {
    val boom = RuntimeException("Boom!")
    shouldThrow<RuntimeException> {
      ior(String::plus) {
       throw boom
      }
    }.message shouldBe "Boom!"
  }

  @Test fun recoverWorksAsExpected() = runTest {
    ior(String::plus) {
      val one = recover({
        Ior.Both("Hi", Unit).bind()
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
    ior(String::plus) {
      val one = try {
        recover({
          Ior.Both("Hi", Unit).bind()
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
    ior(String::plus) {
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
    ior(String::plus) {
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

  @Test fun iorNelAccumulates() = runTest {
    iorNel {
      val one = Ior.Both("ErrorOne", 1).toIorNel().bind()
      val two = Ior.Both("ErrorTwo", 2).toIorNel().bind()
      one + two
    } shouldBe Ior.Both(listOf("ErrorOne", "ErrorTwo"), 3)
  }

  @Test fun accumulateErrorManually() {
    ior(String::plus) {
      accumulate("nonfatal")
      "output"
    } shouldBe Ior.Both("nonfatal", "output")
  }

  @Test fun getOrAccumulateRightEither() {
    ior(String::plus) {
      val result = "success".right().getOrAccumulate { "failed" }
      "output: $result"
    } shouldBe Ior.Right("output: success")
  }

  @Test fun getOrAccumulateLeftEither() {
    ior(String::plus) {
      val result = "nonfatal".left().getOrAccumulate { "failed" }
      "output: $result"
    } shouldBe Ior.Both("nonfatal", "output: failed")
  }
}
