package arrow.optics.instances

import arrow.optics.test.functionAToB
import arrow.optics.test.laws.TraversalLaws
import arrow.optics.test.laws.testLaws
import arrow.optics.test.nonEmptyList
import arrow.optics.typeclasses.FilterIndex
import io.kotest.property.Arb
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string
import kotlin.test.Test

class FilterIndexInstanceTest {

  @Test
  fun filterIndexListLaws() =
    testLaws(
      TraversalLaws(
        traversal = FilterIndex.list<Int>().filter { true },
        aGen = Arb.list(Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

  @Test
  fun filterIndexSequenceLaws() =
    testLaws(
      TraversalLaws(
        traversal = FilterIndex.sequence<Int>().filter { true },
        aGen = Arb.list(Arb.int()).map { it.asSequence() },
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      ) { a, b -> a.toList() == b.toList() }
    )

  @Test
  fun filterIndexNonEmptyListLaws() =
    testLaws(
      TraversalLaws(
        traversal = FilterIndex.nonEmptyList<Int>().filter { true },
        aGen = Arb.nonEmptyList(Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

  @Test
  fun filterIndexMapLaws() =
    testLaws(
      TraversalLaws(
        traversal = FilterIndex.map<Char, Int>().filter { true },
        aGen = Arb.map(Arb.char(), Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

  @Test
  fun filterIndexStringLaws() =
    testLaws(
      TraversalLaws(
        traversal = FilterIndex.string().filter { true },
        aGen = Arb.string(),
        bGen = Arb.char(),
        funcGen = Arb.functionAToB(Arb.char()),
      )
    )
}
