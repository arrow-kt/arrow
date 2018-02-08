package arrow.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import arrow.typeclasses.Eq
import arrow.test.laws.IsoLaws
import arrow.typeclasses.Monoid
import arrow.test.UnitSpec
import arrow.test.generators.genFunctionAToB
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