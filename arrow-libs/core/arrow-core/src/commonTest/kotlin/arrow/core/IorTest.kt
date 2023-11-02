package arrow.core

import arrow.core.test.ior
import arrow.core.test.laws.SemigroupLaws
import arrow.core.test.testLaws
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class IorTest : StringSpec({

  val ARB = Arb.ior(Arb.string(), Arb.int())

  testLaws(
    SemigroupLaws("Ior", { a, b ->
      a.combine(b, String::plus, Int::plus)
    }, ARB)
  )

  "map() should just right side of an Ior" {
    checkAll(Arb.int(), Arb.string()) { a: Int, b: String ->
      Ior.Left(a).map { l: String -> l.length } shouldBe Ior.Left(a)
      Ior.Right(b).map { it.length } shouldBe Ior.Right(b.length)
      Ior.Both(a, b).map { it.length } shouldBe Ior.Both(a, b.length)
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

  "toEither() should convert values into a valid Either" {
    checkAll(Arb.int(), Arb.string()) { a: Int, b: String ->
      Ior.Left(a).toEither() shouldBe Either.Left(a)
      Ior.Right(b).toEither() shouldBe Either.Right(b)
      Ior.Both(a, b).toEither() shouldBe Either.Right(b)
    }
  }

  "getOrNull() should convert right values into a nullable, or return null if left" {
    checkAll(Arb.int(), Arb.string()) { a: Int, b: String ->
      Ior.Left(a).getOrNull() shouldBe null
      Ior.Right(b).getOrNull() shouldBe b
      Ior.Both(a, b).getOrNull() shouldBe b
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

  "leftNel() should build a correct Ior" {
    checkAll(Arb.int()) { a: Int ->
      Ior.leftNel<Int, Nothing>(a) shouldBe Ior.Left(nonEmptyListOf(a))
    }
  }

  "bothNel() should build a correct Ior" {
    checkAll(Arb.int(), Arb.string()) { a: Int, b: String ->
      Ior.bothNel(a, b) shouldBe Ior.Both(nonEmptyListOf(a), b)
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
    val iorResult = ior1.flatMap(Int::plus) { Ior.Left(7) }
    iorResult shouldBe Ior.Left(10)
  }

  "Ior.monad.flatMap should combine Both values" {
    val ior1 = Ior.Both(3, "Hello, world!")
    val iorResult1 = ior1.flatMap(Int::plus) { Ior.Both(7, "Again!") }
    iorResult1 shouldBe Ior.Both(10, "Again!")

    val iorResult2 = ior1.flatMap(Int::plus) { Ior.Right("Again!") }
    iorResult2 shouldBe Ior.Both(3, "Again!")
  }

  "combine cases for Semigroup" {
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
      a.combine(b, String::plus, Int::plus) shouldBe expectedResult
    }
  }

  "isLeft() should return true with Left and false otherwise"{
    checkAll(Arb.int(), Arb.string()){ a, b ->
      Ior.Left(a).isLeft() shouldBe true
      Ior.Right(b).isLeft() shouldBe false
      Ior.Both(a, b).isLeft() shouldBe false
    }
  }

  "isRight() should return true with Right and false otherwise" {
    checkAll(Arb.int(), Arb.string()) { a, b ->
      Ior.Left(a).isRight() shouldBe false
      Ior.Right(b).isRight() shouldBe true
      Ior.Both(a, b).isRight() shouldBe false
    }
  }

  "isBoth() should return true with Both and false otherwise" {
    checkAll(Arb.int(), Arb.string()) { a, b ->
      Ior.Left(a).isBoth() shouldBe false
      Ior.Right(b).isBoth() shouldBe false
      Ior.Both(a, b).isBoth() shouldBe true
    }
  }

  "isLeft(predicate) should return true with Left, if satisfies the predicate, and false otherwise" {
    checkAll(Arb.int(), Arb.string()) { a, b ->
      val predicate = { i: Int -> i % 2 == 0 }

      if (predicate(a)) Ior.Left(a).isLeft(predicate) shouldBe true
      else Ior.Left(a).isLeft(predicate) shouldBe false

      Ior.Right(b).isLeft(predicate) shouldBe false
      Ior.Both(a, b).isLeft(predicate) shouldBe false
    }
  }

  "isRight(predicate) should return true with Right, if satisfies the predicate, and false otherwise" {
    checkAll(Arb.int(), Arb.string()) { a, b ->
      val predicate = { s: String -> s.length % 2 == 0 }

      if (predicate(b)) Ior.Right(b).isRight(predicate) shouldBe true
      else Ior.Right(b).isRight(predicate) shouldBe false

      Ior.Left(a).isRight(predicate) shouldBe false
      Ior.Both(a, b).isRight(predicate) shouldBe false
    }
  }

  "isBoth(predicate) should return true with Both, if satisfies the predicate, and false otherwise" {
    checkAll(Arb.int(), Arb.string()) { a, b ->
      val leftPredicate = { i: Int -> i % 2 == 0 }
      val rightPredicate = { s: String -> s.length % 2 == 0 }
      if (leftPredicate(a) && rightPredicate(b)) Ior.Both(a, b).isBoth(leftPredicate, rightPredicate) shouldBe true
      else Ior.Both(a, b).isBoth(leftPredicate, rightPredicate) shouldBe false

      Ior.Left(a).isBoth(leftPredicate, rightPredicate) shouldBe false
      Ior.Right(b).isBoth(leftPredicate, rightPredicate) shouldBe false
    }
  }

  "widen should retype Right" {
    checkAll(Arb.int(), Arb.string()) { a, b ->
      val ior = Ior.Both(a, b)
      ior.widen<Int, CharSequence, String>().shouldBeInstanceOf<Ior.Both<Int, CharSequence>>()
    }
  }

  "compareTo should compare 2 Ior" {
    val left1 = Ior.Left(1)
    val left2 = Ior.Left(2)
    val right1 = Ior.Right(1)
    val right2 = Ior.Right(2)
    val both11 = Ior.Both(1, 1)
    val both22 = Ior.Both(2, 2)
    left1.compareTo(left2) shouldBe -1
    left1.compareTo(left1) shouldBe 0
    left2.compareTo(left1) shouldBe 1
    left1.compareTo(right1) shouldBe -1
    left1.compareTo(both11) shouldBe -1
    right1.compareTo(right2) shouldBe -1
    right1.compareTo(right1) shouldBe 0
    right2.compareTo(right1) shouldBe 1
    right1.compareTo(left1) shouldBe 1
    right1.compareTo(both11) shouldBe -1
    both11.compareTo(both22) shouldBe -1
    both11.compareTo(both11) shouldBe 0
    both22.compareTo(both11) shouldBe 1
    both11.compareTo(left1) shouldBe 1
    both11.compareTo(right1) shouldBe 1
  }
})
