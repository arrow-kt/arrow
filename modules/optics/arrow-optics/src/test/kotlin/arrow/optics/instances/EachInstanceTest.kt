package arrow.optics.instances

import arrow.core.Either
import arrow.core.EitherPartialOf
import arrow.core.Option
import arrow.data.ListK
import arrow.data.MapK
import arrow.data.Try
import arrow.optics.typeclasses.each
import arrow.test.UnitSpec
import arrow.test.generators.genEither
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genListK
import arrow.test.generators.genMapK
import arrow.test.generators.genOption
import arrow.test.generators.genTry
import arrow.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.properties.Gen
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class EachInstanceTest : UnitSpec() {

    init {
        "instances can be resolved implicitly" {
            each<EitherPartialOf<String>, String>() shouldNotBe null
            each<ListK<String>, String>() shouldNotBe null
            each<List<String>, String>() shouldNotBe null
            each<MapK<Int, String>, String>() shouldNotBe null
            each<Map<Int, String>, String>() shouldNotBe null
            each<Option<String>, String>() shouldNotBe null
            each<Try<String>, String>() shouldNotBe null
        }

        testLaws(TraversalLaws.laws(
                traversal = each<Either<String, Int>, Int>().each(),
                aGen = genEither(Gen.string(), Gen.int()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int())
        ))

        testLaws(TraversalLaws.laws(
                traversal = each<ListK<String>, String>().each(),
                aGen = genListK(Gen.string()),
                bGen = Gen.string(),
                funcGen = genFunctionAToB(Gen.string())
        ))

        testLaws(TraversalLaws.laws(
                traversal = each<List<String>, String>().each(),
                aGen = Gen.list(Gen.string()),
                bGen = Gen.string(),
                funcGen = genFunctionAToB(Gen.string()),
                EQA = Eq.any()
        ))

        testLaws(TraversalLaws.laws(
                traversal = each<MapK<Int, String>, String>().each(),
                aGen = genMapK(Gen.int(), Gen.string()),
                bGen = Gen.string(),
                funcGen = genFunctionAToB(Gen.string())
        ))

        testLaws(TraversalLaws.laws(
                traversal = each<Map<Int, String>, String>().each(),
                aGen = Gen.map(Gen.int(), Gen.string()),
                bGen = Gen.string(),
                funcGen = genFunctionAToB(Gen.string()),
                EQA = Eq.any()
        ))

        testLaws(TraversalLaws.laws(
                traversal = each<Option<String>, String>().each(),
                aGen = genOption(Gen.string()),
                bGen = Gen.string(),
                funcGen = genFunctionAToB(Gen.string())
        ))

        testLaws(TraversalLaws.laws(
                traversal = each<Try<String>, String>().each(),
                aGen = genTry(Gen.string()),
                bGen = Gen.string(),
                funcGen = genFunctionAToB(Gen.string())
        ))

    }

}