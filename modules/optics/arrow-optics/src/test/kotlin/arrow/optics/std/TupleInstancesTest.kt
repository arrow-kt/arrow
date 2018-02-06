package arrow.optics

import arrow.core.Option
import arrow.core.eq
import arrow.data.ListKW
import arrow.data.eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import arrow.typeclasses.Eq
import arrow.test.laws.LensLaws
import arrow.test.laws.TraversalLaws
import arrow.instances.IntMonoid
import arrow.instances.StringMonoidInstance
import arrow.test.UnitSpec
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genTuple
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class TupleInstancesTest : UnitSpec() {

    init {

        testLaws(LensLaws.laws(
                lens = firstTuple2(),
                aGen = genTuple(Gen.int(), Gen.string()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any(),
                EQB = Eq.any(),
                MB = IntMonoid
        ))

        testLaws(LensLaws.laws(
                lens = secondTuple2(),
                aGen = genTuple(Gen.int(), Gen.string()),
                bGen = Gen.string(),
                funcGen = genFunctionAToB(Gen.string()),
                EQA = Eq.any(),
                EQB = Eq.any(),
                MB = StringMonoidInstance
        ))

        testLaws(TraversalLaws.laws(
                traversal = traversalTuple2(),
                aGen = genTuple(Gen.int(), Gen.int()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any(),
                EQOptionB = Option.eq(Eq.any()),
                EQListB = ListKW.eq(Eq.any())
        ))

        testLaws(TraversalLaws.laws(
                traversal = traversalTuple3(),
                aGen = genTuple(Gen.int(), Gen.int(), Gen.int()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any(),
                EQOptionB = Option.eq(Eq.any()),
                EQListB = ListKW.eq(Eq.any())
        ))

        testLaws(TraversalLaws.laws(
                traversal = traversalTuple4(),
                aGen = genTuple(Gen.int(), Gen.int(), Gen.int(), Gen.int()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any(),
                EQOptionB = Option.eq(Eq.any()),
                EQListB = ListKW.eq(Eq.any())
        ))

        testLaws(TraversalLaws.laws(
                traversal = traversalTuple5(),
                aGen = genTuple(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any(),
                EQOptionB = Option.eq(Eq.any()),
                EQListB = ListKW.eq(Eq.any())
        ))

        testLaws(TraversalLaws.laws(
                traversal = traversalTuple6(),
                aGen = genTuple(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any(),
                EQOptionB = Option.eq(Eq.any()),
                EQListB = ListKW.eq(Eq.any())
        ))

        testLaws(TraversalLaws.laws(
                traversal = traversalTuple7(),
                aGen = genTuple(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any(),
                EQOptionB = Option.eq(Eq.any()),
                EQListB = ListKW.eq(Eq.any())
        ))

        testLaws(TraversalLaws.laws(
                traversal = traversalTuple8(),
                aGen = genTuple(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any(),
                EQOptionB = Option.eq(Eq.any()),
                EQListB = ListKW.eq(Eq.any())
        ))

        testLaws(TraversalLaws.laws(
                traversal = traversalTuple9(),
                aGen = genTuple(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any(),
                EQOptionB = Option.eq(Eq.any()),
                EQListB = ListKW.eq(Eq.any())
        ))

        testLaws(TraversalLaws.laws(
                traversal = traversalTuple10(),
                aGen = genTuple(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any(),
                EQOptionB = Option.eq(Eq.any()),
                EQListB = ListKW.eq(Eq.any())
        ))

    }

}
