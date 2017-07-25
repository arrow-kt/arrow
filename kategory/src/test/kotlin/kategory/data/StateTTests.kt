package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class StateTTests : UnitSpec() {

    init {

        val instances = StateT.monadState<Try.F, Int>(Try)

        testLaws(MonadStateLaws.laws(instances, object : Eq<StateTKind<Try.F, Int, Int>> {
            override fun eqv(a: StateTKind<Try.F, Int, Int>, b: StateTKind<Try.F, Int, Int>): Boolean =
                    a.ev().run(1) == b.ev().run(1)

        }))
    }
}
