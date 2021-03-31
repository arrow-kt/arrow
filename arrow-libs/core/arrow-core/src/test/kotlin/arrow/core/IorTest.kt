package arrow.core

import arrow.core.test.UnitSpec
import arrow.core.test.generators.ior
import arrow.core.test.laws.SemigroupLaws
import arrow.typeclasses.Semigroup
import io.kotlintest.forAll
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe

class IorTest : UnitSpec() {

  init {

    val GEN = Gen.ior(Gen.string(), Gen.int())

    testLaws(
      SemigroupLaws.laws(Semigroup.ior(Semigroup.string(), Semigroup.int()), GEN)
    )

    val nullableLongSemigroup = object : Semigroup<Long?> {
      override fun Long?.combine(b: Long?): Long? =
        Nullable.zip(this, b) { a, bb -> a + bb }
    }

    "zip identity" {
      forAll(Gen.ior(Gen.long().orNull(), Gen.int().orNull())) { ior ->
        val res = ior.zip(nullableLongSemigroup, Ior.Right(Unit)) { a, _ -> a }
        res == ior
      }
    }

    "zip is derived from flatMap" {
      forAll(
        Gen.ior(Gen.long().orNull(), Gen.int().orNull()),
        Gen.ior(Gen.long().orNull(), Gen.int().orNull()),
        Gen.ior(Gen.long().orNull(), Gen.int().orNull()),
        Gen.ior(Gen.long().orNull(), Gen.int().orNull()),
        Gen.ior(Gen.long().orNull(), Gen.int().orNull()),
        Gen.ior(Gen.long().orNull(), Gen.int().orNull()),
        Gen.ior(Gen.long().orNull(), Gen.int().orNull()),
        Gen.ior(Gen.long().orNull(), Gen.int().orNull()),
        Gen.ior(Gen.long().orNull(), Gen.int().orNull()),
        Gen.ior(Gen.long().orNull(), Gen.int().orNull())
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

        res == expected
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
      forAll { a: Int, b: String ->
        Ior.Right(b).bimap({ "5" }, { a * 2 }) == Ior.Right(a * 2) &&
          Ior.Left(a).bimap({ a * 3 }, { "5" }) == Ior.Left(a * 3) &&
          Ior.Both(a, b).bimap({ 2 }, { "power of $it" }) == Ior.Both(2, "power of $b")
      }
    }

    "mapLeft() should modify only left value" {
      forAll { a: Int, b: String ->
        Ior.Right(b).mapLeft { a * 2 } == Ior.Right(b) &&
          Ior.Left(a).mapLeft { b } == Ior.Left(b) &&
          Ior.Both(a, b).mapLeft { "power of $it" } == Ior.Both("power of $a", b)
      }
    }

    "swap() should interchange value" {
      forAll { a: Int, b: String ->
        Ior.Both(a, b).swap() == Ior.Both(b, a)
      }
    }

    "swap() should interchange entity" {
      forAll { a: Int ->
        Ior.Left(a).swap() == Ior.Right(a) &&
          Ior.Right(a).swap() == Ior.Left(a)
      }
    }

    "unwrap() should return the isomorphic either" {
      forAll { a: Int, b: String ->
        Ior.Left(a).unwrap() == Either.Left(Either.Left(a)) &&
          Ior.Right(b).unwrap() == Either.Left(Either.Right(b)) &&
          Ior.Both(a, b).unwrap() == Either.Right(Pair(a, b))
      }
    }

    "padNull() should return the correct Pair of nullables" {
      forAll { a: Int, b: String ->
        Ior.Left(a).padNull() == Pair(a, null) &&
          Ior.Right(b).padNull() == Pair(null, b) &&
          Ior.Both(a, b).padNull() == Pair(a, b)
      }
    }

    "toEither() should convert values into a valid Either" {
      forAll { a: Int, b: String ->
        Ior.Left(a).toEither() == Either.Left(a) &&
          Ior.Right(b).toEither() == Either.Right(b) &&
          Ior.Both(a, b).toEither() == Either.Right(b)
      }
    }

    "orNull() should convert right values into a nullable" {
      forAll { a: Int, b: String ->
        Ior.Left(a).orNull() == null &&
          Ior.Right(b).orNull() == b &&
          Ior.Both(a, b).orNull() == b
      }
    }

    "leftOrNull() should convert left values into a nullable" {
      forAll { a: Int, b: String ->
        Ior.Left(a).leftOrNull() == a &&
          Ior.Right(b).leftOrNull() == null &&
          Ior.Both(a, b).leftOrNull() == a
      }
    }

    "toValidated() should convert values into a valid Validated" {
      forAll { a: Int, b: String ->
        Ior.Left(a).toValidated() == Invalid(a) &&
          Ior.Right(b).toValidated() == Valid(b) &&
          Ior.Both(a, b).toValidated() == Valid(b)
      }
    }

    "fromNullables() should build a correct Ior" {
      forAll { a: Int, b: String ->
        Ior.fromNullables(a, null) == Ior.Left(a) &&
          Ior.fromNullables(a, b) == Ior.Both(a, b) &&
          Ior.fromNullables(null, b) == Ior.Right(b) &&
          Ior.fromNullables(null, null) == null
      }
    }

    "getOrElse() should return value" {
      forAll { a: Int, b: Int ->
        Ior.Right(a).getOrElse { b } == a &&
          Ior.Left(a).getOrElse { b } == b &&
          Ior.Both(a, b).getOrElse { a * 2 } == b
      }
    }

    "Ior.monad.flatMap should combine left values" {
      val ior1 = Ior.Both(3, "Hello, world!")
      val iorResult = ior1.flatMap(Semigroup.int()) { Ior.Left(7) }
      iorResult shouldBe Ior.Left(10)
    }

    "combine cases for Semigroup" {
      fun case(a: Ior<String, Int>, b: Ior<String, Int>, result: Ior<String, Int>) = listOf(a, b, result)
      Semigroup.ior(Semigroup.string(), Semigroup.int()).run {
        forAll(
          listOf(
            case("Hello, ".leftIor(), Ior.Left("Arrow!"), Ior.Left("Hello, Arrow!")),
            case(Ior.Left("Hello"), Ior.Right(2020), Ior.Both("Hello", 2020)),
            case(Ior.Left("Hello, "), Ior.Both("number", 1), Ior.Both("Hello, number", 1)),
            case(Ior.Right(9000), Ior.Left("Over"), Ior.Both("Over", 9000)),
            case(Ior.Right(9000), Ior.Right(1), Ior.Right(9001)),
            case(Ior.Right(8000), Ior.Both("Over", 1000), Ior.Both("Over", 9000)),
            case(Ior.Both("Hello ", 1), Ior.Left("number"), Ior.Both("Hello number", 1)),
            case(Ior.Both("Hello number", 1), Ior.Right(1), Ior.Both("Hello number", 2)),
            case(Ior.Both("Hello ", 1), Ior.Both("number", 1), Ior.Both("Hello number", 2))
          )
        ) { (a, b, expectedResult) ->
          a + b shouldBe expectedResult
        }
      }
    }
  }
}
