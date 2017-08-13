package kategory

import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class StateTTests : UnitSpec() {

    init {

        val instances = StateT.monadState<TryHK, Int>(Try)

        testLaws(MonadStateLaws.laws(
                instances,
                object : Eq<StateTKind<TryHK, Int, Int>> {
                    override fun eqv(a: StateTKind<TryHK, Int, Int>, b: StateTKind<TryHK, Int, Int>): Boolean =
                            a.runM(1) == b.runM(1)

                },
                object : Eq<StateTKind<TryHK, Int, Unit>> {
                    override fun eqv(a: StateTKind<TryHK, Int, Unit>, b: StateTKind<TryHK, Int, Unit>): Boolean =
                            a.runM(1) == b.runM(1)
                }))

        testLaws(SemigroupKLaws.laws<StateTF<OptionHK, Int>>(
                StateT.semigroupK(Option, OptionSemigroupK()),
                StateT.applicative(Option),
                object : Eq<HK<StateTF<OptionHK, Int>, Int>> {
                    override fun eqv(a: HK<StateTF<OptionHK, Int>, Int>, b: HK<StateTF<OptionHK, Int>, Int>): Boolean =
                            a.runM(1) == b.runM(1)
                }))
    }
}
