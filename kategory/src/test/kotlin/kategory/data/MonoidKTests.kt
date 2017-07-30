package kategory

import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class MonoidKTests : UnitSpec() {

    init {
        val monoidK = OptionT.monoidK(Id)

        testLaws(MonoidKLaws.laws(
                monoidK,
                OptionT.applicative(Id),
                object : Eq<HK<OptionTF<Id.F>, Id.F>> {
                    override fun eqv(a: HK<OptionTF<Id.F>, Id.F>, b: HK<OptionTF<Id.F>, Id.F>): Boolean {
                        return a.ev().value == b.ev().value
                    }
                },
                object : Eq<HK<OptionTF<Id.F>, Int>> {
                    override fun eqv(a: HK<OptionTF<Id.F>, Int>, b: HK<OptionTF<Id.F>, Int>): Boolean {
                        return a.ev().value == b.ev().value
                    }
                }))
    }
}
