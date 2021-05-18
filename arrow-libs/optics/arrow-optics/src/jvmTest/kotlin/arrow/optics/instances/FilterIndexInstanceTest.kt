package arrow.optics.instances

import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.intSmall
import arrow.core.test.generators.nonEmptyList
import arrow.core.test.generators.sequence
import arrow.optics.test.generators.char
import arrow.optics.test.laws.TraversalLaws
import arrow.optics.typeclasses.FilterIndex
import io.kotlintest.properties.Gen

class FilterIndexInstanceTest : UnitSpec() {

  init {
    testLaws(TraversalLaws.laws(
      traversal = FilterIndex.list<String>().filter { true },
      aGen = Gen.list(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = FilterIndex.sequence<String>().filter { true },
        aGen = Gen.sequence(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
      ) { a, b -> a.toList() == b.toList() }
    )

    testLaws(TraversalLaws.laws(
      traversal = FilterIndex.nonEmptyList<String>().filter { true },
      aGen = Gen.nonEmptyList(Gen.string()),
      bGen = Gen.string(),
      funcGen = Gen.functionAToB(Gen.string()),
    ))

    testLaws(
      TraversalLaws.laws(
        traversal = FilterIndex.map<Char, Int>().filter { true },
        aGen = Gen.map(Gen.char(), Gen.intSmall()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
      )
    )

    testLaws(TraversalLaws.laws(
      traversal = FilterIndex.string().filter { true },
      aGen = Gen.string(),
      bGen = Gen.char(),
      funcGen = Gen.functionAToB(Gen.char()),
    ))
  }
}
