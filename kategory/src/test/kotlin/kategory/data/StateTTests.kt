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

        testLaws(SemigroupKLaws.laws<StateTF<Id.F, Int>>(
                StateT.semigroupK(Id, IdSemigroupK),
                StateT.applicative(Id),
                object : Eq<HK<StateTF<Id.F, Int>, Int>> {
                    override fun eqv(a: HK<StateTF<Id.F, Int>, Int>, b: HK<StateTF<Id.F, Int>, Int>): Boolean =
                            a.runM(1) == b.runM(1)
                }))
    }
}
