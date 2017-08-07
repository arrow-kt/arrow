package kategory

import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class StateTTests : UnitSpec() {

    init {

        val instances = StateT.monadState<Try.F, Int>(Try)

        testLaws(MonadStateLaws.laws(
                instances,
                object : Eq<StateTKind<Try.F, Int, Int>> {
                    override fun eqv(a: StateTKind<Try.F, Int, Int>, b: StateTKind<Try.F, Int, Int>): Boolean =
                            a.runM(1) == b.runM(1)

                },
                object : Eq<StateTKind<Try.F, Int, Unit>> {
                    override fun eqv(a: StateTKind<Try.F, Int, Unit>, b: StateTKind<Try.F, Int, Unit>): Boolean =
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
