package arrow.optics.instances

import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.optics.Traversal
import arrow.optics.string
import arrow.optics.test.generators.char
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.test.laws.PrismLaws
import arrow.optics.test.laws.TraversalLaws
import arrow.optics.typeclasses.Cons
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.optics.typeclasses.Snoc
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class StringInstanceTest : UnitSpec() {

  init {

    testLaws(
      TraversalLaws.laws(
        traversal = Traversal.string(),
        aGen = Gen.string(),
        bGen = Gen.char(),
        funcGen = Gen.functionAToB(Gen.char()),
        EQA = Eq.any()
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = FilterIndex.string().filter { true },
        aGen = Gen.string(),
        bGen = Gen.char(),
        funcGen = Gen.functionAToB(Gen.char()),
        EQA = Eq.any()
      )
    )

    testLaws(
      OptionalLaws.laws(
        optionalGen = Gen.int().map { Index.string().index(it) },
        aGen = Gen.string(),
        bGen = Gen.char(),
        funcGen = Gen.functionAToB(Gen.char()),
        EQA = Eq.any()
      )
    )

    testLaws(
      PrismLaws.laws(
        prism = Cons.string().cons(),
        aGen = Gen.string(),
        bGen = Gen.pair(Gen.char(), Gen.string()),
        funcGen = Gen.functionAToB(Gen.pair(Gen.char(), Gen.string())),
        EQA = Eq.any()
      )
    )

    testLaws(
      PrismLaws.laws(
        prism = Snoc.string().snoc(),
        aGen = Gen.string(),
        bGen = Gen.pair(Gen.string(), Gen.char()),
        funcGen = Gen.functionAToB(Gen.pair(Gen.string(), Gen.char())),
        EQA = Eq.any()
      )
    )
  }
}
