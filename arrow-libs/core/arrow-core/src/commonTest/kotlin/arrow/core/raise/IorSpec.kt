package arrow.core.raise

import arrow.core.Either
import arrow.core.Ior
import arrow.core.bothIor
import arrow.core.fold
import arrow.core.left
import arrow.core.leftIor
import arrow.core.right
import arrow.core.rightIor
import arrow.core.shouldThrow
import arrow.core.test.ior
import arrow.core.test.nonEmptyList
import arrow.core.test.nonEmptySet
import arrow.core.toIorNel
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@Suppress(
  "UNREACHABLE_CODE",
  "IMPLICIT_NOTHING_TYPE_ARGUMENT_IN_RETURN_POSITION",
  "UNUSED_VARIABLE",
)
class IorSpec {

  val range = 0..3
  val nelRange = 1..4
  val iorArb = Arb.ior(Arb.string(range), Arb.int())
  val iterations = 20

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
    checkAll(Arb.nonEmptyList(Arb.int(), range = 0..20)) { xs ->
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
    ior(String::plus) {
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

  @Test
  fun bindAllIterable() = runTest {
    checkAll(iterations, Arb.list(iorArb, range)) { a ->
      val expected =
        a.fold(listOf<Int>().rightIor(), iorFold(String::plus))

      ior(String::plus) {
        a.bindAll()
      } shouldBe expected
    }
  }

  @Test
  fun bindAllNel() = runTest {
    checkAll(iterations, Arb.nonEmptyList(iorArb, nelRange)) { a ->
      val expected =
        a.fold(listOf<Int>().rightIor(), iorFold(String::plus))

      ior(String::plus) {
        a.bindAll()
      } shouldBe expected
    }
  }

  @Test
  fun bindAllNes() = runTest {
    checkAll(iterations, Arb.nonEmptySet(iorArb, nelRange)) { a ->
      val expected =
        a.fold(listOf<Int>().rightIor(), iorFold(String::plus))
          .map { l -> l.toSet() }

      ior(String::plus) {
        a.bindAll()
      } shouldBe expected
    }
  }

  @Test
  fun bindAllMap() = runTest {
    checkAll(iterations, Arb.map(Arb.int(), iorArb, range.first, range.last)) { a ->
      val expected = a.fold(
        emptyMap<Int, Int>().rightIor(),
        iorMapFold(String::plus),
      )

      ior(String::plus) {
        a.bindAll()
      } shouldBe expected
    }
  }

  private fun <E, V> iorFold(
    combine: (E, E) -> E,
  ): (Ior<E, List<V>>, Ior<E, V>)
  -> Ior<E, List<V>> = { acc, rhs ->
    when (acc) {
      is Ior.Left -> acc
      is Ior.Right -> {
        when (rhs) {
          is Ior.Left -> rhs
          is Ior.Right -> (acc.value + rhs.value).rightIor()
          is Ior.Both -> (rhs.leftValue to (acc.value + rhs.rightValue)).bothIor()
        }
      }
      is Ior.Both -> {
        when (rhs) {
          is Ior.Left -> (combine(acc.leftValue, rhs.value)).leftIor()
          is Ior.Right -> (acc.leftValue to (acc.rightValue + rhs.value)).bothIor()
          is Ior.Both -> (combine(acc.leftValue, rhs.leftValue) to (acc.rightValue + rhs.rightValue)).bothIor()
        }
      }
    }
  }

  private fun <E, V> iorMapFold(
    combine: (E, E) -> E,
  ): (Ior<E, Map<V, V>>, Map.Entry<V, Ior<E, V>>)
  -> Ior<E, Map<V, V>> = { acc, rhs ->
    when (acc) {
      is Ior.Left -> acc
      is Ior.Right -> {
        when (rhs.value) {
          is Ior.Left -> (rhs.value as Ior.Left<E>).value.leftIor()
          is Ior.Right -> {
            val p = rhs.key to (rhs.value as Ior.Right<V>).value
            acc.value.plus(p).rightIor()
          }
          is Ior.Both -> {
            val (rhsL, rhsR) = rhs.value as Ior.Both<E, V>
            val p = rhs.key to rhsR
            val r = acc.value.plus(p)
            (rhsL to r).bothIor()
          }
        }
      }
      is Ior.Both -> {
        when (rhs.value) {
          is Ior.Left -> {
            val l = (rhs.value as Ior.Left<E>).value
            combine(acc.leftValue, l).leftIor()
          }
          is Ior.Right -> {
            val p = rhs.key to (rhs.value as Ior.Right<V>).value
            val r = acc.rightValue.plus(p)
            (acc.leftValue to r).bothIor()
          }
          is Ior.Both -> {
            val (rhsL, rhsR) = rhs.value as Ior.Both<E, V>
            val l = combine(acc.leftValue, rhsL)
            val p = rhs.key to rhsR
            val r = acc.rightValue.plus(p)
            (l to r).bothIor()
          }
        }
      }
    }
  }
}
