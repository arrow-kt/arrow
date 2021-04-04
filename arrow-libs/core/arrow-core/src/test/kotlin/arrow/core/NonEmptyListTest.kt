package arrow.core

import arrow.core.test.UnitSpec
import arrow.core.test.generators.nonEmptyList
import arrow.core.test.laws.SemigroupLaws
import arrow.typeclasses.Semigroup
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kotlin.math.max
import kotlin.math.min

class NonEmptyListTest : UnitSpec() {
  init {

    testLaws(SemigroupLaws.laws(Semigroup.nonEmptyList(), Gen.nonEmptyList(Gen.int())))

    "can align lists with different lengths" {
      forAll(Gen.nonEmptyList(Gen.bool()), Gen.nonEmptyList(Gen.bool())) { a, b ->
        a.align(b).size == max(a.size, b.size)
      }

      forAll(Gen.nonEmptyList(Gen.bool()), Gen.nonEmptyList(Gen.bool())) { a, b ->
        a.align(b).all.take(min(a.size, b.size)).all {
          it.isBoth
        }
      }
    }

    "zip2" {
      forAll(Gen.nonEmptyList(Gen.int()), Gen.nonEmptyList(Gen.int())) { a, b ->
        val result = a.zip(b)
        val expected = a.all.zip(b.all).let(NonEmptyList.Companion::fromListUnsafe)
        result == expected
      }
    }

    "zip3" {
      forAll(
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
      forAll(
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
      forAll(
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
      forAll(
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
      forAll(
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
      forAll(
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
      forAll(
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
      forAll(
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

    "traverseEither should correctly traverse nel" {
      forAll(Gen.nonEmptyList(Gen.int())) { a ->
        val result = (a.traverseEither { it.right() } as Either.Right<NonEmptyList<Int>>).value
        val expected = a
        result == expected
      }
    }

    "traverseValidated should correctly traverse nel" {
      forAll(Gen.nonEmptyList(Gen.int())) { a ->
        val result = (a.traverseValidated<List<*>, Int, Int>(Semigroup.list()) { it.valid() } as Validated.Valid<NonEmptyList<Int>>).value
        val expected = a
        result == expected
      }
    }
  }
}
