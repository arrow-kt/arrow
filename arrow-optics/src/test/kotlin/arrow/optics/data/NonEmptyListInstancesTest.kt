package arrow.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import arrow.Eq
import arrow.test.laws.IsoLaws
import arrow.test.laws.LensLaws
import arrow.Monoid
import arrow.core.Option
import arrow.test.UnitSpec
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genNonEmptyList
import arrow.test.generators.genOption
import arrow.optics.instances.nelHead
import arrow.optics.instances.optionNelToList
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
