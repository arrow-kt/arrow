package arrow.optics.instances

import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.intSmall
import arrow.optics.test.laws.TraversalLaws
import arrow.optics.typeclasses.FilterIndex
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.arrow.laws.testLaws

class FilterIndexInstanceTest : StringSpec() {

  init {
    testLaws(
      "FilterIndex list - ",
      TraversalLaws.laws(
        traversal = FilterIndex.list<String>().filter { true },
        aGen = Arb.list(Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      )
    )

    testLaws(
      "FilterIndex sequence - ",
      TraversalLaws.laws(
        traversal = FilterIndex.sequence<String>().filter { true },
        aGen = Arb.sequence(Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      ) { a, b -> a.toList() == b.toList() }
    )

    testLaws(
      "FilterIndex Nel - ",
      TraversalLaws.laws(
        traversal = FilterIndex.nonEmptyList<String>().filter { true },
        aGen = Arb.nonEmptyList(Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      )
    )

    testLaws(
      "FilterIndex map - ",
      TraversalLaws.laws(
        traversal = FilterIndex.map<Char, Int>().filter { true },
        aGen = Arb.map(Arb.char(), Arb.intSmall()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

    testLaws(
      "FilterIndex string - ",
      TraversalLaws.laws(
        traversal = FilterIndex.string().filter { true },
        aGen = Arb.string(),
        bGen = Arb.char(),
        funcGen = Arb.functionAToB(Arb.char()),
      )
    )
  }
}
