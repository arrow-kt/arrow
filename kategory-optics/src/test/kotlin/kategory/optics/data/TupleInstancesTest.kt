package kategory.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.Gen
import kategory.Eq
import kategory.LensLaws
import kategory.NonEmptyList
import kategory.Option
import kategory.TraversalLaws
import kategory.TraverseLaws
import kategory.Try
import kategory.UnitSpec
import kategory.applicative
import kategory.functor
import kategory.genFunctionAToB
import kategory.genTuple
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class TupleInstancesTest : UnitSpec() {

    init {

        testLaws(
            LensLaws.laws(
                lens = firstTuple2(),
                aGen = genTuple(Gen.int(), Gen.string()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any(),
                EQB = Eq.any(),
                FA = NonEmptyList.applicative()),

            LensLaws.laws(
                lens = secondTuple2(),
                aGen = genTuple(Gen.int(), Gen.string()),
                bGen = Gen.string(),
                funcGen = genFunctionAToB(Gen.string()),
                EQA = Eq.any(),
                EQB = Eq.any(),
                FA = Try.applicative()),

            TraversalLaws.laws(
                traversal = traversalTuple2(),
                aGen = genTuple(Gen.int(), Gen.int()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any(),
                EQB = Eq.any()),

            TraversalLaws.laws(
                traversal = traversalTuple3(),
                aGen = genTuple(Gen.int(), Gen.int(), Gen.int()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any(),
                EQB = Eq.any()),

            TraversalLaws.laws(
                traversal = traversalTuple4(),
                aGen = genTuple(Gen.int(), Gen.int(), Gen.int(), Gen.int()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any(),
                EQB = Eq.any()),

            TraversalLaws.laws(
                traversal = traversalTuple5(),
                aGen = genTuple(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any(),
                EQB = Eq.any()),

            TraversalLaws.laws(
                traversal = traversalTuple6(),
                aGen = genTuple(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any(),
                EQB = Eq.any()),

            TraversalLaws.laws(
                traversal = traversalTuple7(),
                aGen = genTuple(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any(),
                EQB = Eq.any()),

            TraversalLaws.laws(
                traversal = traversalTuple8(),
                aGen = genTuple(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any(),
                EQB = Eq.any()),

            TraversalLaws.laws(
                traversal = traversalTuple9(),
                aGen = genTuple(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any(),
                EQB = Eq.any()),

            TraversalLaws.laws(
                traversal = traversalTuple10(),
                aGen = genTuple(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any(),
                EQB = Eq.any())
        )

    }

}
