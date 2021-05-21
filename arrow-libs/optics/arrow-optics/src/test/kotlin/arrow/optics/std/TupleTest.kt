package arrow.optics.std

import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.tuple10
import arrow.core.test.generators.tuple4
import arrow.core.test.generators.tuple5
import arrow.core.test.generators.tuple6
import arrow.core.test.generators.tuple7
import arrow.core.test.generators.tuple8
import arrow.core.test.generators.tuple9
import arrow.optics.Lens
import arrow.optics.Traversal
import arrow.optics.test.laws.LensLaws
import arrow.optics.test.laws.TraversalLaws
import io.kotest.property.Arb

class TupleTest : UnitSpec() {

  init {

    testLaws(
      LensLaws.laws(
        lens = Lens.pairFirst(),
        aGen = Gen.pair(Arb.int(), Arb.string()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

    testLaws(
      LensLaws.laws(
        lens = Lens.pairSecond(),
        aGen = Gen.pair(Arb.int(), Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      )
    )

    testLaws(
      LensLaws.laws(
        lens = Lens.tripleFirst(),
        aGen = Gen.triple(Arb.int(), Arb.string(), Arb.string()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

    testLaws(
      LensLaws.laws(
        lens = Lens.tripleSecond(),
        aGen = Gen.triple(Arb.int(), Arb.string(), Arb.int()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      )
    )

    testLaws(
      LensLaws.laws(
        lens = Lens.tripleThird(),
        aGen = Gen.triple(Arb.int(), Arb.int(), Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Traversal.pair(),
        aGen = Gen.pair(Arb.int(), Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Traversal.triple(),
        aGen = Gen.triple(Arb.int(), Arb.int(), Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Traversal.tuple4(),
        aGen = Gen.tuple4(Arb.int(), Arb.int(), Arb.int(), Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Traversal.tuple5(),
        aGen = Gen.tuple5(Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Traversal.tuple6(),
        aGen = Gen.tuple6(Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Traversal.tuple7(),
        aGen = Gen.tuple7(Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Traversal.tuple8(),
        aGen = Gen.tuple8(Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Traversal.tuple9(),
        aGen = Gen.tuple9(
          Arb.int(),
          Arb.int(),
          Arb.int(),
          Arb.int(),
          Arb.int(),
          Arb.int(),
          Arb.int(),
          Arb.int(),
          Arb.int()
        ),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Traversal.tuple10(),
        aGen = Gen.tuple10(
          Arb.int(),
          Arb.int(),
          Arb.int(),
          Arb.int(),
          Arb.int(),
          Arb.int(),
          Arb.int(),
          Arb.int(),
          Arb.int(),
          Arb.int()
        ),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )
  }
}
