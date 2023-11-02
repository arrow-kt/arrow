package arrow.core

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.test.any
import arrow.core.test.either
import arrow.core.test.intSmall
import arrow.core.test.laws.MonoidLaws
import arrow.core.test.nonEmptyList
import arrow.core.test.suspendFunThatReturnsAnyLeft
import arrow.core.test.suspendFunThatReturnsAnyRight
import arrow.core.test.suspendFunThatReturnsEitherAnyOrAnyOrThrows
import arrow.core.test.suspendFunThatThrows
import arrow.core.test.testLaws
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.byte
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.float
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.nonPositiveInt
import io.kotest.property.arbitrary.short
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class EitherTest : StringSpec({
  
  val ARB = Arb.either(Arb.string(), Arb.int())

    testLaws(
      MonoidLaws("Either", 0.right(), { x, y -> x.combine(y, String::plus, Int::plus) }, ARB)
    )
    
    "isLeft should return true if Left and false if Right" {
      checkAll(Arb.int()) { a: Int ->
        val x = Left(a)
        if (x.isLeft()) x.value shouldBe a
        else fail("Left(a).isLeft() cannot be false")
        x.isRight() shouldBe false
      }
    }
    
    "isRight should return false if Left and true if Right" {
      checkAll(Arb.int()) { a: Int ->
        val x = Right(a)
        if (x.isRight()) x.value shouldBe a
        else fail("Right(a).isRight() cannot be false")
        x.isLeft() shouldBe false
      }
    }
    
    "tap applies effects returning the original value" {
      checkAll(Arb.either(Arb.long(), Arb.int())) { either ->
        var effect = 0
        val res = either.onRight { effect += 1 }
        val expected = when (either) {
          is Left -> 0
          is Right -> 1
        }
        effect shouldBe expected
        res shouldBe either
      }
    }
    
    "tapLeft applies effects returning the original value" {
      checkAll(Arb.either(Arb.long(), Arb.int())) { either ->
        var effect = 0
        val res = either.onLeft { effect += 1 }
        val expected = when (either) {
          is Left -> 1
          is Right -> 0
        }
        effect shouldBe expected
        res shouldBe either
      }
    }
    
    "fold should apply first op if Left and second op if Right" {
      checkAll(Arb.intSmall(), Arb.intSmall()) { a, b ->
        val right: Either<Int, Int> = Right(a)
        val left: Either<Int, Int> = Left(b)
        
        right.fold({ it + 2 }, { it + 1 }) shouldBe a + 1
        left.fold({ it + 2 }, { it + 1 }) shouldBe b + 2
      }
    }

    "combine two rights should return a right of the combine of the inners" {
      checkAll(Arb.string(), Arb.string()) { a: String, b: String ->
        Right(a + b) shouldBe Right(a).combine(
          Right(b),
          Int::plus,
          String::plus
        )
      }
    }
    
    "combine two lefts should return a left of the combine of the inners" {
      checkAll(Arb.string(), Arb.string()) { a: String, b: String ->
        Left(a + b) shouldBe Left(a).combine(
          Left(b),
          String::plus,
          Int::plus
        )
      }
    }
    
    "combine a right and a left should return left" {
      checkAll(Arb.string(), Arb.string()) { a: String, b: String ->
        Left(a) shouldBe Left(a).combine(Right(b), String::plus, String::plus)
        Left(a) shouldBe Right(b).combine(Left(a), String::plus, String::plus)
      }
    }
    
    "getOrElse should return value" {
      checkAll(Arb.int(), Arb.int()) { a: Int, b: Int ->
        Right(a).getOrElse { b } shouldBe a
        Left(a).getOrElse { b } shouldBe b
      }
    }
    
    "getOrNull should return value" {
      checkAll(Arb.int()) { a: Int ->
        Right(a).getOrNull() shouldBe a
      }
    }
    
    "getOrNone should return Some(value)" {
      checkAll(Arb.int()) { a: Int ->
        Right(a).getOrNone() shouldBe Some(a)
      }
    }
    
    "getOrNone should return None when left" {
      checkAll(Arb.string()) { a: String ->
        Left(a).getOrNone() shouldBe None
      }
    }

    "swap should interchange values" {
      checkAll(Arb.int()) { a: Int ->
        Left(a).swap() shouldBe Right(a)
        Right(a).swap() shouldBe Left(a)
      }
    }

    "map should alter right instance only" {
      checkAll(Arb.intSmall(), Arb.intSmall()) { a, b ->
        val right: Either<Int, Int> = Right(a)
        val left: Either<Int, Int> = Left(b)
        
        right.map { it + 1 } shouldBe Right(a + 1)
        left.map { it + 1 } shouldBe left
      }
    }
    
    "mapLeft should alter left instance only" {
      checkAll(Arb.intSmall(), Arb.intSmall()) { a, b ->
        val right: Either<Int, Int> = Right(a)
        val left: Either<Int, Int> = Left(b)
        
        right.mapLeft { it + 1 } shouldBe right
        left.mapLeft { it + 1 } shouldBe Left(b + 1)
      }
    }

    "flatMap should map right instance only" {
      checkAll(Arb.intSmall(), Arb.intSmall()) { a, b ->
        val right: Either<Int, Int> = Right(a)
        val left: Either<Int, Int> = Left(b)
        
        right.flatMap { Right(it + 1) } shouldBe Right(a + 1)
        left.flatMap { Right(it + 1) } shouldBe left
      }
    }

    "handleErrorWith should handle left instance otherwise return Right" {
      checkAll(Arb.int(), Arb.string()) { a: Int, b: String ->
        Left(a).recover<Int, String, String> { Right(b).bind() } shouldBe Right(b)
        Right(a).recover { Right(a + 1).bind() } shouldBe Right(a)
        Left(a).recover { Left(b).bind() } shouldBe Left(b)
      }
    }
    
    "catch should return Right(result) when f does not throw" {
      Either.catch { 1 } shouldBe Right(1)
    }
    
    "catch should return Left(result) when f throws" {
      val exception = Exception("Boom!")
      Either.catch { throw exception } shouldBe Left(exception)
    }

  "zipOrAccumulate results in all Right transformed, or all Left combined according to combine" {
    checkAll(
      Arb.either(Arb.string(), Arb.short()),
      Arb.either(Arb.string(), Arb.byte()),
      Arb.either(Arb.string(), Arb.int()),
      Arb.either(Arb.string(), Arb.long()),
      Arb.either(Arb.string(), Arb.float()),
      Arb.either(Arb.string(), Arb.double()),
      Arb.either(Arb.string(), Arb.char()),
      Arb.either(Arb.string(), Arb.string()),
      Arb.either(Arb.string(), Arb.boolean())
    ) { a, b, c, d, e, f, g, h, i ->
      val res = Either.zipOrAccumulate({ e1, e2 -> "$e1$e2" }, a, b, c, d, e, f, g, h, i, ::Tuple9)
      val all = listOf(a, b, c, d, e, f, g, h, i)

      val expected = if (all.any { it.isLeft() }) {
        all.filterIsInstance<Left<String>>().fold("") { acc, t -> "$acc${t.value}" }.left()
      } else {
        all.filterIsInstance<Right<Any?>>().map { it.value }.let {
          Tuple9(it[0], it[1], it[2], it[3], it[4], it[5], it[6], it[7], it[8]).right()
        }
      }

      res shouldBe expected
    }
  }

  "zipOrAccumulate without Semigroup results in all Right transformed, or all Left in a NonEmptyList" {
    checkAll(
      Arb.either(Arb.string(), Arb.short()),
      Arb.either(Arb.string(), Arb.byte()),
      Arb.either(Arb.string(), Arb.int()),
      Arb.either(Arb.string(), Arb.long()),
      Arb.either(Arb.string(), Arb.float()),
      Arb.either(Arb.string(), Arb.double()),
      Arb.either(Arb.string(), Arb.char()),
      Arb.either(Arb.string(), Arb.string()),
      Arb.either(Arb.string(), Arb.boolean())
    ) { a, b, c, d, e, f, g, h, i ->
      val res = Either.zipOrAccumulate(a, b, c, d, e, f, g, h, i, ::Tuple9)
      val all = listOf(a, b, c, d, e, f, g, h, i)

      val expected = if (all.any { it.isLeft() }) {
        all.filterIsInstance<Left<String>>().map { it.value }.toNonEmptyListOrNull()!!.left()
      } else {
        all.filterIsInstance<Right<Any?>>().map { it.value }.let {
          Tuple9(it[0], it[1], it[2], it[3], it[4], it[5], it[6], it[7], it[8]).right()
        }
      }

      res shouldBe expected
    }
  }

  "zipOrAccumulate EitherNel results in all Right transformed, or all Left in a NonEmptyList" {
    checkAll(
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.short()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.byte()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.int()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.long()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.float()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.double()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.char()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.string()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.boolean())
    ) { a, b, c, d, e, f, g, h, i ->
      val res = Either.zipOrAccumulate(a, b, c, d, e, f, g, h, i, ::Tuple9)
      val all = listOf(a, b, c, d, e, f, g, h, i)

      val expected = if (all.any { it.isLeft() }) {
        all.filterIsInstance<Left<NonEmptyList<String>>>()
          .flatMap { it.value }
          .toNonEmptyListOrNull()!!.left()
      } else {
        all.filterIsInstance<Right<Any?>>().map { it.value }.let {
          Tuple9(it[0], it[1], it[2], it[3], it[4], it[5], it[6], it[7], it[8]).right()
        }
      }

      res shouldBe expected
    }
  }
})

@Suppress("RedundantSuspendModifier", "UNUSED_PARAMETER")
suspend fun handleWithPureFunction(a: Any, b: Any): Either<Throwable, Any> =
  b.right()

@Suppress("RedundantSuspendModifier", "UNUSED_PARAMETER")
suspend fun handleWithPureFunction(throwable: Throwable): Either<Throwable, Unit> =
  Unit.right()

@Suppress("RedundantSuspendModifier", "UNUSED_PARAMETER")
private suspend fun <A> throwException(
  a: A,
): Either<Throwable, Any> =
  throw RuntimeException("An Exception is thrown while handling the result of the supplied function.")
