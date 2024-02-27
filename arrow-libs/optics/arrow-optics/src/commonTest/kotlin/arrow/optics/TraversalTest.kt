package arrow.optics

import arrow.optics.test.functionAToB
import arrow.optics.test.option
import arrow.optics.test.laws.TraversalLaws
import arrow.optics.test.laws.testLaws
import arrow.optics.test.nonEmptyList
import io.kotest.property.Arb
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string
import kotlin.test.Test

class TraversalTest {

  @Test fun traversalListLaws() =
    testLaws(
      TraversalLaws(
        traversal = Traversal.list(),
        aGen = Arb.list(Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

  @Test fun traversalNonEmptyListLaws() =
    testLaws(
      TraversalLaws(
        traversal = Traversal.nonEmptyList(),
        aGen = Arb.nonEmptyList(Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

  @Test fun traversalSequenceLaws() =
    testLaws(
      TraversalLaws(
        traversal = Traversal.sequence(),
        aGen = Arb.list(Arb.int()).map { it.asSequence() },
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
        eq = { a, b -> a.toList() == b.toList() }
      )
    )

  @Test fun traversalMapLaws() =
    testLaws(
      TraversalLaws(
        traversal = Traversal.map(),
        aGen = Arb.map(Arb.int(), Arb.long()),
        bGen = Arb.long(),
        funcGen = Arb.functionAToB(Arb.string()),
      )
    )

  @Test fun traversalOptionLaws() =
    testLaws(
      TraversalLaws(
        traversal = Traversal.option(),
        aGen = Arb.option(Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      )
    )

  @Test fun traversalStringLaws() =
    testLaws(
      TraversalLaws(
        traversal = Traversal.string(),
        aGen = Arb.string(),
        bGen = Arb.char(),
        funcGen = Arb.functionAToB(Arb.char()),
      )
    )

}
