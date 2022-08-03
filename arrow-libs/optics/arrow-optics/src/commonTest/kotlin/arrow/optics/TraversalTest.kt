package arrow.optics

import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.arrow.core.functionAToB
import io.kotest.property.arrow.core.nonEmptyList
import io.kotest.property.arrow.core.option
import io.kotest.property.arrow.laws.testLaws
import io.kotest.property.arrow.optics.SetterLaws
import io.kotest.property.arrow.optics.TraversalLaws

class TraversalTest : StringSpec() {

  init {

    testLaws(
      "Traversal list - ",
      TraversalLaws.laws(
        traversal = Traversal.list(),
        aGen = Arb.list(Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      ),

      SetterLaws.laws(
        setter = Traversal.list(),
        aGen = Arb.list(Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
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
        aGen = Arb.list(Arb.string()).map { it.asSequence() },
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
