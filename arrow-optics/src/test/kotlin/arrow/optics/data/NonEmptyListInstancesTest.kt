package arrow.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import arrow.Eq
import arrow.IsoLaws
import arrow.LensLaws
import arrow.Monoid
import arrow.Option
import arrow.UnitSpec
import arrow.applicative
import arrow.genFunctionAToB
import arrow.genNonEmptyList
import arrow.genOption
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
