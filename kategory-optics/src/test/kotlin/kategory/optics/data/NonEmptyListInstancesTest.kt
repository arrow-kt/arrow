package kategory.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import kategory.Eq
import kategory.LensLaws
import kategory.Option
import kategory.UnitSpec
import kategory.applicative
import kategory.genFunctionAToB
import kategory.genNonEmptyList
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class NonEmptyListInstancesTest : UnitSpec() {

    init {

        testLaws(LensLaws.laws(
                lens = nelHead(),
                aGen = genNonEmptyList(Gen.string()),
                bGen = Gen.string(),
                funcGen = genFunctionAToB(Gen.string()),
                EQA = Eq.any(),
                EQB = Eq.any(),
                FA = Option.applicative()
        ))

    }

}