package arrow.optics

import arrow.optics.test.functionAToB
import arrow.optics.test.option
import arrow.optics.test.laws.SetterLaws
import arrow.optics.test.laws.TraversalLaws
import arrow.optics.test.laws.testLaws
import arrow.optics.test.nonEmptyList
import arrow.optics.test.sequence
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string

class TraversalTest : StringSpec({

    testLaws(
      "Traversal list - ",
      TraversalLaws(
        traversal = Traversal.list(),
        aGen = Arb.list(Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      ),

      SetterLaws(
        setter = Traversal.list(),
        aGen = Arb.list(Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

    testLaws(
      "Traversal Nel - ",
      TraversalLaws(
        traversal = Traversal.nonEmptyList(),
        aGen = Arb.nonEmptyList(Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

    testLaws(
      "Traversal sequence - ",
      TraversalLaws(
        traversal = Traversal.sequence(),
        aGen = Arb.sequence(Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
        eq = { a, b -> a.toList() == b.toList() }
      )
    )

    testLaws(
      "Traversal map - ",
      TraversalLaws(
        traversal = Traversal.map(),
        aGen = Arb.map(Arb.int(), Arb.long()),
        bGen = Arb.long(),
        funcGen = Arb.functionAToB(Arb.string()),
      )
    )

    testLaws(
      "Traversal option - ",
      TraversalLaws(
        traversal = Traversal.option(),
        aGen = Arb.option(Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      )
    )

    testLaws(
      "Traversal string - ",
      TraversalLaws(
        traversal = Traversal.string(),
        aGen = Arb.string(),
        bGen = Arb.char(),
        funcGen = Arb.functionAToB(Arb.char()),
      )
    )

})
