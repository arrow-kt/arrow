package arrow.optics.instances

import arrow.data.ListKW
import arrow.data.MapKW
import arrow.data.NonEmptyList
import arrow.data.SequenceKW
import arrow.optics.typeclasses.index
import arrow.test.UnitSpec
import arrow.test.generators.genChars
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genListKW
import arrow.test.generators.genMapKW
import arrow.test.generators.genNonEmptyList
import arrow.test.generators.genSequenceKW
import arrow.test.laws.OptionalLaws
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.properties.Gen
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class IndexInstanceTest : UnitSpec() {

    init {

        "instances can be resolved implicitly" {
            index<ListKW<String>, Int, String>() shouldNotBe null
            index<NonEmptyList<String>, Int, String>() shouldNotBe null
            index<SequenceKW<Char>, Int, Char>() shouldNotBe null
            index<MapKW<Char, Int>, String, Int>() shouldNotBe null
            index<String, Int, Char>() shouldNotBe null
        }

        testLaws(OptionalLaws.laws(
                optional = index<ListKW<String>, Int, String>().index(5),
                aGen = genListKW(Gen.string()),
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
                optional = index<SequenceKW<String>, Int, String>().index(5),
                aGen = genSequenceKW(Gen.string()),
                bGen = Gen.string(),
                funcGen = genFunctionAToB(Gen.string())
        ))

        testLaws(OptionalLaws.laws(
                optional = index<MapKW<String, Int>, String, Int>().index(Gen.string().generate()),
                aGen = genMapKW(Gen.string(), Gen.int()),
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