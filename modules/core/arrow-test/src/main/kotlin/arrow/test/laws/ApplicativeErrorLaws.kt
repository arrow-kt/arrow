package arrow.test.laws

import arrow.*
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.test.generators.genApplicative
import arrow.test.generators.genEither
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genThrowable
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object ApplicativeErrorLaws {

    inline fun <F> laws(AP: ApplicativeError<F, Throwable>, EQERR: Eq<Kind<F, Int>>, EQ_EITHER: Eq<Kind<F, Either<Throwable, Int>>>, EQ: Eq<Kind<F, Int>> = EQERR): List<Law> =
            ApplicativeLaws.laws(AP, EQ) + with(AP) {
                listOf(
                        Law("Applicative Error Laws: handle", { applicativeErrorHandle(EQERR) }),
                        Law("Applicative Error Laws: handle with for error", { applicativeErrorHandleWith(EQERR) }),
                        Law("Applicative Error Laws: handle with for success", { applicativeErrorHandleWithPure(EQERR) }),
                        Law("Applicative Error Laws: attempt for error", { applicativeErrorAttemptError(EQ_EITHER) }),
                        Law("Applicative Error Laws: attempt for success", { applicativeErrorAttemptSuccess(EQ_EITHER) }),
                        Law("Applicative Error Laws: attempt fromEither consistent with pure", { applicativeErrorAttemptFromEitherConsistentWithPure(EQ_EITHER) }),
                        Law("Applicative Error Laws: catch captures errors", { applicativeErrorCatch(EQERR) })
                )
            }

    fun <F> ApplicativeError<F, Throwable>.applicativeErrorHandle(EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genFunctionAToB<Throwable, Int>(Gen.int()), genThrowable(), { f: (Throwable) -> Int, e: Throwable ->
                handleError(raiseError<Int>(e), f).equalUnderTheLaw(pure(f(e)), EQ)
            })

    fun <F> ApplicativeError<F, Throwable>.applicativeErrorHandleWith(EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genFunctionAToB<Throwable, Kind<F, Int>>(genApplicative(Gen.int(), this)), genThrowable(), { f: (Throwable) -> Kind<F, Int>, e: Throwable ->
                handleErrorWith(raiseError<Int>(e), f).equalUnderTheLaw(f(e), EQ)
            })

    fun <F> ApplicativeError<F, Throwable>.applicativeErrorHandleWithPure(EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genFunctionAToB<Throwable, Kind<F, Int>>(genApplicative(Gen.int(), this)), Gen.int(), { f: (Throwable) -> Kind<F, Int>, a: Int ->
                handleErrorWith(pure(a), f).equalUnderTheLaw(pure(a), EQ)
            })

    fun <F> ApplicativeError<F, Throwable>.applicativeErrorAttemptError(EQ: Eq<Kind<F, Either<Throwable, Int>>>): Unit =
            forAll(genThrowable(), { e: Throwable ->
                attempt(raiseError<Int>(e)).equalUnderTheLaw(pure(Left(e)), EQ)
            })

    fun <F> ApplicativeError<F, Throwable>.applicativeErrorAttemptSuccess(EQ: Eq<Kind<F, Either<Throwable, Int>>>): Unit =
            forAll(Gen.int(), { a: Int ->
                attempt(pure(a)).equalUnderTheLaw(pure(Right(a)), EQ)
            })

    fun <F> ApplicativeError<F, Throwable>.applicativeErrorAttemptFromEitherConsistentWithPure(EQ: Eq<Kind<F, Either<Throwable, Int>>>): Unit =
            forAll(genEither(genThrowable(), Gen.int()), { either: Either<Throwable, Int> ->
                attempt(fromEither(either)).equalUnderTheLaw(pure(either), EQ)
            })

    fun <F> ApplicativeError<F, Throwable>.applicativeErrorCatch(EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genEither(genThrowable(), Gen.int()), { either: Either<Throwable, Int> ->
                catch({ either.fold({ throw it }, { it }) }).equalUnderTheLaw(either.fold({ raiseError<Int>(it) }, { pure(it) }), EQ)
            })
}
