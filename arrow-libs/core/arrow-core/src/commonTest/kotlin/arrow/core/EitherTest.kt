package arrow.core

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.test.any
import arrow.core.test.either
import arrow.core.test.intSmall
import arrow.core.test.laws.MonoidLaws
import arrow.core.test.suspendFunThatReturnsAnyLeft
import arrow.core.test.suspendFunThatReturnsAnyRight
import arrow.core.test.suspendFunThatReturnsEitherAnyOrAnyOrThrows
import arrow.core.test.suspendFunThatThrows
import arrow.core.test.testLaws
import arrow.typeclasses.Monoid
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.nonPositiveInt
import io.kotest.property.checkAll

class EitherTest : StringSpec({
  
  val ARB = Arb.either(Arb.string(), Arb.int())

    testLaws(
      MonoidLaws.laws(Monoid.either(Monoid.string(), Monoid.int()), ARB),
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
        val res = either.tap { effect += 1 }
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
        val res = either.tapLeft { effect += 1 }
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
    
    "foldLeft should return initial if Left and apply op if Right" {
      checkAll<Int, Int, Int>(Arb.intSmall(), Arb.intSmall(), Arb.intSmall()) { a, b, c ->
        Right(a).foldLeft(c, Int::plus) shouldBe c + a
        Left(b).foldLeft(c, Int::plus) shouldBe c
      }
    }
    
    "foldMap should return the empty of the inner type if Left and apply op if Right" {
      checkAll(Arb.intSmall(), Arb.intSmall()) { a, b ->
        val left: Either<Int, Int> = Left(b)
        
        Right(a).foldMap(Monoid.int()) { it + 1 } shouldBe a + 1
        left.foldMap(Monoid.int()) { it + 1 } shouldBe Monoid.int().empty()
      }
    }
    
    "bifoldLeft should apply first op if Left and apply second op if Right" {
      checkAll(Arb.intSmall(), Arb.intSmall(), Arb.intSmall()) { a, b, c ->
        Right(a).bifoldLeft(c, Int::plus, Int::times) shouldBe a * c
        Left(b).bifoldLeft(c, Int::plus, Int::times) shouldBe b + c
      }
    }
    
    "bifoldMap should apply first op if Left and apply second op if Right" {
      checkAll(Arb.intSmall(), Arb.intSmall()) { a, b ->
        val right: Either<Int, Int> = Right(a)
        val left: Either<Int, Int> = Left(b)
        
        right.bifoldMap(Monoid.int(), { it + 2 }, { it + 1 }) shouldBe a + 1
        left.bifoldMap(Monoid.int(), { it + 2 }, { it + 1 }) shouldBe b + 2
      }
    }
    
    "fromNullable should lift value as a Right if it is not null" {
      checkAll(Arb.int()) { a: Int ->
        Either.fromNullable(a) shouldBe Right(a)
      }
    }
    
    "fromNullable should lift value as a Left(Unit) if it is null" {
      Either.fromNullable(null) shouldBe Left(Unit)
    }
    
    "empty should return a Right of the empty of the inner type" {
      Right(Monoid.string().empty()) shouldBe Monoid.either(Monoid.string(), Monoid.string()).empty()
    }
    
    "combine two rights should return a right of the combine of the inners" {
      checkAll(Arb.string(), Arb.string()) { a: String, b: String ->
        Monoid.string().run { Right(a.combine(b)) } shouldBe Right(a).combine(
          Monoid.string(),
          Monoid.string(),
          Right(b)
        )
      }
    }
    
    "combine two lefts should return a left of the combine of the inners" {
      checkAll(Arb.string(), Arb.string()) { a: String, b: String ->
        Monoid.string().run { Left(a.combine(b)) } shouldBe Left(a).combine(Monoid.string(), Monoid.string(), Left(b))
      }
    }
    
    "combine a right and a left should return left" {
      checkAll(Arb.string(), Arb.string()) { a: String, b: String ->
        Left(a) shouldBe Left(a).combine(Monoid.string(), Monoid.string(), Right(b))
        Left(a) shouldBe Right(b).combine(Monoid.string(), Monoid.string(), Left(a))
      }
    }
    
    "getOrElse should return value" {
      checkAll(Arb.int(), Arb.int()) { a: Int, b: Int ->
        Right(a).getOrElse { b } shouldBe a
        Left(a).getOrElse { b } shouldBe b
      }
    }
    
    "orNull should return value" {
      checkAll(Arb.int()) { a: Int ->
        Right(a).orNull() shouldBe a
      }
    }
    
    "orNone should return Some(value)" {
      checkAll(Arb.int()) { a: Int ->
        Right(a).orNone() shouldBe Some(a)
      }
    }
    
    "orNone should return None when left" {
      checkAll(Arb.string()) { a: String ->
        Left(a).orNone() shouldBe None
      }
    }
    
    "getOrHandle should return value" {
      checkAll(Arb.int(), Arb.int()) { a: Int, b: Int ->
        Right(a).getOrHandle { b } shouldBe a
        Left(a).getOrHandle { it + b } shouldBe a + b
      }
    }
    
    "filterOrElse should filter values" {
      checkAll(Arb.intSmall(), Arb.intSmall()) { a, b ->
        val left: Either<Int, Int> = Left(a)
        
        Right(a).filterOrElse({ it > a - 1 }, { b }) shouldBe Right(a)
        Right(a).filterOrElse({ it > a + 1 }, { b }) shouldBe Left(b)
        left.filterOrElse({ it > a - 1 }, { b }) shouldBe Left(a)
        left.filterOrElse({ it > a + 1 }, { b }) shouldBe Left(a)
      }
    }
    
    "filterOrOther should filter values" {
      checkAll(Arb.intSmall(), Arb.intSmall()) { a, b ->
        val left: Either<Int, Int> = Left(a)
        
        Right(a).filterOrOther({ it > a - 1 }, { b + a }) shouldBe Right(a)
        Right(a).filterOrOther({ it > a + 1 }, { b + a }) shouldBe Left(b + a)
        left.filterOrOther({ it > a - 1 }, { b + a }) shouldBe Left(a)
        left.filterOrOther({ it > a + 1 }, { b + a }) shouldBe Left(a)
      }
    }
    
    "leftIfNull should return Left if Right value is null of if Either is Left" {
      checkAll(Arb.int(), Arb.int()) { a: Int, b: Int ->
        Right(a).leftIfNull { b } shouldBe Right(a)
        Right(null).leftIfNull { b } shouldBe Left(b)
        Left(a).leftIfNull { b } shouldBe Left(a)
      }
    }
    
    "exists should apply predicate to Right only" {
      checkAll(Arb.intSmall()) { a ->
        val left: Either<Int, Int> = Left(a)
        
        Right(a).exists { it > a - 1 } shouldBe true
        !Right(a).exists { it > a + 1 } shouldBe true
        !left.exists { it > a - 1 } shouldBe true
        !left.exists { it > a + 1 } shouldBe true
      }
    }
    
    "rightIfNotNull should return Left if value is null or Right of value when not null" {
      checkAll(Arb.int(), Arb.int()) { a: Int, b: Int ->
        null.rightIfNotNull { b } shouldBe Left(b)
        a.rightIfNotNull { b } shouldBe Right(a)
      }
    }
    
    "rightIfNull should return Left if value is not null or Right of value when null" {
      checkAll(Arb.int(), Arb.int()) { a: Int, b: Int ->
        a.rightIfNull { b } shouldBe Left(b)
        null.rightIfNull { b } shouldBe Right(null)
      }
    }
    
    "swap should interchange values" {
      checkAll(Arb.int()) { a: Int ->
        Left(a).swap() shouldBe Right(a)
        Right(a).swap() shouldBe Left(a)
      }
    }
    
    "orNull should convert" {
      checkAll(Arb.int()) { a: Int ->
        val left: Either<Int, Int> = Left(a)
        
        Right(a).orNull() shouldBe a
        left.orNull() shouldBe null
      }
    }
    
    "contains should check value" {
      checkAll(Arb.intSmall(), Arb.intSmall()) { a, b ->
        require(Right(a).contains(a)) { "Expected ${Right(a)}.contains($a) to be true, but it was false." }
        if (a != b) require(!Right(a).contains(b)) { "Expected ${Right(a)}.contains($b) to be false, but it was true." }
        else require(Right(a).contains(b)) { "Expected ${Right(a)}.contains($b) to be true, but it was false." }
        require(!Left(a).contains(a)) { "Expected ${Left(a)}.contains($a) to be false, but it was true." }
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
    
    "bimap should alter left or right instance accordingly" {
      checkAll(Arb.intSmall(), Arb.intSmall()) { a, b ->
        val right: Either<Int, Int> = Right(a)
        val left: Either<Int, Int> = Left(b)
        
        right.bimap({ it + 2 }, { it + 1 }) shouldBe Right(a + 1)
        left.bimap({ it + 2 }, { it + 1 }) shouldBe Left(b + 2)
      }
    }
    
    "replicate should return Right(empty list) when n <= 0" {
      checkAll(
        Arb.nonPositiveInt(),
        Arb.int(0..100)
      ) { n: Int, a: Int ->
        val expected: Either<Int, List<Int>> = Right(emptyList())
        
        Right(a).replicate(n) shouldBe expected
        Left(a).replicate(n) shouldBe expected
      }
    }
    
    "replicate should return Right(list of repeated value size n) when Right and n is positive" {
      checkAll(
        Arb.int(1..10),
        Arb.int()
      ) { n: Int, a: Int ->
        Right(a).replicate(n) shouldBe Right(List(n) { a })
        Left(a).replicate(n) shouldBe Left(a)
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
    
    "conditionally should create right instance only if test is true" {
      checkAll(Arb.boolean(), Arb.int(), Arb.string()) { t: Boolean, i: Int, s: String ->
        val expected = if (t) Right(i) else Left(s)
        Either.conditionally(t, { s }, { i }) shouldBe expected
      }
    }
    
    "handleErrorWith should handle left instance otherwise return Right" {
      checkAll(Arb.int(), Arb.string()) { a: Int, b: String ->
        Left(a).handleErrorWith { Right(b) } shouldBe Right(b)
        Right(a).handleErrorWith { Right(b) } shouldBe Right(a)
        Left(a).handleErrorWith { Left(b) } shouldBe Left(b)
      }
    }
    
    "catch should return Right(result) when f does not throw" {
      Either.catch { 1 } shouldBe Right(1)
    }
    
    "catch should return Left(result) when f throws" {
      val exception = Exception("Boom!")
      Either.catch { throw exception } shouldBe Left(exception)
    }
    
    "catchAndFlatten should return Right(result) when f does not throw" {
      Either.catchAndFlatten { Right(1) } shouldBe Right(1)
    }
    
    "catchAndFlatten should return Left(result) when f throws" {
      val exception = Exception("Boom!")
      Either.catchAndFlatten<Int> { throw exception } shouldBe Left(exception)
    }
    
    "resolve should yield a result when deterministic functions are used as handlers" {
      checkAll(
        Arb.suspendFunThatReturnsEitherAnyOrAnyOrThrows(),
        Arb.any()
      ) { f: suspend () -> Either<Any, Any>, returnObject: Any ->
        val result =
          Either.resolve(
            f = { f() },
            success = { a -> handleWithPureFunction(a, returnObject) },
            error = { e -> handleWithPureFunction(e, returnObject) },
            throwable = { t -> handleWithPureFunction(t, returnObject) },
            unrecoverableState = { handleWithPureFunction(it) }
          )
        result shouldBe returnObject
      }
    }
    
    "resolve should yield a result when an exception is thrown in the success supplied function" {
      checkAll(
        Arb.suspendFunThatReturnsAnyRight(),
        Arb.any()
      ) { f: suspend () -> Either<Any, Any>, returnObject: Any ->
        val result =
          Either.resolve(
            f = { f() },
            success = { throwException(it) },
            error = { e -> handleWithPureFunction(e, returnObject) },
            throwable = { t -> handleWithPureFunction(t, returnObject) },
            unrecoverableState = { handleWithPureFunction(it) }
          )
        result shouldBe returnObject
      }
    }
    
    "resolve should yield a result when an exception is thrown in the error supplied function" {
      checkAll(
        Arb.suspendFunThatReturnsAnyLeft(),
        Arb.any()
      ) { f: suspend () -> Either<Any, Any>, returnObject: Any ->
        val result =
          Either.resolve(
            f = { f() },
            success = { a -> handleWithPureFunction(a, returnObject) },
            error = { throwException(it) },
            throwable = { t -> handleWithPureFunction(t, returnObject) },
            unrecoverableState = { handleWithPureFunction(it) }
          )
        result shouldBe returnObject
      }
    }
    
    "resolve should throw a Throwable when any exception is thrown in the throwable supplied function" {
      checkAll(
        Arb.suspendFunThatThrows()
      ) { f: suspend () -> Either<Any, Any> ->
        shouldThrow<Throwable> {
          Either.resolve(
            f = { f() },
            success = { throwException(it) },
            error = { throwException(it) },
            throwable = { throwException(it) },
            unrecoverableState = { handleWithPureFunction(it) }
          )
        }
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
