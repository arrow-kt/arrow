package arrow.optics.std

import arrow.optics.Lens
import arrow.optics.Traversal
import arrow.optics.test.functionAToB
import arrow.optics.test.laws.LensLaws
import arrow.optics.test.laws.TraversalLaws
import arrow.optics.test.laws.testLaws
import arrow.optics.test.tuple4
import arrow.optics.test.tuple5
import arrow.optics.test.tuple6
import arrow.optics.test.tuple7
import arrow.optics.test.tuple8
import arrow.optics.test.tuple9
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.pair
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.triple

class TupleTest : StringSpec({

    testLaws(
      "Lens pair first - ",
      LensLaws.laws(
        lens = Lens.pairFirst(),
        aGen = Arb.pair(Arb.int(), Arb.string()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

    testLaws(
      "Lens pair second - ",
      LensLaws.laws(
        lens = Lens.pairSecond(),
        aGen = Arb.pair(Arb.int(), Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      )
    )

    testLaws(
      "Lens triple first - ",
      LensLaws.laws(
        lens = Lens.tripleFirst(),
        aGen = Arb.triple(Arb.int(), Arb.string(), Arb.string()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

    testLaws(
      "Lens triple second - ",
      LensLaws.laws(
        lens = Lens.tripleSecond(),
        aGen = Arb.triple(Arb.int(), Arb.string(), Arb.int()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      )
    )

    testLaws(
      "Lens triple third - ",
      LensLaws.laws(
        lens = Lens.tripleThird(),
        aGen = Arb.triple(Arb.int(), Arb.int(), Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      )
    )

    testLaws(
      "Traversal pair - ",
      TraversalLaws.laws(
        traversal = Traversal.pair(),
        aGen = Arb.pair(Arb.boolean(), Arb.boolean()),
        bGen = Arb.boolean(),
        funcGen = Arb.functionAToB(Arb.boolean()),
      )
    )

    testLaws(
      "Traversal triple - ",
      TraversalLaws.laws(
        traversal = Traversal.triple(),
        aGen = Arb.triple(Arb.boolean(), Arb.boolean(), Arb.boolean()),
        bGen = Arb.boolean(),
        funcGen = Arb.functionAToB(Arb.boolean()),
      )
    )

    testLaws(
      "Traversal tuple4 - ",
      TraversalLaws.laws(
        traversal = Traversal.tuple4(),
        aGen = Arb.tuple4(Arb.boolean(), Arb.boolean(), Arb.boolean(), Arb.boolean()),
        bGen = Arb.boolean(),
        funcGen = Arb.functionAToB(Arb.boolean()),
      )
    )

    testLaws(
      "Traversal tuple5 - ",
      TraversalLaws.laws(
        traversal = Traversal.tuple5(),
        aGen = Arb.tuple5(Arb.boolean(), Arb.boolean(), Arb.boolean(), Arb.boolean(), Arb.boolean()),
        bGen = Arb.boolean(),
        funcGen = Arb.functionAToB(Arb.boolean()),
      )
    )

    testLaws(
      "Traversal tuple6 - ",
      TraversalLaws.laws(
        traversal = Traversal.tuple6(),
        aGen = Arb.tuple6(Arb.boolean(), Arb.boolean(), Arb.boolean(), Arb.boolean(), Arb.boolean(), Arb.boolean()),
        bGen = Arb.boolean(),
        funcGen = Arb.functionAToB(Arb.boolean()),
      )
    )

    testLaws(
      "Traversal tuple7 - ",
      TraversalLaws.laws(
        traversal = Traversal.tuple7(),
        aGen = Arb.tuple7(
          Arb.boolean(),
          Arb.boolean(),
          Arb.boolean(),
          Arb.boolean(),
          Arb.boolean(),
          Arb.boolean(),
          Arb.boolean()
        ),
        bGen = Arb.boolean(),
        funcGen = Arb.functionAToB(Arb.boolean()),
      )
    )

    testLaws(
      "Traversal tuple8 - ",
      TraversalLaws.laws(
        traversal = Traversal.tuple8(),
        aGen = Arb.tuple8(
          Arb.boolean(),
          Arb.boolean(),
          Arb.boolean(),
          Arb.boolean(),
          Arb.boolean(),
          Arb.boolean(),
          Arb.boolean(),
          Arb.boolean()
        ),
        bGen = Arb.boolean(),
        funcGen = Arb.functionAToB(Arb.boolean()),
      )
    )

    testLaws(
      "Traversal tuple9 - ",
      TraversalLaws.laws(
        traversal = Traversal.tuple9(),
        aGen = Arb.tuple9(
          Arb.boolean(),
          Arb.boolean(),
          Arb.boolean(),
          Arb.boolean(),
          Arb.boolean(),
          Arb.boolean(),
          Arb.boolean(),
          Arb.boolean(),
          Arb.boolean()
        ),
        bGen = Arb.boolean(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

})
