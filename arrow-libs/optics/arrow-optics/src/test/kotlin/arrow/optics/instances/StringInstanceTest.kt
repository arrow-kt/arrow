package arrow.optics.instances

import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.optics.Traversal
import arrow.optics.test.generators.char
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.test.laws.PrismLaws
import arrow.optics.test.laws.TraversalLaws
import arrow.optics.typeclasses.Cons
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.optics.typeclasses.Snoc
import io.kotest.property.Arb

class StringInstanceTest : UnitSpec() {

  init {

    testLaws(
      TraversalLaws.laws(
        traversal = Traversal.string(),
        aGen = Arb.string(),
        bGen = Gen.char(),
        funcGen = Arb.functionAToB(Gen.char()),
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = FilterIndex.string().filter { true },
        aGen = Arb.string(),
        bGen = Gen.char(),
        funcGen = Arb.functionAToB(Gen.char()),
      )
    )

    testLaws(
      OptionalLaws.laws(
        optionalGen = Arb.int().map { Index.string().index(it) },
        aGen = Arb.string(),
        bGen = Gen.char(),
        funcGen = Arb.functionAToB(Gen.char()),
      )
    )

    testLaws(
      PrismLaws.laws(
        prism = Cons.string().cons(),
        aGen = Arb.string(),
        bGen = Gen.pair(Gen.char(), Arb.string()),
        funcGen = Arb.functionAToB(Gen.pair(Gen.char(), Arb.string())),
      )
    )

    testLaws(
      PrismLaws.laws(
        prism = Snoc.string().snoc(),
        aGen = Arb.string(),
        bGen = Gen.pair(Arb.string(), Gen.char()),
        funcGen = Arb.functionAToB(Gen.pair(Arb.string(), Gen.char())),
      )
    )
  }
}
