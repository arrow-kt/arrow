package arrow

import arrow.core.Either
import arrow.core.Ior
import arrow.typeclasses.Semigroup
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

class IorSpec : StringSpec({
  "Accumulates" {
    ior(Semigroup.string()) {
      val one = Ior.Both("Hello", 1).bind()
      val two = Ior.Both(", World!", 2).bind()
      one + two
    } shouldBe Ior.Both("Hello, World!", 3)
  }

  "Accumulates with Either" {
    ior(Semigroup.string()) {
      val one = Ior.Both("Hello", 1).bind()
      val two: Int = Either.Left(", World!").bind()
      one + two
    } shouldBe Ior.Left("Hello, World!")
  }

  "Concurrent - arrow.ior bind" {
    checkAll(Arb.list(Arb.string()).filter(List<String>::isNotEmpty)) { strs ->
      ior(Semigroup.list<String>()) {
        strs.mapIndexed { index, s ->
          async { Ior.Both(listOf(s), index).bind() }
        }.awaitAll()
      }.mapLeft { it.toSet() } shouldBe Ior.Both(strs.toSet(), strs.indices.toList())
    }
  }
})
