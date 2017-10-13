package kategory.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import kategory.Eq
import kategory.OptionalLaws
import kategory.UnitSpec
import kategory.genFunctionAToB
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class ListInstancesTest : UnitSpec() {

    init {

        testLaws(OptionalLaws.laws(
                optional = listHead(),
                aGen = Gen.list(Gen.int()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any(),
                EQB = Eq.any(),
                EQOptionB = Eq.any()
        ))

        testLaws(OptionalLaws.laws(
                optional = listTail(),
                aGen = Gen.list(Gen.int()),
                bGen = Gen.list(Gen.int()),
                funcGen = genFunctionAToB(Gen.list(Gen.int())),
                EQA = Eq.any(),
                EQB = Eq.any(),
                EQOptionB = Eq.any()
        ))

    }

}