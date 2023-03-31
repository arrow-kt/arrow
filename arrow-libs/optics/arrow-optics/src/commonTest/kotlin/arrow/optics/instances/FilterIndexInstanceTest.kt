package arrow.optics.instances

import arrow.optics.test.functionAToB
import arrow.optics.test.laws.TraversalLaws
import arrow.optics.test.laws.testLaws
import arrow.optics.test.nonEmptyList
import arrow.optics.typeclasses.FilterIndex
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string

class FilterIndexInstanceTest : StringSpec({

    testLaws(
      "FilterIndex list - ",
      TraversalLaws(
        traversal = FilterIndex.list<Int>().filter { true },
        aGen = Arb.list(Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

    testLaws(
      "FilterIndex sequence - ",
      TraversalLaws(
        traversal = FilterIndex.sequence<Int>().filter { true },
        aGen = Arb.list(Arb.int()).map { it.asSequence() },
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      ) { a, b -> a.toList() == b.toList() }
    )

    testLaws(
      "FilterIndex Nel - ",
      TraversalLaws(
        traversal = FilterIndex.nonEmptyList<Int>().filter { true },
        aGen = Arb.nonEmptyList(Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

    testLaws(
      "FilterIndex map - ",
      TraversalLaws(
        traversal = FilterIndex.map<Char, Int>().filter { true },
        aGen = Arb.map(Arb.char(), Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

    testLaws(
      "FilterIndex string - ",
      TraversalLaws(
        traversal = FilterIndex.string().filter { true },
        aGen = Arb.string(),
        bGen = Arb.char(),
        funcGen = Arb.functionAToB(Arb.char()),
      )
    )
})
