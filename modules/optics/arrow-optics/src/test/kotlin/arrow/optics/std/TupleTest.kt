package arrow.optics

import arrow.core.*
import arrow.data.ListK
import arrow.instances.monoid
import arrow.instances.listk.eq.eq
import arrow.instances.option.eq.eq
import arrow.test.UnitSpec
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genTuple
import arrow.test.laws.LensLaws
import arrow.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class TupleTest : UnitSpec() {

  init {

    testLaws(LensLaws.laws(
      lens = Tuple2.first(),
      aGen = genTuple(Gen.int(), Gen.string()),
      bGen = Gen.int(),
      funcGen = genFunctionAToB(Gen.int()),
      EQA = Eq.any(),
      EQB = Eq.any(),
      MB = Int.monoid()
    ))

    testLaws(LensLaws.laws(
      lens = Tuple2.second(),
      aGen = genTuple(Gen.int(), Gen.string()),
      bGen = Gen.string(),
      funcGen = genFunctionAToB(Gen.string()),
      EQA = Eq.any(),
      EQB = Eq.any(),
      MB = String.monoid()
    ))

    testLaws(LensLaws.laws(
      lens = Tuple3.first(),
      aGen = genTuple(Gen.int(), Gen.string(), Gen.string()),
      bGen = Gen.int(),
      funcGen = genFunctionAToB(Gen.int()),
      EQA = Eq.any(),
      EQB = Eq.any(),
      MB = Int.monoid()
    ))

    testLaws(LensLaws.laws(
      lens = Tuple3.second(),
      aGen = genTuple(Gen.int(), Gen.string(), Gen.int()),
      bGen = Gen.string(),
      funcGen = genFunctionAToB(Gen.string()),
      EQA = Eq.any(),
      EQB = Eq.any(),
      MB = String.monoid()
    ))

    testLaws(LensLaws.laws(
      lens = Tuple3.third(),
      aGen = genTuple(Gen.int(), Gen.int(), Gen.string()),
      bGen = Gen.string(),
      funcGen = genFunctionAToB(Gen.string()),
      EQA = Eq.any(),
      EQB = Eq.any(),
      MB = String.monoid()
    ))

    testLaws(TraversalLaws.laws(
      traversal = Tuple2.traversal(),
      aGen = genTuple(Gen.int(), Gen.int()),
      bGen = Gen.int(),
      funcGen = genFunctionAToB(Gen.int()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

    testLaws(TraversalLaws.laws(
      traversal = Tuple3.traversal(),
      aGen = genTuple(Gen.int(), Gen.int(), Gen.int()),
      bGen = Gen.int(),
      funcGen = genFunctionAToB(Gen.int()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

    testLaws(TraversalLaws.laws(
      traversal = Tuple4.traversal(),
      aGen = genTuple(Gen.int(), Gen.int(), Gen.int(), Gen.int()),
      bGen = Gen.int(),
      funcGen = genFunctionAToB(Gen.int()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

    testLaws(TraversalLaws.laws(
      traversal = Tuple5.traversal(),
      aGen = genTuple(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()),
      bGen = Gen.int(),
      funcGen = genFunctionAToB(Gen.int()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

    testLaws(TraversalLaws.laws(
      traversal = Tuple6.traversal(),
      aGen = genTuple(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()),
      bGen = Gen.int(),
      funcGen = genFunctionAToB(Gen.int()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

    testLaws(TraversalLaws.laws(
      traversal = Tuple7.traversal(),
      aGen = genTuple(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()),
      bGen = Gen.int(),
      funcGen = genFunctionAToB(Gen.int()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

    testLaws(TraversalLaws.laws(
      traversal = Tuple8.traversal(),
      aGen = genTuple(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()),
      bGen = Gen.int(),
      funcGen = genFunctionAToB(Gen.int()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

    testLaws(TraversalLaws.laws(
      traversal = Tuple9.traversal(),
      aGen = genTuple(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()),
      bGen = Gen.int(),
      funcGen = genFunctionAToB(Gen.int()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

    testLaws(TraversalLaws.laws(
      traversal = Tuple10.traversal(),
      aGen = genTuple(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()),
      bGen = Gen.int(),
      funcGen = genFunctionAToB(Gen.int()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

  }

}
