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
                            a.ev().run(1) == b.ev().run(1)

                },
                object : Eq<StateTKind<Try.F, Int, Unit>> {
                    override fun eqv(a: StateTKind<Try.F, Int, Unit>, b: StateTKind<Try.F, Int, Unit>): Boolean =
                            a.ev().run(1) == b.ev().run(1)
                }))

        "FAKE: set get playground" {
            val instances = StateT.monadState<Try.F, Int>(Try)
            val M = instances
            val s = 1
            val a = M.flatMap(M.set(s), { M.get() }).run(10)
            val b = M.set(s).ev().run(10)
            a.equalUnderTheLaw(b)

            // M.flatMap(M.set(s), { M.get() }).equalUnderTheLaw(M.flatMap(M.set(s), { M.pure(s) }), EQ)
        }
    }
}
