package arrow.test.laws

import arrow.Kind
import arrow.core.Either
import arrow.test.generators.genApplicative
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genThrowable
import arrow.typeclasses.Eq
import arrow.typeclasses.MonadError
import arrow.typeclasses.monadError
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object MonadErrorLaws {

    inline fun <reified F> laws(M: MonadError<F, Throwable> = monadError<F, Throwable>(), EQERR: Eq<Kind<F, Int>>, EQ_EITHER: Eq<Kind<F, Either<Throwable, Int>>>, EQ: Eq<Kind<F, Int>> = EQERR): List<Law> =
            MonadLaws.laws(M, EQ) + ApplicativeErrorLaws.laws(M, EQERR, EQ_EITHER, EQ) + listOf(
                    Law("Monad Error Laws: left zero", { monadErrorLeftZero(M, EQERR) }),
                    Law("Monad Error Laws: ensure consistency", { monadErrorEnsureConsistency(M, EQERR) })
            )

    inline fun <reified F> monadErrorLeftZero(M: MonadError<F, Throwable> = monadError<F, Throwable>(), EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genFunctionAToB<Int, Kind<F, Int>>(genApplicative(Gen.int(), M)), genThrowable(), { f: (Int) -> Kind<F, Int>, e: Throwable ->
                M.flatMap(M.raiseError<Int>(e), f).equalUnderTheLaw(M.raiseError<Int>(e), EQ)
            })

    inline fun <reified F> monadErrorEnsureConsistency(M: MonadError<F, Throwable> = monadError<F, Throwable>(), EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genApplicative(Gen.int(), M), genThrowable(), genFunctionAToB<Int, Boolean>(Gen.bool()), { fa: Kind<F, Int>, e: Throwable, p: (Int) -> Boolean ->
                M.ensure(fa, { e }, p).equalUnderTheLaw(M.flatMap(fa, { a -> if (p(a)) M.pure(a) else M.raiseError(e) }), EQ)
            })
}
