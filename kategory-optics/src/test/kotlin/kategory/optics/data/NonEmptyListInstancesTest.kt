package kategory.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import kategory.Eq
import kategory.IsoLaws
import kategory.LensLaws
import kategory.Monoid
import kategory.Option
import kategory.UnitSpec
import kategory.applicative
import kategory.genFunctionAToB
import kategory.genNonEmptyList
import kategory.genOption
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class NonEmptyListInstancesTest : UnitSpec() {

    init {

        testLaws(
            LensLaws.laws(
                lens = nelHead(),
                aGen = genNonEmptyList(Gen.string()),
                bGen = Gen.string(),
                funcGen = genFunctionAToB(Gen.string()),
                EQA = Eq.any(),
                EQB = Eq.any(),
                FA = Option.applicative()),

            IsoLaws.laws(
                iso = optionNelToList(),
                aGen = genOption(genNonEmptyList(Gen.int())),
                bGen = Gen.list(Gen.int()),
                funcGen = genFunctionAToB(Gen.list(Gen.int())),
                EQA = Eq.any(),
                EQB = Eq.any(),
                bMonoid = object : Monoid<List<Int>> {
                    override fun combine(a: List<Int>, b: List<Int>): List<Int> = a + b
                    override fun empty(): List<Int> = emptyList()
                })
        )

    }

}
