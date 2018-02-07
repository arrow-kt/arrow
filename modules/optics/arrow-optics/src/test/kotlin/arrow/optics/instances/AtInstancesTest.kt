package arrow.optics.instances

import arrow.core.Option
import arrow.data.ListK
import arrow.data.MapK
import arrow.data.SetK
import arrow.optics.AndMonoid
import arrow.optics.typeclasses.at
import arrow.test.UnitSpec
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genListK
import arrow.test.generators.genMapK
import arrow.test.generators.genOption
import arrow.test.generators.genSetK
import arrow.test.laws.LensLaws
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.properties.Gen
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class AtInstanceTest : UnitSpec() {

    init {

        "instances can be resolved implicitly" {
            at<MapK<String, Int>, String, Option<Int>>() shouldNotBe null
            at<SetK<String>, String, Boolean>() shouldNotBe null
        }

        testLaws(LensLaws.laws(
                lens = at<MapK<String, Int>, String, Option<Int>>().at(Gen.string().generate()),
                aGen = genMapK(Gen.string(), Gen.int()),
                bGen = genOption(Gen.int()),
                funcGen = genFunctionAToB(genOption(Gen.int()))
        ))

        testLaws(LensLaws.laws(
                lens = at<SetK<String>, String, Boolean>().at(Gen.string().generate()),
                aGen = genSetK(Gen.string()),
                bGen = Gen.bool(),
                funcGen = genFunctionAToB(Gen.bool()),
                MB = AndMonoid
        ))

    }
}
