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
import arrow.optics.pair
import arrow.optics.pairFirst
import arrow.optics.pairSecond
import arrow.optics.test.laws.LensLaws
import arrow.optics.test.laws.TraversalLaws
import arrow.optics.triple
import arrow.optics.tripleFirst
import arrow.optics.tripleSecond
import arrow.optics.tripleThird
import arrow.optics.tuple10
import arrow.optics.tuple4
import arrow.optics.tuple5
import arrow.optics.tuple6
import arrow.optics.tuple7
import arrow.optics.tuple8
import arrow.optics.tuple9
import io.kotlintest.properties.Gen

class TupleTest : UnitSpec() {

  init {

    testLaws(
      LensLaws.laws(
        lens = Lens.pairFirst(),
        aGen = Gen.pair(Gen.int(), Gen.string()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
      )
    )

    testLaws(
      LensLaws.laws(
        lens = Lens.pairSecond(),
        aGen = Gen.pair(Gen.int(), Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
      )
    )

    testLaws(
      LensLaws.laws(
        lens = Lens.tripleFirst(),
        aGen = Gen.triple(Gen.int(), Gen.string(), Gen.string()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
      )
    )

    testLaws(
      LensLaws.laws(
        lens = Lens.tripleSecond(),
        aGen = Gen.triple(Gen.int(), Gen.string(), Gen.int()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
      )
    )

    testLaws(
      LensLaws.laws(
        lens = Lens.tripleThird(),
        aGen = Gen.triple(Gen.int(), Gen.int(), Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Traversal.pair(),
        aGen = Gen.pair(Gen.int(), Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Traversal.triple(),
        aGen = Gen.triple(Gen.int(), Gen.int(), Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Traversal.tuple4(),
        aGen = Gen.tuple4(Gen.int(), Gen.int(), Gen.int(), Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Traversal.tuple5(),
        aGen = Gen.tuple5(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Traversal.tuple6(),
        aGen = Gen.tuple6(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Traversal.tuple7(),
        aGen = Gen.tuple7(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Traversal.tuple8(),
        aGen = Gen.tuple8(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Traversal.tuple9(),
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
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Traversal.tuple10(),
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
      )
    )
  }
}
