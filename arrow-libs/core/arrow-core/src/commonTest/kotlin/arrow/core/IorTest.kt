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

    val nullableLongSemigroup = object : Semigroup<Long?> {
      override fun Long?.combine(b: Long?): Long? =
        Nullable.zip(this, b) { a, bb -> a + bb }
    }

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

    "toValidated() should convert values into a valid Validated" {
      checkAll(Arb.int(), Arb.string()) { a: Int, b: String ->
        Ior.Left(a).toValidated() shouldBe Invalid(a)
        Ior.Right(b).toValidated() shouldBe Valid(b)
        Ior.Both(a, b).toValidated() shouldBe Valid(b)
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

    "traverse should wrap ior in a list" {
      checkAll(Arb.int(), Arb.string()) { a: Int, b: String ->
        val iorL: Ior<Int, String> = a.leftIor()
        val iorR: Ior<Int, String> = b.rightIor()
        val iorBoth: Ior<Int, String> = (a to b).bothIor()

        iorL.traverse { listOf(it) } shouldBe listOf(Ior.Left(a))
        iorR.traverse { listOf(it) } shouldBe listOf(Ior.Right(b))
        iorBoth.traverse { listOf(it) } shouldBe listOf(Ior.Both(a, b))
      }
    }

    "sequence should be consistent with traverse" {
      checkAll(Arb.ior(Arb.int(), Arb.string())) { ior ->
        ior.map { listOf(it) }.sequence() shouldBe ior.traverse { listOf(it) }
      }
    }

    "traverseNullable should wrap ior in a nullable" {
      checkAll(Arb.int(), Arb.string()) { a: Int, b: String ->
        val iorL: Ior<Int, String> = a.leftIor()
        val iorR: Ior<Int, String> = b.rightIor()
        val iorBoth: Ior<Int, String> = (a to b).bothIor()

        iorL.traverseNullable { it } shouldBe Ior.Left(a)
        iorR.traverseNullable { it } shouldBe Ior.Right(b)
        iorBoth.traverseNullable { it } shouldBe Ior.Both(a, b)

        iorL.traverseNullable { null } shouldBe Ior.Left(a)
        iorR.traverseNullable { null } shouldBe null
        iorBoth.traverseNullable { null } shouldBe null
      }
    }

    "sequence for Nullable should be consistent with traverseNullable" {
      checkAll(Arb.ior(Arb.int(), Arb.string())) { ior ->
        ior.map<String?> { it }.sequence() shouldBe ior.traverseNullable { it }
        ior.map<String?> { null }.sequence() shouldBe ior.traverseNullable { null }
      }
    }

    "traverseOption should wrap ior in an Option" {
      checkAll(Arb.int(), Arb.string()) { a: Int, b: String ->
        val iorL: Ior<Int, String> = a.leftIor()
        val iorR: Ior<Int, String> = b.rightIor()
        val iorBoth: Ior<Int, String> = (a to b).bothIor()

        iorL.traverse { Some(it) } shouldBe Some(Ior.Left(a))
        iorR.traverse { Some(it) } shouldBe Some(Ior.Right(b))
        iorBoth.traverse { Some(it) } shouldBe Some(Ior.Both(a, b))
      }
    }

    "sequenceOption should be consistent with traverseOption" {
      checkAll(Arb.ior(Arb.int(), Arb.string())) { ior ->
        ior.map { Some(it) }.sequence() shouldBe ior.traverse { Some(it) }
      }
    }

    "traverseEither should wrap ior in an Option" {
      checkAll(Arb.int(), Arb.string()) { a: Int, b: String ->
        val iorL: Ior<Int, String> = a.leftIor()
        val iorR: Ior<Int, String> = b.rightIor()
        val iorBoth: Ior<Int, String> = (a to b).bothIor()

        iorL.traverse { it.right() } shouldBe Either.Right(Ior.Left(a))
        iorR.traverse { it.right() } shouldBe Either.Right(Ior.Right(b))
        iorBoth.traverse { it.right() } shouldBe Either.Right(Ior.Both(a, b))
      }
    }

    "sequenceEither should be consistent with traverseEither" {
      checkAll(Arb.ior(Arb.int(), Arb.string())) { ior ->
        ior.map { it.right() }.sequence() shouldBe ior.traverse { it.right() }
      }
    }

    "bitraverse should wrap ior in a list" {
      checkAll(Arb.int(), Arb.string()) { a: Int, b: String ->
        val iorL: Ior<Int, String> = a.leftIor()
        val iorR: Ior<Int, String> = b.rightIor()
        val iorBoth: Ior<Int, String> = (a to b).bothIor()

        iorL.bitraverse({ listOf(it, 2, 3) }, { listOf(it) }) shouldBe listOf(Ior.Left(a), Ior.Left(2), Ior.Left(3))
        iorR.bitraverse({ listOf(it, 2, 3) }, { listOf(it) }) shouldBe listOf(Ior.Right(b))
        iorBoth.bitraverse({ listOf(it, 2, 3) }, { listOf(it, 4, 5) }) shouldBe
          listOf(Ior.Both(a, b), Ior.Both(2, 4), Ior.Both(3, 5))
      }
    }

    "bisequence should be consistent with bitraverse" {
      checkAll(Arb.ior(Arb.int(), Arb.string())) { ior ->
        ior.bimap({ listOf(it) }, { listOf(it) }).bisequence() shouldBe
          ior.bitraverse({ listOf(it) }, { listOf(it) })
      }
    }

    "bitraverseOption should wrap ior in an Option" {
      checkAll(Arb.int(), Arb.string()) { a: Int, b: String ->
        val iorL: Ior<Int, String> = a.leftIor()
        val iorR: Ior<Int, String> = b.rightIor()
        val iorBoth: Ior<Int, String> = (a to b).bothIor()

        iorL.bitraverseOption({ None }, { Some(it) }) shouldBe None
        iorR.bitraverseOption({ None }, { Some(it) }) shouldBe Some(Ior.Right(b))
        iorBoth.bitraverseOption({ None }, { Some(it) }) shouldBe None
      }
    }

    "bisequenceOption should be consistent with bitraverseOption" {
      checkAll(Arb.ior(Arb.int(), Arb.string())) { ior ->
        ior.bimap({ None }, { Some(it) }).bisequenceOption() shouldBe
          ior.bitraverseOption({ None }, { Some(it) })
      }
    }

    "bitraverseEither should wrap ior in an Either" {
      checkAll(Arb.int(), Arb.string()) { a: Int, b: String ->
        val iorL: Ior<Int, String> = a.leftIor()
        val iorR: Ior<Int, String> = b.rightIor()
        val iorBoth: Ior<Int, String> = (a to b).bothIor()

        iorL.bitraverseEither({ it.left() }, { it.right() }) shouldBe Either.Left(a)
        iorR.bitraverseEither({ it.left() }, { it.right() }) shouldBe Either.Right(Ior.Right(b))
        iorBoth.bitraverseEither({ it.left() }, { it.right() }) shouldBe Either.Left(a)
      }
    }

    "bisequenceEither should be consistent with bitraverseEither" {
      checkAll(Arb.ior(Arb.int(), Arb.string())) { ior ->
        ior.bimap({ it.left() }, { it.right() }).bisequenceEither() shouldBe
          ior.bitraverseEither({ it.left() }, { it.right() })
      }
    }

})
