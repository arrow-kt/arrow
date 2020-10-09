package arrow.core

import arrow.Kind
import arrow.core.computations.either
import arrow.core.extensions.combine
import arrow.core.extensions.either.applicative.applicative
import arrow.core.extensions.either.bicrosswalk.bicrosswalk
import arrow.core.extensions.either.bifunctor.bifunctor
import arrow.core.extensions.either.bitraverse.bitraverse
import arrow.core.extensions.either.eq.eq
import arrow.core.extensions.either.eqK.eqK
import arrow.core.extensions.either.eqK2.eqK2
import arrow.core.extensions.either.functor.functor
import arrow.core.extensions.either.hash.hash
import arrow.core.extensions.either.monad.monad
import arrow.core.extensions.either.monadError.monadError
import arrow.core.extensions.either.monoid.monoid
import arrow.core.extensions.either.order.order
import arrow.core.extensions.either.semigroupK.semigroupK
import arrow.core.extensions.either.show.show
import arrow.core.extensions.either.traverse.traverse
import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.extensions.id.eq.eq
import arrow.core.extensions.monoid
import arrow.core.extensions.order
import arrow.core.extensions.show
import arrow.core.test.UnitSpec
import arrow.core.test.generators.any
import arrow.core.test.generators.either
import arrow.core.test.generators.genK
import arrow.core.test.generators.genK2
import arrow.core.test.generators.id
import arrow.core.test.generators.intSmall
import arrow.core.test.generators.suspendFunThatReturnsAnyLeft
import arrow.core.test.generators.suspendFunThatReturnsAnyRight
import arrow.core.test.generators.suspendFunThatReturnsEitherAnyOrAnyOrThrows
import arrow.core.test.generators.suspendFunThatThrows
import arrow.core.test.generators.suspendFunThatThrowsFatalThrowable
import arrow.core.test.generators.throwable
import arrow.core.test.laws.BicrosswalkLaws
import arrow.core.test.laws.BifunctorLaws
import arrow.core.test.laws.BitraverseLaws
import arrow.core.test.laws.EqK2Laws
import arrow.core.test.laws.FxLaws
import arrow.core.test.laws.HashLaws
import arrow.core.test.laws.MonadErrorLaws
import arrow.core.test.laws.MonoidLaws
import arrow.core.test.laws.OrderLaws
import arrow.core.test.laws.SemigroupKLaws
import arrow.core.test.laws.ShowLaws
import arrow.core.test.laws.TraverseLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import kotlinx.coroutines.runBlocking

class EitherTest : UnitSpec() {

  val EQ: Eq<Kind<EitherPartialOf<ForId>, Int>> = Eq.any()
  val throwableEQ: Eq<Throwable> = Eq.any()
  val GEN = Gen.either(Gen.string(), Gen.int())

  init {
    testLaws(
      EqK2Laws.laws(Either.eqK2(), Either.genK2()),
      BifunctorLaws.laws(Either.bifunctor(), Either.genK2(), Either.eqK2()),
      MonoidLaws.laws(Either.monoid(MOL = String.monoid(), MOR = Int.monoid()), GEN, Either.eq(String.eq(), Int.eq())),
      ShowLaws.laws(Either.show(String.show(), Int.show()), Either.eq(String.eq(), Int.eq()), GEN),
      MonadErrorLaws.laws(
        Either.monadError(),
        Either.functor(),
        Either.applicative(),
        Either.monad(),
        Either.genK(Gen.throwable()),
        Either.eqK(throwableEQ)
      ),
      TraverseLaws.laws(Either.traverse(), Either.applicative(), Either.genK(Gen.int()), Either.eqK(Int.eq())),
      BitraverseLaws.laws(Either.bitraverse(), Either.genK2(), Either.eqK2()),
      SemigroupKLaws.laws(Either.semigroupK(), Either.genK(Gen.id(Gen.int())), Either.eqK(Id.eq(Int.eq()))),
      HashLaws.laws(Either.hash(String.hash(), Int.hash()), GEN, Either.eq(String.eq(), Int.eq())),
      OrderLaws.laws(Either.order(String.order(), Int.order()), GEN),
      BicrosswalkLaws.laws(Either.bicrosswalk(), Either.genK2(), Either.eqK2()),
      FxLaws.laws<EitherPartialOf<String>, Int>(Gen.int().map(::Right), GEN.map { it }, Either.eqK(String.eq()).liftEq(Int.eq()), either::eager, either::invoke)
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
        Right(String.monoid().run { empty() }) == Either.monoid(String.monoid(), String.monoid()).run { empty() }
      }
    }

    "combine two rights should return a right of the combine of the inners" {
      forAll { a: String, b: String ->
        String.monoid().run { Either.right(a.combine(b)) } == Either.right(a).combine(String.monoid(), String.monoid(), Either.right(b))
      }
    }

    "combine two lefts should return a left of the combine of the inners" {
      forAll { a: String, b: String ->
        String.monoid().run { Either.left(a.combine(b)) } == Either.left(a).combine(String.monoid(), String.monoid(), Either.left(b))
      }
    }

    "combine a right and a left should return left" {
      forAll { a: String, b: String ->
        Either.left(a) == Either.left(a).combine(String.monoid(), String.monoid(), Either.right(b)) &&
          Either.left(a) == Either.right(b).combine(String.monoid(), String.monoid(), Either.left(a))
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

    "toOption should convert" {
      forAll { a: Int ->
        Right(a).toOption() == Some(a) &&
          Left(a).toOption() == None
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
        Right(a).contains(a) &&
          !Right(a).contains(b) &&
          !Left(a).contains(a)
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
      forAll { a: Int, b: Int ->
        Left(a).handleErrorWith { Right(b) } == Right(b) &&
          Right(a).handleErrorWith { Right(b) } == Right(a)
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
      ) { f: suspend () -> Either<Any, Any>,
        returnObject: Any ->

        runBlocking {
          val result =
            Either.resolve(
              f = f,
              success = { a -> handleWithPureFunction(a, returnObject) },
              error = { e -> handleWithPureFunction(e, returnObject) },
              throwable = { t -> handleWithPureFunction(t, returnObject) },
              unrecoverableState = ::handleWithPureFunction
            )
          result == returnObject
        }
      }
    }

    "resolve should throw a Throwable when a fatal Throwable is thrown" {
      forAll(
        Gen.suspendFunThatThrowsFatalThrowable(),
        Gen.any()
      ) { f: suspend () -> Either<Any, Any>,
        returnObject: Any ->

        runBlocking {
          shouldThrow<Throwable> {
            Either.resolve(
              f = f,
              success = { a -> handleWithPureFunction(a, returnObject) },
              error = { e -> handleWithPureFunction(e, returnObject) },
              throwable = { t -> handleWithPureFunction(t, returnObject) },
              unrecoverableState = ::handleWithPureFunction
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
      ) { f: suspend () -> Either<Any, Any>,
        returnObject: Any ->

        runBlocking {
          val result =
            Either.resolve(
              f = f,
              success = ::throwException,
              error = { e -> handleWithPureFunction(e, returnObject) },
              throwable = { t -> handleWithPureFunction(t, returnObject) },
              unrecoverableState = ::handleWithPureFunction
            )
          result == returnObject
        }
      }
    }

    "resolve should yield a result when an exception is thrown in the error supplied function" {
      forAll(
        Gen.suspendFunThatReturnsAnyLeft(),
        Gen.any()
      ) { f: suspend () -> Either<Any, Any>,
        returnObject: Any ->

        runBlocking {
          val result =
            Either.resolve(
              f = f,
              success = { a -> handleWithPureFunction(a, returnObject) },
              error = ::throwException,
              throwable = { t -> handleWithPureFunction(t, returnObject) },
              unrecoverableState = ::handleWithPureFunction
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
              f = f,
              success = ::throwException,
              error = ::throwException,
              throwable = ::throwException,
              unrecoverableState = ::handleWithPureFunction
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
