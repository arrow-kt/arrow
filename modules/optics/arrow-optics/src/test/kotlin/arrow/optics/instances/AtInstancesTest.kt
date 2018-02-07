package arrow.optics.instances

import arrow.core.Option
import arrow.data.MapKW
import arrow.data.SetKW
import arrow.optics.AndMonoid
import arrow.optics.typeclasses.at
import arrow.test.UnitSpec
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genMapKW
import arrow.test.generators.genOption
import arrow.test.generators.genSetKW
import arrow.test.laws.LensLaws
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.properties.Gen
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class AtInstanceTest : UnitSpec() {

    init {

        "instances can be resolved implicitly" {
            at<MapKW<String, Int>, String, Option<Int>>() shouldNotBe null
            at<SetKW<String>, String, Boolean>() shouldNotBe null
        }

        testLaws(LensLaws.laws(
                lens = at<MapKW<String, Int>, String, Option<Int>>().at(Gen.string().generate()),
                aGen = genMapKW(Gen.string(), Gen.int()),
                bGen = genOption(Gen.int()),
                funcGen = genFunctionAToB(genOption(Gen.int()))
        ))

        testLaws(LensLaws.laws(
                lens = at<SetKW<String>, String, Boolean>().at(Gen.string().generate()),
                aGen = genSetKW(Gen.string()),
                bGen = Gen.bool(),
                funcGen = genFunctionAToB(Gen.bool()),
                MB = AndMonoid
        ))

    }
}
