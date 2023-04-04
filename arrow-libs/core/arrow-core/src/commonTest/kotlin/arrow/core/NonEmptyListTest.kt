package arrow.core

import arrow.core.test.laws.SemigroupLaws
import arrow.core.test.nonEmptyList
import arrow.core.test.stackSafeIteration
import arrow.core.test.testLaws
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.negativeInt
import io.kotest.property.arbitrary.pair
import io.kotest.property.checkAll
import kotlin.math.max
import kotlin.math.min

class NonEmptyListTest : StringSpec({

    testLaws(SemigroupLaws("NonEmptyList", NonEmptyList<Int>::plus, Arb.nonEmptyList(Arb.int())))

    "iterable.toNonEmptyListOrNull should round trip" {
      checkAll(Arb.nonEmptyList(Arb.int())) { nonEmptyList ->
        nonEmptyList.all.toNonEmptyListOrNull().shouldNotBeNull() shouldBe nonEmptyList
      }
    }

    "iterable.toNonEmptyListOrNone should round trip" {
      checkAll(Arb.nonEmptyList(Arb.int())) { nonEmptyList ->
        nonEmptyList.all.toNonEmptyListOrNone() shouldBe nonEmptyList.some()
      }
    }

    "can align lists with different lengths" {
      checkAll(Arb.nonEmptyList(Arb.boolean()), Arb.nonEmptyList(Arb.boolean())) { a, b ->
        val result = a.align(b)

        result.size shouldBe max(a.size, b.size)
        result.take(min(a.size, b.size)).shouldForAll {
          it.isBoth() shouldBe true
        }
        result.drop(min(a.size, b.size)).shouldForAll {
          if (a.size < b.size) {
            it.isRight() shouldBe true
          } else {
            it.isLeft() shouldBe true
          }
        }
      }
    }

    "mapOrAccumulate is stack-safe, and runs in original order" {
      val acc = mutableListOf<Int>()
      val res = (0..stackSafeIteration())
        .toNonEmptyListOrNull()!!
        .mapOrAccumulate(String::plus) {
          acc.add(it)
          it
        }
      res shouldBe Either.Right(acc)
      res shouldBe Either.Right((0..stackSafeIteration()).toList())
    }

    "mapOrAccumulate accumulates errors" {
      checkAll(Arb.nonEmptyList(Arb.int())) { nel ->
        val res = nel.mapOrAccumulate { i ->
          if (i % 2 == 0) i else raise(i)
        }

        val expected = nel.filterNot { it % 2 == 0 }
          .toNonEmptyListOrNull()?.left() ?: nel.filter { it % 2 == 0 }.right()

        res shouldBe expected
      }
    }

    "mapOrAccumulate accumulates errors with combine function" {
      checkAll(Arb.nonEmptyList(Arb.negativeInt())) { nel ->
        val res = nel.mapOrAccumulate(String::plus) { i ->
          if (i > 0) i else raise("Negative")
        }

        res shouldBe nel.map { "Negative" }.joinToString("").left()
      }
    }

    "padZip" {
      checkAll(Arb.nonEmptyList(Arb.int()), Arb.nonEmptyList(Arb.int())) { a, b ->
        val result = a.padZip(b)
        val left = a + List(max(0, b.size - a.size)) { null }
        val right = b + List(max(0, a.size - b.size)) { null }

        result shouldBe left.zip(right)
      }
    }

    "padZip with transformation" {
      checkAll(Arb.nonEmptyList(Arb.int()), Arb.nonEmptyList(Arb.int())) { a, b ->
        val result = a.padZip(b, { it * 2 }, { it * 3 }, { x, y -> x + y })

        val minSize = min(a.size, b.size)
        result.size shouldBe max(a.size, b.size)
        result.take(minSize) shouldBe a.take(minSize).zip(b.take(minSize)) { x, y -> x + y }

        if (a.size > b.size)
          result.drop(minSize) shouldBe a.drop(minSize).map { it * 2 }
        else
          result.drop(minSize) shouldBe b.drop(minSize).map { it * 3 }
      }
    }

    "unzip is the inverse of zip" {
      checkAll(Arb.nonEmptyList(Arb.int())) { nel ->
        val zipped = nel.zip(nel)
        val left = zipped.map { it.first }
        val right = zipped.map { it.second }

        left shouldBe nel
        right shouldBe nel
      }
    }

    "unzip with split function" {
      checkAll(Arb.nonEmptyList(Arb.pair(Arb.int(), Arb.int()))) { nel ->
        val unzipped = nel.unzip(::identity)

        unzipped.first shouldBe nel.map { it.first }
        unzipped.second shouldBe nel.map { it.second }
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

    "NonEmptyList equals List" {
      checkAll(
        Arb.nonEmptyList(Arb.int())
      ) { a ->
        withClue("$a should be equal to ${a.all}") {
          // `shouldBe` doesn't use the `equals` methods on `Iterable`
          (a == a.all).shouldBeTrue()
        }
      }
    }
})
