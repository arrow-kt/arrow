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

        testLaws(SemigroupKLaws.laws<StateTKindPartial<OptionHK, Int>>(
                StateT.semigroupK(Option.monad(), OptionSemigroupK()),
                StateT.applicative(Option.monad()),
                object : Eq<HK<StateTKindPartial<OptionHK, Int>, Int>> {
                    override fun eqv(a: HK<StateTKindPartial<OptionHK, Int>, Int>, b: HK<StateTKindPartial<OptionHK, Int>, Int>): Boolean =
                            a.runM(1) == b.runM(1)
                }))
    }
}
