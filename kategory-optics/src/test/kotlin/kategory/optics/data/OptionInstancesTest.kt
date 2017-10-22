package kategory.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import kategory.Eq
import kategory.IntMonoid
import kategory.IsoLaws
import kategory.OptionMonoidInstanceImplicits
import kategory.PrismLaws
import kategory.UnitSpec
import kategory.genFunctionAToB
import kategory.genNullable
import kategory.genOption
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class OptionInstancesTest : UnitSpec() {

    init {

        testLaws(IsoLaws.laws(
                iso = nullableToOption<Int>(),
                aGen = genNullable(Gen.int()),
                bGen = genOption(Gen.int()),
                EQA = Eq.any(),
                EQB = Eq.any(),
                funcGen = genFunctionAToB(genOption(Gen.int())),
                bMonoid = OptionMonoidInstanceImplicits.instance(IntMonoid)
        ))

        testLaws(PrismLaws.laws(
                prism = somePrism(),
                aGen = genOption(Gen.int()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any(),
                EQB = Eq.any(),
                EQOptionB = Eq.any()
        ))

        testLaws(PrismLaws.laws(
                prism = nonePrism(),
                aGen = genOption(Gen.int()),
                bGen = Gen.create { Unit },
                funcGen = genFunctionAToB(Gen.create { Unit }),
                EQA = Eq.any(),
                EQB = Eq.any(),
                EQOptionB = Eq.any()
        ))

    }
}