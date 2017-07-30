package kategory

import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class SemigroupKTests : UnitSpec() {

    init {
        val monoidK = OptionT.monoidK(Id)

        testLaws(SemigroupKLaws.laws(monoidK, OptionT.applicative(Id), object : Eq<HK<OptionTF<Id.F>, Int>> {
            override fun eqv(a: HK<OptionTF<Id.F>, Int>, b: HK<OptionTF<Id.F>, Int>): Boolean {
                return a.ev().value == b.ev().value
            }
        }))
    }
}
