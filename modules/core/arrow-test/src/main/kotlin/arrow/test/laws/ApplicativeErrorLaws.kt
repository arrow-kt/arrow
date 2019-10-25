package arrow.test.laws

import arrow.Kind
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.identity
import arrow.fx.IO
import arrow.test.generators.applicativeError
import arrow.test.generators.either
import arrow.test.generators.functionAToB
import arrow.test.generators.throwable
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object ApplicativeErrorLaws {

  fun <F> laws(AE: ApplicativeError<F, Throwable>, EQERR: Eq<Kind<F, Int>>, EQ_EITHER: Eq<Kind<F, Either<Throwable, Int>>>, EQ: Eq<Kind<F, Int>> = EQERR): List<Law> =
    ApplicativeLaws.laws(AE, EQ) + listOf(
      Law("Applicative Error Laws: handle") { AE.applicativeErrorHandle(EQERR) },
      Law("Applicative Error Laws: handle with for error") { AE.applicativeErrorHandleWith(EQERR) },
      Law("Applicative Error Laws: handle with for success") { AE.applicativeErrorHandleWithPure(EQERR) },
      Law("Applicative Error Laws: redeem is derived from map and handleError") { AE.redeemIsDerivedFromMapHandleError(EQERR) },
      Law("Applicative Error Laws: attempt for error") { AE.applicativeErrorAttemptError(EQ_EITHER) },
      Law("Applicative Error Laws: attempt for success") { AE.applicativeErrorAttemptSuccess(EQ_EITHER) },
      Law("Applicative Error Laws: attempt lift from Either consistent with pure") { AE.applicativeErrorAttemptFromEitherConsistentWithPure(EQ_EITHER) },
      Law("Applicative Error Laws: catch captures errors") { AE.applicativeErrorCatch(EQERR) },
      Law("Applicative Error Laws: effectCatch captures errors") { AE.applicativeErrorEffectCatch(EQERR) }
    )

  fun <F> ApplicativeError<F, Throwable>.applicativeErrorHandle(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.functionAToB<Throwable, Int>(Gen.int()), Gen.throwable()) { f: (Throwable) -> Int, e: Throwable ->
      raiseError<Int>(e).handleError(f).equalUnderTheLaw(just(f(e)), EQ)
    }

  fun <F> ApplicativeError<F, Throwable>.applicativeErrorHandleWith(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.functionAToB<Throwable, Kind<F, Int>>(Gen.int().applicativeError(this)), Gen.throwable()) { f: (Throwable) -> Kind<F, Int>, e: Throwable ->
      raiseError<Int>(e).handleErrorWith(f).equalUnderTheLaw(f(e), EQ)
    }

  fun <F> ApplicativeError<F, Throwable>.applicativeErrorHandleWithPure(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.functionAToB<Throwable, Kind<F, Int>>(Gen.int().applicativeError(this)), Gen.int()) { f: (Throwable) -> Kind<F, Int>, a: Int ->
      just(a).handleErrorWith(f).equalUnderTheLaw(just(a), EQ)
    }

  fun <F> ApplicativeError<F, Throwable>.redeemIsDerivedFromMapHandleError(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.int().applicativeError(this), Gen.functionAToB<Throwable, Int>(Gen.int()), Gen.functionAToB<Int, Int>(Gen.int())) { fa, fe, fb ->
      fa.redeem(fe, fb).equalUnderTheLaw(fa.map(fb).handleError(fe), EQ)
    }

  fun <F> ApplicativeError<F, Throwable>.applicativeErrorAttemptError(EQ: Eq<Kind<F, Either<Throwable, Int>>>): Unit =
    forAll(Gen.throwable()) { e: Throwable ->
      raiseError<Int>(e).attempt().equalUnderTheLaw(just(Left(e)), EQ)
    }

  fun <F> ApplicativeError<F, Throwable>.applicativeErrorAttemptSuccess(EQ: Eq<Kind<F, Either<Throwable, Int>>>): Unit =
    forAll(Gen.int()) { a: Int ->
      just(a).attempt().equalUnderTheLaw(just(Right(a)), EQ)
    }

  fun <F> ApplicativeError<F, Throwable>.applicativeErrorAttemptFromEitherConsistentWithPure(EQ: Eq<Kind<F, Either<Throwable, Int>>>): Unit =
    forAll(Gen.either(Gen.throwable(), Gen.int())) { either: Either<Throwable, Int> ->
      either.fromEither { it }.attempt().equalUnderTheLaw(just(either), EQ)
    }

  fun <F> ApplicativeError<F, Throwable>.applicativeErrorCatch(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.either(Gen.throwable(), Gen.int())) { either: Either<Throwable, Int> ->
      catch { either.fold({ throw it }, ::identity) }.equalUnderTheLaw(either.fold({ raiseError<Int>(it) }, { just(it) }), EQ)
    }

  fun <F> ApplicativeError<F, Throwable>.applicativeErrorEffectCatch(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.either(Gen.throwable(), Gen.int())) { either: Either<Throwable, Int> ->
      IO.effect {
        effectCatch { either.fold({ throw it }, ::identity) }
      }.unsafeRunSync().equalUnderTheLaw(either.fold({ raiseError<Int>(it) }, { just(it) }), EQ)
    }
}
