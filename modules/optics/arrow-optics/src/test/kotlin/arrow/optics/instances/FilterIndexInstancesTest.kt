package arrow.optics.instances

import arrow.data.ListKW
import arrow.data.MapKW
import arrow.data.NonEmptyList
import arrow.data.SequenceKW
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.filterIndex
import arrow.test.UnitSpec
import arrow.test.generators.genChars
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genIntSmall
import arrow.test.generators.genListKW
import arrow.test.generators.genMapKW
import arrow.test.generators.genNonEmptyList
import arrow.test.generators.genSequenceKW
import arrow.test.laws.TraversalLaws
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.properties.Gen
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class FilterIndexInstanceTest : UnitSpec() {

    init {

        "instances can be resolved implicitly" {
            filterIndex<ListKW<String>, Int, String>() shouldNotBe null
            filterIndex<NonEmptyList<String>, Int, String>() shouldNotBe null
            filterIndex<SequenceKW<Char>, Int, Char>() shouldNotBe null
            filterIndex<MapKW<Char, Int>, String, Int>() shouldNotBe null
            filterIndex<String, Int, Char>() shouldNotBe null
        }

        testLaws(TraversalLaws.laws(
                traversal = FilterIndex.filterIndex<ListKW<String>, Int, String> { true },
                aGen = genListKW(Gen.string()),
                bGen = Gen.string(),
                funcGen = genFunctionAToB(Gen.string())
        ))

        testLaws(TraversalLaws.laws(
                traversal = FilterIndex.filterIndex<NonEmptyList<String>, Int, String> { true },
                aGen = genNonEmptyList(Gen.string()),
                bGen = Gen.string(),
                funcGen = genFunctionAToB(Gen.string())
        ))

        testLaws(TraversalLaws.laws(
                traversal =  FilterIndex.filterIndex<SequenceKW<Char>, Int, Char> { true },
                aGen = genSequenceKW(genChars()),
                bGen = genChars(),
                funcGen = genFunctionAToB(genChars())
        ))

        testLaws(TraversalLaws.laws(
                traversal = FilterIndex.filterIndex<MapKW<Char, Int>, Char, Int> { true },
                aGen = genMapKW(genChars(), genIntSmall()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int())
        ))

        testLaws(TraversalLaws.laws(
                traversal = FilterIndex.filterIndex<String, Int, Char> { true },
                aGen = Gen.string(),
                bGen = genChars(),
                funcGen = genFunctionAToB(genChars())
        ))

    }

}