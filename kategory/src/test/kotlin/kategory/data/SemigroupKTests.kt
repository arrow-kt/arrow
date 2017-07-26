package kategory

import io.kotlintest.KTestJUnitRunner
import kategory.laws.SemigroupKLaws
import kategory.typeclasses.OptionSemigroupK
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class SemigroupKTests : UnitSpec() {

    init {
        testLaws(SemigroupKLaws.laws(OptionSemigroupK(), object : Eq<HK<Option.F, Int>> {
            override fun eqv(a: HK<Option.F, Int>, b: HK<Option.F, Int>): Boolean {
                return a.ev() == b.ev()
            }
        }))
    }
}
