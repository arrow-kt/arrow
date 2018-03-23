package arrow.optics.instances

import arrow.data.ListK
import arrow.data.MapK
import arrow.data.NonEmptyList
import arrow.data.SequenceK
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.filterIndex
import arrow.test.UnitSpec
import arrow.test.generators.genChars
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genIntSmall
import arrow.test.generators.genListK
import arrow.test.generators.genMapK
import arrow.test.generators.genNonEmptyList
import arrow.test.generators.genSequenceK
import arrow.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.properties.Gen
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class FilterIndexInstanceTest : UnitSpec() {

    init {

        "instances can be resolved implicitly" {
            filterIndex<ListK<String>, Int, String>() shouldNotBe null
            filterIndex<List<String>, Int, String>() shouldNotBe null
            filterIndex<NonEmptyList<String>, Int, String>() shouldNotBe null
            filterIndex<SequenceK<Char>, Int, Char>() shouldNotBe null
            filterIndex<MapK<Char, Int>, String, Int>() shouldNotBe null
            filterIndex<Map<Char, Int>, String, Int>() shouldNotBe null
            filterIndex<String, Int, Char>() shouldNotBe null
        }

        testLaws(TraversalLaws.laws(
                traversal = FilterIndex.filterIndex<ListK<String>, Int, String> { true },
                aGen = genListK(Gen.string()),
                bGen = Gen.string(),
                funcGen = genFunctionAToB(Gen.string())
        ))

        testLaws(TraversalLaws.laws(
                traversal = FilterIndex.filterIndex<List<String>, Int, String> { true },
                aGen = Gen.list(Gen.string()),
                bGen = Gen.string(),
                funcGen = genFunctionAToB(Gen.string()),
                EQA = Eq.any()
        ))

        testLaws(TraversalLaws.laws(
                traversal = FilterIndex.filterIndex<NonEmptyList<String>, Int, String> { true },
                aGen = genNonEmptyList(Gen.string()),
                bGen = Gen.string(),
                funcGen = genFunctionAToB(Gen.string())
        ))

        testLaws(TraversalLaws.laws(
                traversal =  FilterIndex.filterIndex<SequenceK<Char>, Int, Char> { true },
                aGen = genSequenceK(genChars()),
                bGen = genChars(),
                funcGen = genFunctionAToB(genChars())
        ))

        testLaws(TraversalLaws.laws(
                traversal = FilterIndex.filterIndex<MapK<Char, Int>, Char, Int> { true },
                aGen = genMapK(genChars(), genIntSmall()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int())
        ))

        testLaws(TraversalLaws.laws(
                traversal = FilterIndex.filterIndex<Map<Char, Int>, Char, Int> { true },
                aGen = genMapK(genChars(), genIntSmall()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any()
        ))

        testLaws(TraversalLaws.laws(
                traversal = FilterIndex.filterIndex<String, Int, Char> { true },
                aGen = Gen.string(),
                bGen = genChars(),
                funcGen = genFunctionAToB(genChars())
        ))

    }

}