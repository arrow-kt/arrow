package arrow.test.laws

import arrow.Kind
import arrow.core.Either
import arrow.test.generators.genApplicative
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genThrowable
import arrow.typeclasses.Eq
import arrow.typeclasses.MonadError
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object MonadErrorLaws {

    inline fun <F> laws(M: MonadError<F, Throwable>, EQERR: Eq<Kind<F, Int>>, EQ_EITHER: Eq<Kind<F, Either<Throwable, Int>>>, EQ: Eq<Kind<F, Int>> = EQERR): List<Law> =
            MonadLaws.laws(M, EQ) + ApplicativeErrorLaws.laws(M, EQERR, EQ_EITHER, EQ) + listOf(
                    Law("Monad Error Laws: left zero", { M.monadErrorLeftZero(EQERR) }),
                    Law("Monad Error Laws: ensure consistency", { M.monadErrorEnsureConsistency(EQERR) })
            )

    fun <F> MonadError<F, Throwable>.monadErrorLeftZero(EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genFunctionAToB<Int, Kind<F, Int>>(genApplicative(Gen.int(), this)), genThrowable(), { f: (Int) -> Kind<F, Int>, e: Throwable ->
                flatMap(raiseError<Int>(e), f).equalUnderTheLaw(raiseError<Int>(e), EQ)
            })

    fun <F> MonadError<F, Throwable>.monadErrorEnsureConsistency(EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genApplicative(Gen.int(), this), genThrowable(), genFunctionAToB<Int, Boolean>(Gen.bool()), { fa: Kind<F, Int>, e: Throwable, p: (Int) -> Boolean ->
                ensure(fa, { e }, p).equalUnderTheLaw(flatMap(fa, { a -> if (p(a)) pure(a) else raiseError(e) }), EQ)
            })
}
