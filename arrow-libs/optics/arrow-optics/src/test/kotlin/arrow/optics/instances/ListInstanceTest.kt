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
import io.kotest.property.Arb

class ListInstanceTest : UnitSpec() {

  init {

    testLaws(
      TraversalLaws.laws(
        traversal = Traversal.list(),
        aGen = Arb.list(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Traversal.list(),
        aGen = Arb.list(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = FilterIndex.list<String>().filter { true },
        aGen = Arb.list(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
      )
    )

    testLaws(
      OptionalLaws.laws(
        optionalGen = Gen.int().map { Index.list<String>().index(it) },
        aGen = Arb.list(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
      )
    )

    testLaws(
      PrismLaws.laws(
        prism = Cons.list<Int>().cons(),
        aGen = Arb.list(Gen.int()),
        bGen = Gen.pair(Gen.int(), Arb.list(Gen.int())),
        funcGen = Gen.functionAToB(Gen.pair(Gen.int(), Arb.list(Gen.int()))),
      )
    )

    testLaws(
      PrismLaws.laws(
        prism = Snoc.list<Int>().snoc(),
        aGen = Arb.list(Gen.int()),
        bGen = Gen.pair(Arb.list(Gen.int()), Gen.int()),
        funcGen = Gen.functionAToB(Gen.pair(Arb.list(Gen.int()), Gen.int())),
      )
    )
  }
}
