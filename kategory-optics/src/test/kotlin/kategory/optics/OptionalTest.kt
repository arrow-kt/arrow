package kategory.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import kategory.Eq
import kategory.OptionalLaws
import kategory.UnitSpec
import kategory.genEither
import kategory.genFunctionAToB
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class OptionalTest : UnitSpec() {

    init {

        testLaws(
                OptionalLaws.laws(
                        optional = optionalHead,
                        aGen = Gen.list(Gen.int()),
                        bGen = Gen.int(),
                        funcGen = genFunctionAToB(Gen.int()),
                        EQA = Eq.any(),
                        EQB = Eq.any()
                ) + OptionalLaws.laws(
                        optional = Optional.id(),
                        aGen = Gen.int(),
                        bGen = Gen.int(),
                        funcGen = genFunctionAToB(Gen.int()),
                        EQA = Eq.any(),
                        EQB = Eq.any()
                )
        )

    }

}