package arrow.optics

import arrow.core.ListK
import arrow.core.Option
import arrow.core.Tuple10
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9
import arrow.core.extensions.listk.eq.eq
import arrow.core.extensions.monoid
import arrow.core.extensions.option.eq.eq
import arrow.core.extensions.tuple2.monoid.monoid
import arrow.test.UnitSpec
import arrow.test.generators.functionAToB
import arrow.test.generators.tuple10
import arrow.test.generators.tuple2
import arrow.test.generators.tuple3
import arrow.test.generators.tuple4
import arrow.test.generators.tuple5
import arrow.test.generators.tuple6
import arrow.test.generators.tuple7
import arrow.test.generators.tuple8
import arrow.test.generators.tuple9
import arrow.test.laws.LensLaws
import arrow.test.laws.MonoidLaws
import arrow.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class TupleTest : UnitSpec() {

  init {

    testLaws(
      MonoidLaws.laws(
        M = Tuple2.monoid(Int.monoid(), String.monoid()),
        GEN = Gen.tuple2(Gen.int(), Gen.string()),
        EQ = Eq.any()
      )
    )

    testLaws(
      LensLaws.laws(
        lens = Tuple2.first(),
        aGen = Gen.tuple2(Gen.int(), Gen.string()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQB = Eq.any(),
        MB = Int.monoid()
      )
    )

    testLaws(
      LensLaws.laws(
        lens = Tuple2.second(),
        aGen = Gen.tuple2(Gen.int(), Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQB = Eq.any(),
        MB = String.monoid()
      )
    )

    testLaws(
      LensLaws.laws(
        lens = Tuple3.first(),
        aGen = Gen.tuple3(Gen.int(), Gen.string(), Gen.string()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQB = Eq.any(),
        MB = Int.monoid()
      )
    )

    testLaws(
      LensLaws.laws(
        lens = Tuple3.second(),
        aGen = Gen.tuple3(Gen.int(), Gen.string(), Gen.int()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQB = Eq.any(),
        MB = String.monoid()
      )
    )

    testLaws(
      LensLaws.laws(
        lens = Tuple3.third(),
        aGen = Gen.tuple3(Gen.int(), Gen.int(), Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQB = Eq.any(),
        MB = String.monoid()
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Tuple2.traversal(),
        aGen = Gen.tuple2(Gen.int(), Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Tuple3.traversal(),
        aGen = Gen.tuple3(Gen.int(), Gen.int(), Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Tuple4.traversal(),
        aGen = Gen.tuple4(Gen.int(), Gen.int(), Gen.int(), Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Tuple5.traversal(),
        aGen = Gen.tuple5(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Tuple6.traversal(),
        aGen = Gen.tuple6(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Tuple7.traversal(),
        aGen = Gen.tuple7(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Tuple8.traversal(),
        aGen = Gen.tuple8(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Tuple9.traversal(),
        aGen = Gen.tuple9(
          Gen.int(),
          Gen.int(),
          Gen.int(),
          Gen.int(),
          Gen.int(),
          Gen.int(),
          Gen.int(),
          Gen.int(),
          Gen.int()
        ),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Tuple10.traversal(),
        aGen = Gen.tuple10(
          Gen.int(),
          Gen.int(),
          Gen.int(),
          Gen.int(),
          Gen.int(),
          Gen.int(),
          Gen.int(),
          Gen.int(),
          Gen.int(),
          Gen.int()
        ),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )
  }
}
