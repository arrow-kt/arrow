package arrow

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object ApplicativeErrorLaws {

    inline fun <reified F> laws(AP: ApplicativeError<F, Throwable> = applicativeError<F, Throwable>(), EQERR: Eq<HK<F, Int>>, EQ_EITHER: Eq<HK<F, Either<Throwable, Int>>>, EQ: Eq<HK<F, Int>> = EQERR): List<Law> =
            ApplicativeLaws.laws(AP, EQ) + listOf(
                    Law("Applicative Error Laws: handle", { applicativeErrorHandle(AP, EQERR) }),
                    Law("Applicative Error Laws: handle with for error", { applicativeErrorHandleWith(AP, EQERR) }),
                    Law("Applicative Error Laws: handle with for success", { applicativeErrorHandleWithPure(AP, EQERR) }),
                    Law("Applicative Error Laws: attempt for error", { applicativeErrorAttemptError(AP, EQ_EITHER) }),
                    Law("Applicative Error Laws: attempt for success", { applicativeErrorAttemptSuccess(AP, EQ_EITHER) }),
                    Law("Applicative Error Laws: attempt fromEither consistent with pure", { applicativeErrorAttemptFromEitherConsistentWithPure(AP, EQ_EITHER) }),
                    Law("Applicative Error Laws: catch captures errors", { applicativeErrorCatch(AP, EQERR) })
            )

    inline fun <reified F> applicativeErrorHandle(AP: ApplicativeError<F, Throwable> = applicativeError<F, Throwable>(), EQ: Eq<HK<F, Int>>): Unit =
            forAll(genFunctionAToB<Throwable, Int>(Gen.int()), genThrowable(), { f: (Throwable) -> Int, e: Throwable ->
                AP.handleError(AP.raiseError<Int>(e), f).equalUnderTheLaw(AP.pure(f(e)), EQ)
            })

    inline fun <reified F> applicativeErrorHandleWith(AP: ApplicativeError<F, Throwable> = applicativeError<F, Throwable>(), EQ: Eq<HK<F, Int>>): Unit =
            forAll(genFunctionAToB<Throwable, HK<F, Int>>(genApplicative(Gen.int(), AP)), genThrowable(), { f: (Throwable) -> HK<F, Int>, e: Throwable ->
                AP.handleErrorWith(AP.raiseError<Int>(e), f).equalUnderTheLaw(f(e), EQ)
            })

    inline fun <reified F> applicativeErrorHandleWithPure(AP: ApplicativeError<F, Throwable> = applicativeError<F, Throwable>(), EQ: Eq<HK<F, Int>>): Unit =
            forAll(genFunctionAToB<Throwable, HK<F, Int>>(genApplicative(Gen.int(), AP)), Gen.int(), { f: (Throwable) -> HK<F, Int>, a: Int ->
                AP.handleErrorWith(AP.pure(a), f).equalUnderTheLaw(AP.pure(a), EQ)
            })

    inline fun <reified F> applicativeErrorAttemptError(AP: ApplicativeError<F, Throwable> = applicativeError<F, Throwable>(), EQ: Eq<HK<F, Either<Throwable, Int>>>): Unit =
            forAll(genThrowable(), { e: Throwable ->
                AP.attempt(AP.raiseError<Int>(e)).equalUnderTheLaw(AP.pure(Left(e)), EQ)
            })

    inline fun <reified F> applicativeErrorAttemptSuccess(AP: ApplicativeError<F, Throwable> = applicativeError<F, Throwable>(), EQ: Eq<HK<F, Either<Throwable, Int>>>): Unit =
            forAll(Gen.int(), { a: Int ->
                AP.attempt(AP.pure(a)).equalUnderTheLaw(AP.pure(Right(a)), EQ)
            })

    inline fun <reified F> applicativeErrorAttemptFromEitherConsistentWithPure(AP: ApplicativeError<F, Throwable> = applicativeError<F, Throwable>(), EQ: Eq<HK<F, Either<Throwable, Int>>>): Unit =
            forAll(genEither(genThrowable(), Gen.int()), { either: Either<Throwable, Int> ->
                AP.attempt(AP.fromEither(either)).equalUnderTheLaw(AP.pure(either), EQ)
            })

    inline fun <reified F> applicativeErrorCatch(AP: ApplicativeError<F, Throwable> = applicativeError<F, Throwable>(), EQ: Eq<HK<F, Int>>): Unit =
            forAll(genEither(genThrowable(), Gen.int()), { either: Either<Throwable, Int> ->
                AP.catch({ either.fold({ throw it }, { it }) }).equalUnderTheLaw(either.fold({ AP.raiseError<Int>(it) }, { AP.pure(it) }), EQ)
            })

}