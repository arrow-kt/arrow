package arrow.core

import arrow.core.computations.EitherEffect
import arrow.core.computations.RestrictedEitherEffect
import arrow.core.computations.either
import arrow.core.Either.Right
import arrow.core.Either.Left
import arrow.core.test.UnitSpec
import arrow.core.test.generators.any
import arrow.core.test.generators.either
import arrow.core.test.generators.intSmall
import arrow.core.test.generators.suspendFunThatReturnsAnyLeft
import arrow.core.test.generators.suspendFunThatReturnsAnyRight
import arrow.core.test.generators.suspendFunThatReturnsEitherAnyOrAnyOrThrows
import arrow.core.test.generators.suspendFunThatThrows
import arrow.core.test.generators.suspendFunThatThrowsFatalThrowable
import arrow.core.test.laws.FxLaws
import arrow.core.test.laws.MonoidLaws
import arrow.typeclasses.Monoid
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import kotlinx.coroutines.runBlocking

class EitherTest : UnitSpec() {

  val GEN = Gen.either(Gen.string(), Gen.int())

  init {
    testLaws(
      MonoidLaws.laws(Monoid.either(Monoid.string(), Monoid.int()), GEN),
      FxLaws.suspended<EitherEffect<String, *>, Either<String, Int>, Int>(Gen.int().map(::Right), GEN.map { it }, Either<String, Int>::equals, either::invoke) {
        it.bind()
      },
      FxLaws.eager<RestrictedEitherEffect<String, *>, Either<String, Int>, Int>(Gen.int().map(::Right), GEN.map { it }, Either<String, Int>::equals, either::eager) {
        it.bind()
      }
    )

    "fromNullable should lift value as a Right if it is not null" {
      forAll { a: Int ->
        Either.fromNullable(a) == Right(a)
      }
    }

    "fromNullable should lift value as a Left(Unit) if it is null" {
      Either.fromNullable(null) shouldBe Left(Unit)
    }

    "empty should return a Right of the empty of the inner type" {
      forAll { _: String ->
        Right(Monoid.string().empty()) == Monoid.either(Monoid.string(), Monoid.string()).empty()
      }
    }

    "combine two rights should return a right of the combine of the inners" {
      forAll { a: String, b: String ->
        Monoid.string().run { Right(a.combine(b)) } == Right(a).combine(Monoid.string(), Monoid.string(), Right(b))
      }
    }

    "combine two lefts should return a left of the combine of the inners" {
      forAll { a: String, b: String ->
        Monoid.string().run { Left(a.combine(b)) } == Left(a).combine(Monoid.string(), Monoid.string(), Left(b))
      }
    }

    "combine a right and a left should return left" {
      forAll { a: String, b: String ->
        Left(a) == Left(a).combine(Monoid.string(), Monoid.string(), Right(b)) &&
          Left(a) == Right(b).combine(Monoid.string(), Monoid.string(), Left(a))
      }
    }

    "getOrElse should return value" {
      forAll { a: Int, b: Int ->
        Right(a).getOrElse { b } == a &&
          Left(a).getOrElse { b } == b
      }
    }

    "orNull should return value" {
      forAll { a: Int ->
        Either.Right(a).orNull() == a
      }
    }

    "getOrHandle should return value" {
      forAll { a: Int, b: Int ->
        Right(a).getOrHandle { b } == a &&
          Left(a).getOrHandle { it + b } == a + b
      }
    }

    "filterOrElse should filter values" {
      forAll(Gen.intSmall(), Gen.intSmall()) { a: Int, b: Int ->
        val left: Either<Int, Int> = Left(a)

        Right(a).filterOrElse({ it > a - 1 }, { b }) == Right(a) &&
          Right(a).filterOrElse({ it > a + 1 }, { b }) == Left(b) &&
          left.filterOrElse({ it > a - 1 }, { b }) == Left(a) &&
          left.filterOrElse({ it > a + 1 }, { b }) == Left(a)
      }
    }

    "filterOrOther should filter values" {
      forAll(Gen.intSmall(), Gen.intSmall()) { a: Int, b: Int ->
        val left: Either<Int, Int> = Left(a)

        Right(a).filterOrOther({ it > a - 1 }, { b + a }) == Right(a) &&
          Right(a).filterOrOther({ it > a + 1 }, { b + a }) == Left(b + a) &&
          left.filterOrOther({ it > a - 1 }, { b + a }) == Left(a) &&
          left.filterOrOther({ it > a + 1 }, { b + a }) == Left(a)
      }
    }

    "leftIfNull should return Left if Right value is null of if Either is Left" {
      forAll { a: Int, b: Int ->
        Right(a).leftIfNull { b } == Right(a) &&
          Right(null).leftIfNull { b } == Left(b) &&
          Left(a).leftIfNull { b } == Left(a)
      }
    }

    "rightIfNotNull should return Left if value is null or Right of value when not null" {
      forAll { a: Int, b: Int ->
        null.rightIfNotNull { b } == Left(b) &&
          a.rightIfNotNull { b } == Right(a)
      }
    }

    "rightIfNull should return Left if value is not null or Right of value when null" {
      forAll { a: Int, b: Int ->
        a.rightIfNull { b } == Left(b) &&
          null.rightIfNull { b } == Right(null)
      }
    }

    "swap should interchange values" {
      forAll { a: Int ->
        Left(a).swap() == Right(a) &&
          Right(a).swap() == Left(a)
      }
    }

    "orNull should convert" {
      forAll { a: Int ->
        Right(a).orNull() == a &&
          Left(a).orNull() == null
      }
    }

    "contains should check value" {
      forAll(Gen.intSmall(), Gen.intSmall()) { a: Int, b: Int ->
        val rightContains = Right(a).contains(a)
        // We need to check that a != b or this test will result in a false negative
        val rightDoesntContains = if (a != b) !Right(a).contains(b) else true
        val leftNeverContains = !Left(a).contains(a)

        rightContains && rightDoesntContains && leftNeverContains
      }
    }

    "mapLeft should alter left instance only" {
      forAll(Gen.intSmall(), Gen.intSmall()) { a: Int, b: Int ->
        val right: Either<Int, Int> = Right(a)
        val left: Either<Int, Int> = Left(b)
        right.mapLeft { it + 1 } == right && left.mapLeft { it + 1 } == Left(b + 1)
      }
    }

    "conditionally should create right instance only if test is true" {
      forAll { t: Boolean, i: Int, s: String ->
        val expected = if (t) Right(i) else Left(s)
        Either.conditionally(t, { s }, { i }) == expected
      }
    }

    "handleErrorWith should handle left instance otherwise return Right" {
      forAll { a: Int, b: String ->
        Left(a).handleErrorWith { Right(b) } == Right(b) &&
          Right(a).handleErrorWith { Right(b) } == Right(a) &&
          Left(a).handleErrorWith { Left(b) } == Left(b)
      }
    }

    "catch should return Right(result) when f does not throw" {
      suspend fun loadFromNetwork(): Int = 1
      Either.catch { loadFromNetwork() } shouldBe Right(1)
    }

    "catch should return Left(result) when f throws" {
      val exception = Exception("Boom!")
      suspend fun loadFromNetwork(): Int = throw exception
      Either.catch { loadFromNetwork() } shouldBe Left(exception)
    }

    "catchAndFlatten should return Right(result) when f does not throw" {
      suspend fun loadFromNetwork(): Either<Throwable, Int> = Right(1)
      Either.catchAndFlatten { loadFromNetwork() } shouldBe Right(1)
    }

    "catchAndFlatten should return Left(result) when f throws" {
      val exception = Exception("Boom!")
      suspend fun loadFromNetwork(): Either<Throwable, Int> = throw exception
      Either.catchAndFlatten { loadFromNetwork() } shouldBe Left(exception)
    }

    "resolve should yield a result when deterministic functions are used as handlers" {
      forAll(
        Gen.suspendFunThatReturnsEitherAnyOrAnyOrThrows(),
        Gen.any()
      ) { f: suspend () -> Either<Any, Any>, returnObject: Any ->

        runBlocking {
          val result =
            Either.resolve(
              f = { f() },
              success = { a -> handleWithPureFunction(a, returnObject) },
              error = { e -> handleWithPureFunction(e, returnObject) },
              throwable = { t -> handleWithPureFunction(t, returnObject) },
              unrecoverableState = { handleWithPureFunction(it) }
            )
          result == returnObject
        }
      }
    }

    "resolve should throw a Throwable when a fatal Throwable is thrown" {
      forAll(
        Gen.suspendFunThatThrowsFatalThrowable(),
        Gen.any()
      ) { f: suspend () -> Either<Any, Any>, returnObject: Any ->

        runBlocking {
          shouldThrow<Throwable> {
            Either.resolve(
              f = { f() },
              success = { a -> handleWithPureFunction(a, returnObject) },
              error = { e -> handleWithPureFunction(e, returnObject) },
              throwable = { t -> handleWithPureFunction(t, returnObject) },
              unrecoverableState = { handleWithPureFunction(it) }
            )
          }
        }
        true
      }
    }

    "resolve should yield a result when an exception is thrown in the success supplied function" {
      forAll(
        Gen.suspendFunThatReturnsAnyRight(),
        Gen.any()
      ) { f: suspend () -> Either<Any, Any>, returnObject: Any ->

        runBlocking {
          val result =
            Either.resolve(
              f = { f() },
              success = { throwException(it) },
              error = { e -> handleWithPureFunction(e, returnObject) },
              throwable = { t -> handleWithPureFunction(t, returnObject) },
              unrecoverableState = { handleWithPureFunction(it) }
            )
          result == returnObject
        }
      }
    }

    "resolve should yield a result when an exception is thrown in the error supplied function" {
      forAll(
        Gen.suspendFunThatReturnsAnyLeft(),
        Gen.any()
      ) { f: suspend () -> Either<Any, Any>, returnObject: Any ->

        runBlocking {
          val result =
            Either.resolve(
              f = { f() },
              success = { a -> handleWithPureFunction(a, returnObject) },
              error = { throwException(it) },
              throwable = { t -> handleWithPureFunction(t, returnObject) },
              unrecoverableState = { handleWithPureFunction(it) }
            )
          result == returnObject
        }
      }
    }

    "resolve should throw a Throwable when any exception is thrown in the throwable supplied function" {
      forAll(
        Gen.suspendFunThatThrows()
      ) { f: suspend () -> Either<Any, Any> ->

        runBlocking {
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
        true
      }
    }
  }
}

@Suppress("RedundantSuspendModifier", "UNUSED_PARAMETER")
suspend fun handleWithPureFunction(a: Any, b: Any): Either<Throwable, Any> =
  b.right()

@Suppress("RedundantSuspendModifier", "UNUSED_PARAMETER")
suspend fun handleWithPureFunction(throwable: Throwable): Either<Throwable, Unit> =
  Unit.right()

@Suppress("RedundantSuspendModifier", "UNUSED_PARAMETER")
private suspend fun <A> throwException(
  a: A
): Either<Throwable, Any> =
  throw RuntimeException("An Exception is thrown while handling the result of the supplied function.")
