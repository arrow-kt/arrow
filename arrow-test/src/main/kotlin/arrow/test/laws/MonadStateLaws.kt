package arrow.test.laws

import arrow.typeclasses.Eq
import arrow.HK
import arrow.mtl.MonadState
import arrow.mtl.monadState
import arrow.test.generators.genIntSmall
import io.kotlintest.properties.forAll

object MonadStateLaws {

    inline fun <reified F> laws(M: MonadState<F, Int> = monadState<F, Int>(), EQ: Eq<HK<F, Int>>, EQUnit: Eq<HK<F, Unit>>): List<Law> =
            MonadLaws.laws(M, EQ) + listOf(
                    Law("Monad State Laws: idempotence", { monadStateGetIdempotent(M, EQ) }),
                    Law("Monad State Laws: set twice eq to set once the last element", { monadStateSetTwice(M, EQUnit) }),
                    Law("Monad State Laws: set get", { monadStateSetGet(M, EQ) }),
                    Law("Monad State Laws: get set", { monadStateGetSet(M, EQUnit) })
            )

    inline fun <reified F> monadStateGetIdempotent(M: MonadState<F, Int> = monadState<F, Int>(), EQ: Eq<HK<F, Int>>) {
        M.flatMap(M.get(), { M.get() }).equalUnderTheLaw(M.get(), EQ)
    }

    inline fun <reified F> monadStateSetTwice(M: MonadState<F, Int> = monadState<F, Int>(), EQ: Eq<HK<F, Unit>>) {
        forAll(genIntSmall(), genIntSmall(), { s: Int, t: Int ->
            M.flatMap(M.set(s), { M.set(t) }).equalUnderTheLaw(M.set(t), EQ)
        })
    }

    inline fun <reified F> monadStateSetGet(M: MonadState<F, Int> = monadState<F, Int>(), EQ: Eq<HK<F, Int>>) {
        forAll(genIntSmall(), { s: Int ->
             M.flatMap(M.set(s), { M.get() }).equalUnderTheLaw(M.flatMap(M.set(s), { M.pure(s) }), EQ)
        })
    }

    inline fun <reified F> monadStateGetSet(M: MonadState<F, Int> = monadState<F, Int>(), EQ: Eq<HK<F, Unit>>) {
        M.flatMap(M.get(), { M.set(it) }).equalUnderTheLaw(M.pure(Unit), EQ)
    }
}
