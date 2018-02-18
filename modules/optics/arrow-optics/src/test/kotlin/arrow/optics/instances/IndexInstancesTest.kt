package arrow.optics.instances

import arrow.data.ListK
import arrow.data.MapK
import arrow.data.NonEmptyList
import arrow.data.SequenceK
import arrow.optics.typeclasses.index
import arrow.test.UnitSpec
import arrow.test.generators.genChars
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genListK
import arrow.test.generators.genMapK
import arrow.test.generators.genNonEmptyList
import arrow.test.generators.genSequenceK
import arrow.test.laws.OptionalLaws
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.properties.Gen
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class IndexInstanceTest : UnitSpec() {

    init {

        "instances can be resolved implicitly" {
            index<ListK<String>, Int, String>() shouldNotBe null
            index<NonEmptyList<String>, Int, String>() shouldNotBe null
            index<SequenceK<Char>, Int, Char>() shouldNotBe null
            index<MapK<Char, Int>, String, Int>() shouldNotBe null
            index<String, Int, Char>() shouldNotBe null
        }

        testLaws(OptionalLaws.laws(
                optional = index<ListK<String>, Int, String>().index(5),
                aGen = genListK(Gen.string()),
                bGen = Gen.string(),
                funcGen = genFunctionAToB(Gen.string())
        ))

        testLaws(OptionalLaws.laws(
                optional = index<NonEmptyList<String>, Int, String>().index(5),
                aGen = genNonEmptyList(Gen.string()),
                bGen = Gen.string(),
                funcGen = genFunctionAToB(Gen.string())
        ))

        testLaws(OptionalLaws.laws(
                optional = index<SequenceK<String>, Int, String>().index(5),
                aGen = genSequenceK(Gen.string()),
                bGen = Gen.string(),
                funcGen = genFunctionAToB(Gen.string())
        ))

        testLaws(OptionalLaws.laws(
                optional = index<MapK<String, Int>, String, Int>().index(Gen.string().generate()),
                aGen = genMapK(Gen.string(), Gen.int()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int())
        ))

        testLaws(OptionalLaws.laws(
                optional = index<String, Int, Char>().index(5),
                aGen = Gen.string(),
                bGen = genChars(),
                funcGen = genFunctionAToB(genChars())
        ))

    }

}