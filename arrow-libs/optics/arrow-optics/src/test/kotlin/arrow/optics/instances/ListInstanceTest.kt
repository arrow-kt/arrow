package arrow.optics.instances

import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.optics.Traversal
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.test.laws.PrismLaws
import arrow.optics.test.laws.TraversalLaws
import arrow.optics.typeclasses.Cons
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.optics.typeclasses.Snoc
import io.kotlintest.properties.Gen

class ListInstanceTest : UnitSpec() {

  init {

    testLaws(
      TraversalLaws.laws(
        traversal = Traversal.list(),
        aGen = Gen.list(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Traversal.list(),
        aGen = Gen.list(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = FilterIndex.list<String>().filter { true },
        aGen = Gen.list(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
      )
    )

    testLaws(
      OptionalLaws.laws(
        optionalGen = Gen.int().map { Index.list<String>().index(it) },
        aGen = Gen.list(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
      )
    )

    testLaws(
      PrismLaws.laws(
        prism = Cons.list<Int>().cons(),
        aGen = Gen.list(Gen.int()),
        bGen = Gen.pair(Gen.int(), Gen.list(Gen.int())),
        funcGen = Gen.functionAToB(Gen.pair(Gen.int(), Gen.list(Gen.int()))),
      )
    )

    testLaws(
      PrismLaws.laws(
        prism = Snoc.list<Int>().snoc(),
        aGen = Gen.list(Gen.int()),
        bGen = Gen.pair(Gen.list(Gen.int()), Gen.int()),
        funcGen = Gen.functionAToB(Gen.pair(Gen.list(Gen.int()), Gen.int())),
      )
    )
  }
}
