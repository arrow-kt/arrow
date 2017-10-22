package kategory.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import kategory.Eq
import kategory.IsoLaws
import kategory.Monoid
import kategory.UnitSpec
import kategory.genFunctionAToB
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class StringInstancesTest : UnitSpec() {

    init {

        testLaws(IsoLaws.laws(
                iso = stringToList,
                aGen = Gen.string(),
                bGen = Gen.create { Gen.string().generate().toList() },
                funcGen = genFunctionAToB(Gen.create { Gen.string().generate().toList() }),
                EQA = Eq.any(),
                EQB = Eq.any(),
                bMonoid = object : Monoid<List<Char>> {
                    override fun combine(a: List<Char>, b: List<Char>): List<Char> = a + b
                    override fun empty(): List<Char> = emptyList()
                }
        ))

    }

}