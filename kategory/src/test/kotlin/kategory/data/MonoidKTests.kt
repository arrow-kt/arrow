package kategory

import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class MonoidKTests : UnitSpec() {

    init {
        val monoidK = OptionMonoidK()
        testLaws(MonoidKLaws.laws(monoidK, object : Eq<HK<Option.F, Int?>> {
            override fun eqv(a: HK<Option.F, Int?>, b: HK<Option.F, Int?>): Boolean {
                return a.ev() == b.ev()
            }
        }))
    }
}
