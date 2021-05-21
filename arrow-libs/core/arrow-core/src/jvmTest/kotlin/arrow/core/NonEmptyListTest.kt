package arrow.core

import arrow.core.test.UnitSpec
import arrow.core.test.generators.nonEmptyList
import arrow.core.test.laws.SemigroupLaws
import arrow.typeclasses.Semigroup
import io.kotest.property.Arb
import io.kotest.property.checkAll
import io.kotest.matchers.shouldBe
import kotlin.math.max
import kotlin.math.min

class NonEmptyListTest : UnitSpec() {
  init {

    testLaws(SemigroupLaws.laws(Semigroup.nonEmptyList(), Gen.nonEmptyList(Gen.int())))

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
      checkAll(Gen.nonEmptyList(Gen.int())) { ints ->
        val acc = mutableListOf<Int>()
        val evens = ints.traverseEither {
          if (it % 2 == 0) {
            acc.add(it)
            Either.Right(it)
          } else Either.Left(it)
        }
        acc == ints.takeWhile { it % 2 == 0 } &&
          when (evens) {
            is Either.Right -> evens.value == ints
            is Either.Left -> evens.value == ints.first { it % 2 != 0 }
          }
      }
    }

    "sequenceEither should be consistent with traverseEither" {
      checkAll(Gen.nonEmptyList(Gen.int())) { ints ->
        ints.map { Either.conditionally(it % 2 == 0, { it }, { it }) }.sequenceEither() ==
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
      checkAll(Gen.nonEmptyList(Gen.int())) { ints ->
        val acc = mutableListOf<Int>()
        val evens = ints.traverseOption {
          (it % 2 == 0).maybe {
            acc.add(it)
            it
          }
        }
        acc == ints.takeWhile { it % 2 == 0 } && evens.all { it == ints }
      }
    }

    "sequenceOption yields some when all entries in the list are some" {
      checkAll(Gen.nonEmptyList(Gen.int())) { ints ->
        val evens = ints.map { (it % 2 == 0).maybe { it } }.sequenceOption()
        evens.all { it == ints }
      }
    }

    "sequenceOption should be consistent with traverseOption" {
      checkAll(Gen.nonEmptyList(Gen.int())) { ints ->
        ints.map { (it % 2 == 0).maybe { it } }.sequenceOption() ==
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
      checkAll(Gen.nonEmptyList(Gen.int())) { ints ->
        val res: ValidatedNel<Int, NonEmptyList<Int>> =
          ints.traverseValidated(Semigroup.nonEmptyList()) { i -> if (i % 2 == 0) i.validNel() else i.invalidNel() }

        val expected: ValidatedNel<Int, NonEmptyList<Int>> = NonEmptyList.fromList(ints.filterNot { it % 2 == 0 })
          .fold({ NonEmptyList.fromListUnsafe(ints.filter { it % 2 == 0 }).validNel() }, { it.invalid() })

        res == expected
      }
    }

    "sequenceValidated should be consistent with traverseValidated" {
      checkAll(Gen.nonEmptyList(Gen.int())) { ints ->
        ints.map { if (it % 2 == 0) it.valid() else it.invalid() }.sequenceValidated(Semigroup.int()) ==
          ints.traverseValidated(Semigroup.int()) { if (it % 2 == 0) it.valid() else it.invalid() }
      }
    }

    "can align lists with different lengths" {
      checkAll(Gen.nonEmptyList(Gen.bool()), Gen.nonEmptyList(Gen.bool())) { a, b ->
        a.align(b).size == max(a.size, b.size)
      }

      checkAll(Gen.nonEmptyList(Gen.bool()), Gen.nonEmptyList(Gen.bool())) { a, b ->
        a.align(b).all.take(min(a.size, b.size)).all {
          it.isBoth
        }
      }
    }

    "zip2" {
      checkAll(Gen.nonEmptyList(Gen.int()), Gen.nonEmptyList(Gen.int())) { a, b ->
        val result = a.zip(b)
        val expected = a.all.zip(b.all).let(NonEmptyList.Companion::fromListUnsafe)
        result == expected
      }
    }

    "zip3" {
      checkAll(
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int())
      ) { a, b, c ->
        val result = a.zip(b, c, ::Triple)
        val expected = a.all.zip(b.all, c.all, ::Triple).let(NonEmptyList.Companion::fromListUnsafe)
        result == expected
      }
    }

    "zip4" {
      checkAll(
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int())
      ) { a, b, c, d ->
        val result = a.zip(b, c, d, ::Tuple4)
        val expected = a.all.zip(b.all, c.all, d.all, ::Tuple4).let(NonEmptyList.Companion::fromListUnsafe)
        result == expected
      }
    }

    "zip5" {
      checkAll(
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int())
      ) { a, b, c, d, e ->
        val result = a.zip(b, c, d, e, ::Tuple5)
        val expected = a.all.zip(b.all, c.all, d.all, e.all, ::Tuple5).let(NonEmptyList.Companion::fromListUnsafe)
        result == expected
      }
    }

    "zip6" {
      checkAll(
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int())
      ) { a, b, c, d, e, f ->
        val result = a.zip(b, c, d, e, f, ::Tuple6)
        val expected = a.all.zip(b.all, c.all, d.all, e.all, f.all, ::Tuple6).let(NonEmptyList.Companion::fromListUnsafe)
        result == expected
      }
    }

    "zip7" {
      checkAll(
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int())
      ) { a, b, c, d, e, f, g ->
        val result = a.zip(b, c, d, e, f, g, ::Tuple7)
        val expected = a.all.zip(b.all, c.all, d.all, e.all, f.all, g.all, ::Tuple7).let(NonEmptyList.Companion::fromListUnsafe)
        result == expected
      }
    }

    "zip8" {
      checkAll(
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int())
      ) { a, b, c, d, e, f, g, h ->
        val result = a.zip(b, c, d, e, f, g, h, ::Tuple8)
        val expected = a.all.zip(b.all, c.all, d.all, e.all, f.all, g.all, h.all, ::Tuple8).let(NonEmptyList.Companion::fromListUnsafe)
        result == expected
      }
    }

    "zip9" {
      checkAll(
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int())
      ) { a, b, c, d, e, f, g, h, i ->
        val result = a.zip(b, c, d, e, f, g, h, i, ::Tuple9)
        val expected = a.all.zip(b.all, c.all, d.all, e.all, f.all, g.all, h.all, i.all, ::Tuple9).let(NonEmptyList.Companion::fromListUnsafe)
        result == expected
      }
    }

    "zip10" {
      checkAll(
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int())
      ) { a, b, c, d, e, f, g, h, i, j ->
        val result = a.zip(b, c, d, e, f, g, h, i, j, ::Tuple10)
        val expected = a.all.zip(b.all, c.all, d.all, e.all, f.all, g.all, h.all, i.all, j.all, ::Tuple10).let(NonEmptyList.Companion::fromListUnsafe)
        result == expected
      }
    }
  }
}
