package arrow.core.raise

import arrow.core.Either
import arrow.core.Ior
import arrow.typeclasses.Semigroup
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

@Suppress(
  "UNREACHABLE_CODE",
  "IMPLICIT_NOTHING_TYPE_ARGUMENT_IN_RETURN_POSITION",
  "UNUSED_VARIABLE"
)
class IorSpec : StringSpec({
  "Accumulates" {
    ior(String::plus) {
      val one = Ior.Both("Hello", 1).bind()
      val two = Ior.Both(", World!", 2).bind()
      one + two
    } shouldBe Ior.Both("Hello, World!", 3)
  }

  "Accumulates and short-circuits with Left" {
    ior(String::plus) {
      val one = Ior.Both("Hello", 1).bind()
      val two: Int = Ior.Left(", World!").bind()
      one + two
    } shouldBe Ior.Left("Hello, World!")
  }

  "Accumulates with Either" {
    ior(String::plus) {
      val one = Ior.Both("Hello", 1).bind()
      val two: Int = Either.Left(", World!").bind<Int>()
      one + two
    } shouldBe Ior.Left("Hello, World!")
  }

  "Concurrent - arrow.ior bind" {
    checkAll(Arb.list(Arb.int(), 1..100)) { xs ->
      ior(List<Int>::plus) {
        xs.mapIndexed { index, s -> async { Ior.Both(listOf(s), index).bind() } }.awaitAll()
      }
        .mapLeft { it.toSet() } shouldBe Ior.Both(xs.toSet(), xs.indices.toList())
    }
  }

  "Accumulates eagerly" {
    ior(String::plus) {
      val one = Ior.Both("Hello", 1).bind()
      val two = Ior.Both(", World!", 2).bind()
      one + two
    } shouldBe Ior.Both("Hello, World!", 3)
  }

  "Accumulates with Either eagerly" {
    ior(String::plus) {
      val one = Ior.Both("Hello", 1).bind()
      val two: Int = Either.Left(", World!").bind<Int>()
      one + two
    } shouldBe Ior.Left("Hello, World!")
  }

  "Ior rethrows exception" {
    val boom = RuntimeException("Boom!")
    shouldThrow<RuntimeException> {
      ior(String::plus) {
       throw boom
      }
    }.message shouldBe "Boom!"
  }
})
