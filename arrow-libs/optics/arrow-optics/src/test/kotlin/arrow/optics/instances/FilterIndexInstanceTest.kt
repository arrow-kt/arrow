package arrow.optics.instances

import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.intSmall
import arrow.core.test.generators.nonEmptyList
import arrow.core.test.generators.sequence
import arrow.optics.test.generators.char
import arrow.optics.test.laws.TraversalLaws
import arrow.optics.typeclasses.FilterIndex
import io.kotest.property.Arb

class FilterIndexInstanceTest : UnitSpec() {

  init {
    testLaws(TraversalLaws.laws(
      traversal = FilterIndex.list<String>().filter { true },
      aGen = Arb.list(Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = FilterIndex.sequence<String>().filter { true },
        aGen = Gen.sequence(Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      ) { a, b -> a.toList() == b.toList() }
    )

    testLaws(TraversalLaws.laws(
      traversal = FilterIndex.nonEmptyList<String>().filter { true },
      aGen = Gen.nonEmptyList(Arb.string()),
      bGen = Arb.string(),
      funcGen = Arb.functionAToB(Arb.string()),
    ))

    testLaws(
      TraversalLaws.laws(
        traversal = FilterIndex.map<Char, Int>().filter { true },
        aGen = Gen.map(Gen.char(), Gen.intSmall()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

    testLaws(TraversalLaws.laws(
      traversal = FilterIndex.string().filter { true },
      aGen = Arb.string(),
      bGen = Gen.char(),
      funcGen = Arb.functionAToB(Gen.char()),
    ))
  }
}
