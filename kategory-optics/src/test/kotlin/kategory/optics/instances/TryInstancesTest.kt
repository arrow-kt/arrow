package kategory.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import kategory.Eq
import kategory.PrismLaws
import kategory.UnitSpec
import kategory.genFunctionAToB
import kategory.genThrowable
import kategory.genTry
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class TryInstancesTest : UnitSpec() {
    init {

        testLaws(PrismLaws.laws(
                prism = trySuccess(),
                aGen = genTry(Gen.int()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any(),
                EQB = Eq.any(),
                EQOptionB = Eq.any()
        ))

        testLaws(PrismLaws.laws(
                prism = tryFailure(),
                aGen = genTry(Gen.int()),
                bGen = genThrowable(),
                funcGen = genFunctionAToB(genThrowable()),
                EQA = Eq.any(),
                EQB = Eq.any(),
                EQOptionB = Eq.any()
        ))

    }
}