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
        aGen = Arb.list(Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Traversal.list(),
        aGen = Arb.list(Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = FilterIndex.list<String>().filter { true },
        aGen = Arb.list(Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      )
    )

    testLaws(
      OptionalLaws.laws(
        optionalGen = Arb.int().map { Index.list<String>().index(it) },
        aGen = Arb.list(Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      )
    )

    testLaws(
      PrismLaws.laws(
        prism = Cons.list<Int>().cons(),
        aGen = Arb.list(Arb.int()),
        bGen = Gen.pair(Arb.int(), Arb.list(Arb.int())),
        funcGen = Arb.functionAToB(Gen.pair(Arb.int(), Arb.list(Arb.int()))),
      )
    )

    testLaws(
      PrismLaws.laws(
        prism = Snoc.list<Int>().snoc(),
        aGen = Arb.list(Arb.int()),
        bGen = Gen.pair(Arb.list(Arb.int()), Arb.int()),
        funcGen = Arb.functionAToB(Gen.pair(Arb.list(Arb.int()), Arb.int())),
      )
    )
  }
}
