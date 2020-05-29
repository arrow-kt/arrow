package arrow.core

import arrow.Kind
import arrow.core.extensions.combine
import arrow.core.extensions.either.applicative.applicative
import arrow.core.extensions.either.applicativeError.handleErrorWith
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
import arrow.core.extensions.either.semigroupK.semigroupK
import arrow.core.extensions.either.show.show
import arrow.core.extensions.either.traverse.traverse
import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.extensions.id.eq.eq
import arrow.core.extensions.monoid
import arrow.core.extensions.show
import arrow.core.test.UnitSpec
import arrow.core.test.generators.either
import arrow.core.test.generators.genK
import arrow.core.test.generators.genK2
import arrow.core.test.generators.id
import arrow.core.test.generators.intSmall
import arrow.core.test.generators.throwable
import arrow.core.test.laws.BicrosswalkLaws
import arrow.core.test.laws.BifunctorLaws
import arrow.core.test.laws.BitraverseLaws
import arrow.core.test.laws.EqK2Laws
import arrow.core.test.laws.HashLaws
import arrow.core.test.laws.MonadErrorLaws
import arrow.core.test.laws.MonoidLaws
import arrow.core.test.laws.SemigroupKLaws
import arrow.core.test.laws.ShowLaws
import arrow.core.test.laws.TraverseLaws
import arrow.typeclasses.Eq
import io.kotlintest.fail
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class EitherTest : UnitSpec() {

  val EQ: Eq<Kind<EitherPartialOf<ForId>, Int>> = Eq.any()

  val throwableEQ: Eq<Throwable> = Eq.any()

  init {
    testLaws(
      EqK2Laws.laws(Either.eqK2(), Either.genK2()),
      BifunctorLaws.laws(Either.bifunctor(), Either.genK2(), Either.eqK2()),
      MonoidLaws.laws(Either.monoid(MOL = String.monoid(), MOR = Int.monoid()), Gen.either(Gen.string(), Gen.int()), Either.eq(String.eq(), Int.eq())),
      ShowLaws.laws(Either.show(String.show(), Int.show()), Either.eq(String.eq(), Int.eq()), Gen.either(Gen.string(), Gen.int())),
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
      HashLaws.laws(Either.hash(String.hash(), Int.hash()), Gen.either(Gen.string(), Gen.int()), Either.eq(String.eq(), Int.eq())),
      BicrosswalkLaws.laws(Either.bicrosswalk(), Either.genK2(), Either.eqK2())
    )

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

    "non-suspended Either.fx can bind immediate values" {
      forAll(Gen.either(Gen.string(), Gen.int())) { either ->
        Either.fx2<String, Int> {
          val res = !either
          res
        } == either
      }
    }

    "non-suspended Either.fx can safely handle immediate exceptions" {
      forAll(Gen.int(), Gen.throwable()) { i: Int, exception ->
        shouldThrow<Throwable> {
          Either.fx2<String, Int> {
            val res = !Either.Right(i)
            throw exception
            res
          }

          fail("It should never reach here. Either.fx should've thrown $exception")
        } == exception
      }
    }

    "suspended Either.fx can bind immediate values" {
      Gen.either(Gen.string(), Gen.int())
        .random()
        .take(1001)
        .forEach { either ->
          Either.fx<String, Int> {
            val res = !either
            res
          } shouldBe either
        }
    }

    "suspended Either.fx can bind suspended values" {
      Gen.either(Gen.string(), Gen.int())
        .random()
        .take(10)
        .forEach { either ->
          Either.fx<String, Int> {
            val res = !(suspend {
              sleep(100)
              either
            }).invoke()

            res
          } shouldBe either
        }
    }

    "suspended Either.fx can safely handle immediate exceptions" {
      Gen.bind(Gen.int(), Gen.throwable(), ::Pair)
        .random()
        .take(1001)
        .forEach { (i, exception) ->
          shouldThrow<Throwable> {
            Either.fx<String, Int> {
              val res = !Either.Right(i)
              throw exception
              res
            }
            fail("It should never reach here. Either.fx should've thrown $exception")
          } shouldBe exception
        }
    }

    "suspended Either.fx can bind suspended exceptions" {
      Gen.bind(Gen.int(), Gen.throwable(), ::Pair)
        .random()
        .take(10)
        .forEach { (i, exception) ->
          shouldThrow<Throwable> {
            Either.fx<String, Int> {
              val res = !Either.Right(i)
              sleep(100)
              throw exception
              res
            }
            fail("It should never reach here. Either.fx should've thrown $exception")
          } shouldBe exception
        }
    }
  }
}

internal val scheduler: ScheduledExecutorService by lazy {
  Executors.newScheduledThreadPool(2) { r ->
    Thread(r).apply {
      name = "arrow-effect-scheduler-$id"
      isDaemon = true
    }
  }
}

suspend fun sleep(duration: Long): Unit =
  if (duration <= 0) Unit
  else suspendCoroutine { cont ->
    scheduler.schedule(
      { cont.resume(Unit) },
      duration,
      TimeUnit.MILLISECONDS
    )
  }
