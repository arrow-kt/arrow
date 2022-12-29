package arrow.core

import arrow.core.test.ior
import arrow.core.test.laws.SemigroupLaws
import arrow.core.test.testLaws
import arrow.typeclasses.Semigroup
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.property.Arb
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class IorTest : StringSpec({

    val ARB = Arb.ior(Arb.string(), Arb.int())

    testLaws(
      SemigroupLaws.laws(Semigroup.ior(Semigroup.string(), Semigroup.int()), ARB)
    )

    val nullableLongSemigroup =
      Semigroup<Long?> { a, b -> Nullable.zip(a, b) { aa, bb -> aa + bb } }

    "zip identity" {
      checkAll(Arb.ior(Arb.long().orNull(), Arb.int().orNull())) { ior ->
        val res = ior.zip(nullableLongSemigroup, Ior.Right(Unit)) { a, _ -> a }
        res shouldBe ior
      }
    }

    "zip is derived from flatMap" {
      checkAll(
        Arb.ior(Arb.long().orNull(), Arb.int().orNull()),
        Arb.ior(Arb.long().orNull(), Arb.int().orNull()),
        Arb.ior(Arb.long().orNull(), Arb.int().orNull()),
        Arb.ior(Arb.long().orNull(), Arb.int().orNull()),
        Arb.ior(Arb.long().orNull(), Arb.int().orNull()),
        Arb.ior(Arb.long().orNull(), Arb.int().orNull()),
        Arb.ior(Arb.long().orNull(), Arb.int().orNull()),
        Arb.ior(Arb.long().orNull(), Arb.int().orNull()),
        Arb.ior(Arb.long().orNull(), Arb.int().orNull()),
        Arb.ior(Arb.long().orNull(), Arb.int().orNull())
      ) { a, b, c, d, e, f, g, h, i, j ->
        val res = a.zip(
          nullableLongSemigroup,
          b, c, d, e, f, g, h, i, j
        ) { a, b, c, d, e, f, g, h, i, j ->
          Nullable.zip(
            a,
            b,
            c,
            d,
            e,
            f,
            g,
            h,
            i,
            j
          ) { a, b, c, d, e, f, g, h, i, j -> a + b + c + d + e + f + g + h + i + j }
        }

        val expected = listOf(a, b, c, d, e, f, g, h, i, j)
          .fold<Ior<Long?, Int?>, Ior<Long?, Int?>>(Ior.Right(0)) { acc, ior ->
            val mid = acc.flatMap(nullableLongSemigroup) { a -> ior.map { b -> Nullable.zip(a, b) { a, b -> a + b } } }
            mid
          }

        res shouldBe expected
      }
    }

    "zip should combine left values in correct order" {
      Ior.Both("fail1", -1).zip(
        Semigroup.string(),
        Ior.Left("fail2"),
        Ior.Right(-1)
      ) { _, _, _ -> "success!" } shouldBe Ior.Left("fail1fail2")
    }

    "bimap() should allow modify both value" {
      checkAll(Arb.int(), Arb.string()) { a: Int, b: String ->
        Ior.Right(b).bimap({ "5" }, { a * 2 }) shouldBe Ior.Right(a * 2)
        Ior.Left(a).bimap({ a * 3 }, { "5" }) shouldBe Ior.Left(a * 3)
        Ior.Both(a, b).bimap({ 2 }, { "power of $it" }) shouldBe Ior.Both(2, "power of $b")
      }
    }

    "mapLeft() should modify only left value" {
      checkAll(Arb.int(), Arb.string()) { a: Int, b: String ->
        Ior.Right(b).mapLeft { a * 2 } shouldBe Ior.Right(b)
        Ior.Left(a).mapLeft { b } shouldBe Ior.Left(b)
        Ior.Both(a, b).mapLeft { "power of $it" } shouldBe Ior.Both("power of $a", b)
      }
    }

    "swap() should interchange value" {
      checkAll(Arb.int(), Arb.string()) { a: Int, b: String ->
        Ior.Both(a, b).swap() shouldBe Ior.Both(b, a)
      }
    }

    "swap() should interchange entity" {
      checkAll(Arb.int()) { a: Int ->
        Ior.Left(a).swap() shouldBe Ior.Right(a)
        Ior.Right(a).swap() shouldBe Ior.Left(a)
      }
    }

    "unwrap() should return the isomorphic either" {
      checkAll(Arb.int(), Arb.string()) { a: Int, b: String ->
        Ior.Left(a).unwrap() shouldBe Either.Left(Either.Left(a))
        Ior.Right(b).unwrap() shouldBe Either.Left(Either.Right(b))
        Ior.Both(a, b).unwrap() shouldBe Either.Right(Pair(a, b))
      }
    }

    "padNull() should return the correct Pair of nullables" {
      checkAll(Arb.int(), Arb.string()) { a: Int, b: String ->
        Ior.Left(a).padNull() shouldBe Pair(a, null)
        Ior.Right(b).padNull() shouldBe Pair(null, b)
        Ior.Both(a, b).padNull() shouldBe Pair(a, b)
      }
    }

    "toEither() should convert values into a valid Either" {
      checkAll(Arb.int(), Arb.string()) { a: Int, b: String ->
        Ior.Left(a).toEither() shouldBe Either.Left(a)
        Ior.Right(b).toEither() shouldBe Either.Right(b)
        Ior.Both(a, b).toEither() shouldBe Either.Right(b)
      }
    }

    "orNull() should convert right values into a nullable" {
      checkAll(Arb.int(), Arb.string()) { a: Int, b: String ->
        Ior.Left(a).orNull() shouldBe null
        Ior.Right(b).orNull() shouldBe b
        Ior.Both(a, b).orNull() shouldBe b
      }
    }

    "leftOrNull() should convert left values into a nullable" {
      checkAll(Arb.int(), Arb.string()) { a: Int, b: String ->
        Ior.Left(a).leftOrNull() shouldBe a
        Ior.Right(b).leftOrNull() shouldBe null
        Ior.Both(a, b).leftOrNull() shouldBe a
      }
    }

    "fromNullables() should build a correct Ior" {
      checkAll(Arb.int(), Arb.string()) { a: Int, b: String ->
        Ior.fromNullables(a, null) shouldBe Ior.Left(a)
        Ior.fromNullables(a, b) shouldBe Ior.Both(a, b)
        Ior.fromNullables(null, b) shouldBe Ior.Right(b)
        Ior.fromNullables(null, null) shouldBe null
      }
    }

    "getOrElse() should return value" {
      checkAll(Arb.int(), Arb.int()) { a: Int, b: Int ->
        Ior.Right(a).getOrElse { b } shouldBe a
        Ior.Left(a).getOrElse { b } shouldBe b
        Ior.Both(a, b).getOrElse { a * 2 } shouldBe b
      }
    }

    "Ior.monad.flatMap should combine left values" {
      val ior1 = Ior.Both(3, "Hello, world!")
      val iorResult = ior1.flatMap(Semigroup.int()) { Ior.Left(7) }
      iorResult shouldBe Ior.Left(10)
    }

    "combine cases for Semigroup" {
      Semigroup.ior(Semigroup.string(), Semigroup.int()).run {
        forAll(
          row("Hello, ".leftIor(), Ior.Left("Arrow!"), Ior.Left("Hello, Arrow!")),
          row(Ior.Left("Hello"), Ior.Right(2020), Ior.Both("Hello", 2020)),
          row(Ior.Left("Hello, "), Ior.Both("number", 1), Ior.Both("Hello, number", 1)),
          row(Ior.Right(9000), Ior.Left("Over"), Ior.Both("Over", 9000)),
          row(Ior.Right(9000), Ior.Right(1), Ior.Right(9001)),
          row(Ior.Right(8000), Ior.Both("Over", 1000), Ior.Both("Over", 9000)),
          row(Ior.Both("Hello ", 1), Ior.Left("number"), Ior.Both("Hello number", 1)),
          row(Ior.Both("Hello number", 1), Ior.Right(1), Ior.Both("Hello number", 2)),
          row(Ior.Both("Hello ", 1), Ior.Both("number", 1), Ior.Both("Hello number", 2))
        ) { a, b, expectedResult ->
          a + b shouldBe expectedResult
        }
      }
    }

})
