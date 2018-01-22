package arrow.test.laws

import arrow.HK
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.effects.Async
import arrow.effects.async
import arrow.test.generators.genThrowable
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object AsyncLaws {
    inline fun <reified F> laws(AC: Async<F> = async(), EQ: Eq<HK<F, Int>>, EQ_EITHER: Eq<HK<F, Either<Throwable, Int>>>, EQERR: Eq<HK<F, Int>> = EQ): List<Law> =
            MonadSuspendLaws.laws(AC, EQERR, EQ_EITHER, EQ) + listOf(
                    Law("Async Laws: success equivalence", { asyncSuccess(AC, EQ) }),
                    Law("Async Laws: error equivalence", { asyncError(AC, EQERR) })
            )

    inline fun <reified F> asyncSuccess(AC: Async<F> = async(), EQ: Eq<HK<F, Int>>): Unit =
            forAll(Gen.int(), { num: Int ->
                AC.async { ff: (Either<Throwable, Int>) -> Unit -> ff(Right(num)) }.equalUnderTheLaw(AC.pure<Int>(num), EQ)
            })

    inline fun <reified F> asyncError(AC: Async<F> = async(), EQ: Eq<HK<F, Int>>): Unit =
            forAll(genThrowable(), { e: Throwable ->
                AC.async { ff: (Either<Throwable, Int>) -> Unit -> ff(Left(e)) }.equalUnderTheLaw(AC.raiseError<Int>(e), EQ)
            })
}
