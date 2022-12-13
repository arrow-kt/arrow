package arrow.optics

import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.option
import arrow.optics.test.laws.TraversalLaws
import io.kotest.property.Arb
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string

class TraversalTest : UnitSpec() {

  init {

    testLaws(
      "Traversal list - ",
      TraversalLaws.laws(
        traversal = Traversal.list(),
        aGen = Arb.list(Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      ),
    )

    testLaws(
      "Traversal Nel - ",
      TraversalLaws.laws(
        traversal = Traversal.nonEmptyList(),
        aGen = Arb.nonEmptyList(Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      )
    )

    testLaws(
      "Traversal sequence - ",
      TraversalLaws.laws(
        traversal = Traversal.sequence(),
        aGen = Arb.sequence(Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
        eq = { a, b -> a.toList() == b.toList() }
      )
    )

    testLaws(
      "Traversal map - ",
      TraversalLaws.laws(
        traversal = Traversal.map(),
        aGen = Arb.map(Arb.int(), Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      )
    )

    testLaws(
      "Traversal option - ",
      TraversalLaws.laws(
        traversal = Traversal.option(),
        aGen = Arb.option(Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      )
    )

    testLaws(
      "Traversal string - ",
      TraversalLaws.laws(
        traversal = Traversal.string(),
        aGen = Arb.string(),
        bGen = Arb.char(),
        funcGen = Arb.functionAToB(Arb.char()),
      )
    )
  }
}
