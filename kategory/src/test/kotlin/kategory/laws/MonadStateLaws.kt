package kategory

import io.kotlintest.properties.forAll
import kategory.typeclasses.MonadState
import kategory.typeclasses.monadState

object MonadStateLaws {

    inline fun <reified F> laws(M: MonadState<F, Int> = monadState<F, Int>(), EQ: Eq<HK<F, Int>>): List<Law> =
            MonadLaws.laws(M, EQ) + listOf(
                    Law("Monad State Laws: idempotence", { monadStateGetIdempotent(M, Eq.any()) }),
                    Law("Monad State Laws: set twice", { monadStateSetTwice(M, Eq.any()) }),
                    Law("Monad State Laws: set get", { monadStateSetTwice(M, Eq.any()) }),
                    Law("Monad State Laws: get set", { monadStateSetTwice(M, Eq.any()) })
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
            M.flatMap(M.set(s), { M.get() }).equalUnderTheLaw(M.flatMap(M.set(s), { M.pure(s)}), EQ)
        })
    }

    inline fun <reified F> monadStateGetSet(M: MonadState<F, Int> = monadState<F, Int>(), EQ: Eq<HK<F, Any>>) {
        forAll(genIntSmall(), { s: Int ->
            M.flatMap(M.get(), { M.set(it) }).equalUnderTheLaw(M.pure(s), EQ)
        })
    }
}
