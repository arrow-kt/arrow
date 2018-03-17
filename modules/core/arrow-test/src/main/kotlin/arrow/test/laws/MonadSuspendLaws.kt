package arrow.test.laws

import arrow.Kind
import arrow.core.Either
import arrow.effects.MonadSuspend
import arrow.effects.monadSuspend
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object MonadSuspendLaws {
    inline fun <reified F> laws(SC: MonadSuspend<F, Throwable> = monadSuspend(), EQ: Eq<Kind<F, Int>>, EQ_EITHER: Eq<Kind<F, Either<Throwable, Int>>>, EQERR: Eq<Kind<F, Int>> = EQ): List<Law> =
            MonadErrorLaws.laws(SC, EQERR, EQ_EITHER, EQ) + listOf(
                    Law("Monad Suspend Laws: delay a constant is pure", { delayConstantIsPure(SC, EQ) })
            )

    inline fun <reified F> delayConstantIsPure(SC: MonadSuspend<F, Throwable> = monadSuspend(), EQ: Eq<Kind<F, Int>>): Unit =
            forAll(Gen.int(), { num: Int ->
                SC { num }.equalUnderTheLaw(SC.pure(num), EQ)
            })
}
