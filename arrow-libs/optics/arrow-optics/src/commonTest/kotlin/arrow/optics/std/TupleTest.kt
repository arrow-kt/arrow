package arrow.optics.std

import arrow.optics.Lens
import arrow.optics.Traversal
import arrow.optics.test.functionAToB
import arrow.optics.test.laws.LensLaws
import arrow.optics.test.laws.TraversalLaws
import arrow.optics.test.laws.testLaws
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
      LensLaws(
        lens = Lens.pairFirst(),
        aGen = Arb.pair(Arb.int(), Arb.string()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

    testLaws(
      "Lens pair second - ",
      LensLaws(
        lens = Lens.pairSecond(),
        aGen = Arb.pair(Arb.int(), Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      )
    )

    testLaws(
      "Lens triple first - ",
      LensLaws(
        lens = Lens.tripleFirst(),
        aGen = Arb.triple(Arb.int(), Arb.string(), Arb.string()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

    testLaws(
      "Lens triple second - ",
      LensLaws(
        lens = Lens.tripleSecond(),
        aGen = Arb.triple(Arb.int(), Arb.string(), Arb.int()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      )
    )

    testLaws(
      "Lens triple third - ",
      LensLaws(
        lens = Lens.tripleThird(),
        aGen = Arb.triple(Arb.int(), Arb.int(), Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      )
    )

    testLaws(
      "Traversal pair - ",
      TraversalLaws(
        traversal = Traversal.pair(),
        aGen = Arb.pair(Arb.boolean(), Arb.boolean()),
        bGen = Arb.boolean(),
        funcGen = Arb.functionAToB(Arb.boolean()),
      )
    )

    testLaws(
      "Traversal triple - ",
      TraversalLaws(
        traversal = Traversal.triple(),
        aGen = Arb.triple(Arb.boolean(), Arb.boolean(), Arb.boolean()),
        bGen = Arb.boolean(),
        funcGen = Arb.functionAToB(Arb.boolean()),
      )
    )

})
