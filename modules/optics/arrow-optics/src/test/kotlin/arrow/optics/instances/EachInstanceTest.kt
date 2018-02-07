package arrow.optics.instances

import arrow.core.Either
import arrow.core.EitherKindPartial
import arrow.core.Option
import arrow.data.ListKW
import arrow.data.MapKW
import arrow.optics.typeclasses.each
import arrow.test.UnitSpec
import arrow.test.generators.genEither
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genListKW
import arrow.test.generators.genMapKW
import arrow.test.generators.genOption
import arrow.test.laws.TraversalLaws
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.properties.Gen
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class EachInstanceTest : UnitSpec() {

    init {
        "instances can be resolved implicitly" {
            each<EitherKindPartial<String>, String>() shouldNotBe null
            each<ListKW<String>, String>() shouldNotBe null
            each<MapKW<Int, String>, String>() shouldNotBe null
            each<Option<String>, String>() shouldNotBe null
        }

        testLaws(TraversalLaws.laws(
                traversal = each<Either<String, Int>, Int>().each(),
                aGen = genEither(Gen.string(), Gen.int()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int())
        ))

        testLaws(TraversalLaws.laws(
                traversal = each<ListKW<String>, String>().each(),
                aGen = genListKW(Gen.string()),
                bGen = Gen.string(),
                funcGen = genFunctionAToB(Gen.string())
        ))

        testLaws(TraversalLaws.laws(
                traversal = each<MapKW<Int, String>, String>().each(),
                aGen = genMapKW(Gen.int(), Gen.string()),
                bGen = Gen.string(),
                funcGen = genFunctionAToB(Gen.string())
        ))

        testLaws(TraversalLaws.laws(
                traversal = each<Option<String>, String>().each(),
                aGen = genOption(Gen.string()),
                bGen = Gen.string(),
                funcGen = genFunctionAToB(Gen.string())
        ))

    }

}