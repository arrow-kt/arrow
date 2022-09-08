package arrow.core

import arrow.core.test.UnitSpec
import arrow.core.test.laws.SemigroupLaws
import arrow.typeclasses.Semigroup
import io.kotest.property.Arb
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import kotlin.math.max
import kotlin.math.min

class NonEmptyListTest : UnitSpec() {
  init {

    testLaws(SemigroupLaws.laws(Semigroup.nonEmptyList(), Arb.nonEmptyList(Arb.int())))

    "traverse for Either stack-safe" {
      // also verifies result order and execution order (l to r)
      val acc = mutableListOf<Int>()
      val res = (0..20_000).toNonEmptyListOrNull()?.traverse { a ->
        acc.add(a)
        Either.Right(a)
      }
      res shouldBe Either.Right(acc.toNonEmptyListOrNull())
      res shouldBe Either.Right((0..20_000).toNonEmptyListOrNull())
    }

    "traverse for Either short-circuit" {
      checkAll(Arb.nonEmptyList(Arb.int())) { ints ->
        val acc = mutableListOf<Int>()
        val evens = ints.traverse {
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

    "sequence for Either should be consistent with traverseEither" {
      checkAll(Arb.nonEmptyList(Arb.int())) { ints ->
        ints.map { Either.conditionally(it % 2 == 0, { it }, { it }) }.sequence() shouldBe
          ints.traverse { Either.conditionally(it % 2 == 0, { it }, { it }) }
      }
    }

    "traverse for Option is stack-safe" {
      // also verifies result order and execution order (l to r)
      val acc = mutableListOf<Int>()
      val res = (0..20_000).toNonEmptyListOrNull()?.traverse { a ->
        acc.add(a)
        Some(a)
      }
      res shouldBe Some(acc.toNonEmptyListOrNull())
      res shouldBe Some((0..20_000).toNonEmptyListOrNull())
    }

    "traverse for Option short-circuits" {
      checkAll(Arb.nonEmptyList(Arb.int())) { ints ->
        val acc = mutableListOf<Int>()
        val evens = ints.traverse {
          (it % 2 == 0).maybe {
            acc.add(it)
            it
          }
        }
        acc shouldBe ints.takeWhile { it % 2 == 0 }
        evens.fold({ Unit }) { it shouldBe ints }
      }
    }

    "sequence for Option yields some when all entries in the list are some" {
      checkAll(Arb.nonEmptyList(Arb.int())) { ints ->
        val evens = ints.map { (it % 2 == 0).maybe { it } }.sequence()
        evens.fold({ Unit }) { it shouldBe ints }
      }
    }

    "sequence for Option should be consistent with traverseOption" {
      checkAll(Arb.nonEmptyList(Arb.int())) { ints ->
        ints.map { (it % 2 == 0).maybe { it } }.sequence() shouldBe
          ints.traverse { (it % 2 == 0).maybe { it } }
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
        val expected = a.all.zip(b.all).toNonEmptyListOrNull()
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
        val expected = a.all.zip(b.all, c.all, ::Triple).toNonEmptyListOrNull()
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
        val expected = a.all.zip(b.all, c.all, d.all, ::Tuple4).toNonEmptyListOrNull()
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
        val expected = a.all.zip(b.all, c.all, d.all, e.all, ::Tuple5).toNonEmptyListOrNull()
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
          a.all.zip(b.all, c.all, d.all, e.all, f.all, ::Tuple6).toNonEmptyListOrNull()
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
          a.all.zip(b.all, c.all, d.all, e.all, f.all, g.all, ::Tuple7).toNonEmptyListOrNull()
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
          .toNonEmptyListOrNull()
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
          .toNonEmptyListOrNull()
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
          .toNonEmptyListOrNull()
        result shouldBe expected
      }
    }

    "max element" {
      checkAll(
        Arb.nonEmptyList(Arb.int())
      ) { a ->
        val result = a.max()
        val expected = a.maxOrNull()
        result shouldBe expected
      }
    }

    "maxBy element" {
      checkAll(
        Arb.nonEmptyList(Arb.int())
      ) { a ->
        val result = a.maxBy(::identity)
        val expected = a.maxByOrNull(::identity)
        result shouldBe expected
      }
    }

    "min element" {
      checkAll(
        Arb.nonEmptyList(Arb.int())
      ) { a ->
        val result = a.min()
        val expected = a.minOrNull()
        result shouldBe expected
      }
    }


    "minBy element" {
      checkAll(
        Arb.nonEmptyList(Arb.int())
      ) { a ->
        val result = a.minBy(::identity)
        val expected = a.minByOrNull(::identity)
        result shouldBe expected
      }
    }
  }
}
