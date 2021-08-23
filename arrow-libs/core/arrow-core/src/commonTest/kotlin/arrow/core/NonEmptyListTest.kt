package arrow.core

import arrow.core.test.UnitSpec
import arrow.core.test.laws.SemigroupLaws
import arrow.typeclasses.Semigroup
import io.kotest.property.Arb
import io.kotest.property.checkAll
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import kotlin.math.max
import kotlin.math.min

class NonEmptyListTest : UnitSpec() {
  init {

    testLaws(SemigroupLaws.laws(Semigroup.nonEmptyList(), Arb.nonEmptyList(Arb.int())))

    "traverseEither stack-safe" {
      // also verifies result order and execution order (l to r)
      val acc = mutableListOf<Int>()
      val res = NonEmptyList.fromListUnsafe((0..20_000).toList()).traverseEither { a ->
        acc.add(a)
        Either.Right(a)
      }
      res shouldBe Either.Right(NonEmptyList.fromListUnsafe(acc))
      res shouldBe Either.Right(NonEmptyList.fromListUnsafe((0..20_000).toList()))
    }

    "traverseEither short-circuit" {
      checkAll(Arb.nonEmptyList(Arb.int())) { ints ->
        val acc = mutableListOf<Int>()
        val evens = ints.traverseEither {
          if (it % 2 == 0) {
            acc.add(it)
            Either.Right(it)
          } else Either.Left(it)
        }
        acc shouldBe ints.takeWhile { it % 2 == 0 }
        when (evens) {
          is Either.Right -> evens.value shouldBe ints
          is Either.Left -> evens.value shouldBe ints.first { it % 2 != 0 }
        }
      }
    }

    "sequenceEither should be consistent with traverseEither" {
      checkAll(Arb.nonEmptyList(Arb.int())) { ints ->
        ints.map { Either.conditionally(it % 2 == 0, { it }, { it }) }.sequenceEither() shouldBe
          ints.traverseEither { Either.conditionally(it % 2 == 0, { it }, { it }) }
      }
    }

    "traverseOption is stack-safe" {
      // also verifies result order and execution order (l to r)
      val acc = mutableListOf<Int>()
      val res = NonEmptyList.fromListUnsafe((0..20_000).toList()).traverseOption { a ->
        acc.add(a)
        Some(a)
      }
      res shouldBe Some(NonEmptyList.fromListUnsafe(acc))
      res shouldBe Some(NonEmptyList.fromListUnsafe((0..20_000).toList()))
    }

    "traverseOption short-circuits" {
      checkAll(Arb.nonEmptyList(Arb.int())) { ints ->
        val acc = mutableListOf<Int>()
        val evens = ints.traverseOption {
          (it % 2 == 0).maybe {
            acc.add(it)
            it
          }
        }
        acc shouldBe ints.takeWhile { it % 2 == 0 }
        evens.fold({ Unit }) { it shouldBe ints }
      }
    }

    "sequenceOption yields some when all entries in the list are some" {
      checkAll(Arb.nonEmptyList(Arb.int())) { ints ->
        val evens = ints.map { (it % 2 == 0).maybe { it } }.sequenceOption()
        evens.fold({ Unit }) { it shouldBe ints }
      }
    }

    "sequenceOption should be consistent with traverseOption" {
      checkAll(Arb.nonEmptyList(Arb.int())) { ints ->
        ints.map { (it % 2 == 0).maybe { it } }.sequenceOption() shouldBe
          ints.traverseOption { (it % 2 == 0).maybe { it } }
      }
    }

    "traverseValidated stack-safe" {
      // also verifies result order and execution order (l to r)
      val acc = mutableListOf<Int>()
      val res = (0..20_000).traverseValidated(Semigroup.string()) {
        acc.add(it)
        Validated.Valid(it)
      }
      res shouldBe Validated.Valid(acc)
      res shouldBe Validated.Valid((0..20_000).toList())
    }

    "traverseValidated acummulates" {
      checkAll(Arb.nonEmptyList(Arb.int())) { ints ->
        val res: ValidatedNel<Int, NonEmptyList<Int>> =
          ints.traverseValidated(Semigroup.nonEmptyList()) { i -> if (i % 2 == 0) i.validNel() else i.invalidNel() }

        val expected: ValidatedNel<Int, NonEmptyList<Int>> = NonEmptyList.fromList(ints.filterNot { it % 2 == 0 })
          .fold({ NonEmptyList.fromListUnsafe(ints.filter { it % 2 == 0 }).validNel() }, { it.invalid() })

        res shouldBe expected
      }
    }

    "sequenceValidated should be consistent with traverseValidated" {
      checkAll(Arb.nonEmptyList(Arb.int())) { ints ->
        ints.map { if (it % 2 == 0) Valid(it) else Invalid(it) }.sequenceValidated(Semigroup.int()) shouldBe
          ints.traverseValidated(Semigroup.int()) { if (it % 2 == 0) Valid(it) else Invalid(it) }
      }
    }

    "can align lists with different lengths" {
      checkAll(Arb.nonEmptyList(Arb.boolean()), Arb.nonEmptyList(Arb.boolean())) { a, b ->
        a.align(b).size shouldBe max(a.size, b.size)
      }

      checkAll(Arb.nonEmptyList(Arb.boolean()), Arb.nonEmptyList(Arb.boolean())) { a, b ->
        a.align(b).all.take(min(a.size, b.size)).forEach {
          it.isBoth shouldBe true
        }
      }
    }

    "zip2" {
      checkAll(Arb.nonEmptyList(Arb.int()), Arb.nonEmptyList(Arb.int())) { a, b ->
        val result = a.zip(b)
        val expected = a.all.zip(b.all).let(NonEmptyList.Companion::fromListUnsafe)
        result shouldBe expected
      }
    }

    "zip3" {
      checkAll(
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int())
      ) { a, b, c ->
        val result = a.zip(b, c, ::Triple)
        val expected = a.all.zip(b.all, c.all, ::Triple).let(NonEmptyList.Companion::fromListUnsafe)
        result shouldBe expected
      }
    }

    "zip4" {
      checkAll(
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int())
      ) { a, b, c, d ->
        val result = a.zip(b, c, d, ::Tuple4)
        val expected = a.all.zip(b.all, c.all, d.all, ::Tuple4).let(NonEmptyList.Companion::fromListUnsafe)
        result shouldBe expected
      }
    }

    "zip5" {
      checkAll(
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int())
      ) { a, b, c, d, e ->
        val result = a.zip(b, c, d, e, ::Tuple5)
        val expected = a.all.zip(b.all, c.all, d.all, e.all, ::Tuple5).let(NonEmptyList.Companion::fromListUnsafe)
        result shouldBe expected
      }
    }

    "zip6" {
      checkAll(
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int())
      ) { a, b, c, d, e, f ->
        val result = a.zip(b, c, d, e, f, ::Tuple6)
        val expected =
          a.all.zip(b.all, c.all, d.all, e.all, f.all, ::Tuple6).let(NonEmptyList.Companion::fromListUnsafe)
        result shouldBe expected
      }
    }

    "zip7" {
      checkAll(
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int())
      ) { a, b, c, d, e, f, g ->
        val result = a.zip(b, c, d, e, f, g, ::Tuple7)
        val expected =
          a.all.zip(b.all, c.all, d.all, e.all, f.all, g.all, ::Tuple7).let(NonEmptyList.Companion::fromListUnsafe)
        result shouldBe expected
      }
    }

    "zip8" {
      checkAll(
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int())
      ) { a, b, c, d, e, f, g, h ->
        val result = a.zip(b, c, d, e, f, g, h, ::Tuple8)
        val expected = a.all.zip(b.all, c.all, d.all, e.all, f.all, g.all, h.all, ::Tuple8)
          .let(NonEmptyList.Companion::fromListUnsafe)
        result shouldBe expected
      }
    }

    "zip9" {
      checkAll(
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int())
      ) { a, b, c, d, e, f, g, h, i ->
        val result = a.zip(b, c, d, e, f, g, h, i, ::Tuple9)
        val expected = a.all.zip(b.all, c.all, d.all, e.all, f.all, g.all, h.all, i.all, ::Tuple9)
          .let(NonEmptyList.Companion::fromListUnsafe)
        result shouldBe expected
      }
    }

    "zip10" {
      checkAll(
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int()),
        Arb.nonEmptyList(Arb.int())
      ) { a, b, c, d, e, f, g, h, i, j ->
        val result = a.zip(b, c, d, e, f, g, h, i, j, ::Tuple10)
        val expected = a.all.zip(b.all, c.all, d.all, e.all, f.all, g.all, h.all, i.all, j.all, ::Tuple10)
          .let(NonEmptyList.Companion::fromListUnsafe)
        result shouldBe expected
      }
    }
  }
}
