package arrow.optics.instances

import arrow.core.int
import arrow.core.option
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.intSmall
import arrow.core.test.generators.option
import arrow.optics.Traversal
import arrow.optics.map
import arrow.optics.test.generators.char
import arrow.optics.test.laws.LensLaws
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.test.laws.TraversalLaws
import arrow.optics.typeclasses.At
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import io.kotlintest.properties.Gen

class MapInstanceTest : UnitSpec() {

  init {

    testLaws(
      TraversalLaws.laws(
        traversal = Traversal.map(),
        aGen = Gen.map(Gen.int(), Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any()
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = FilterIndex.map<Char, Int>().filter { true },
        aGen = Gen.map(Gen.char(), Gen.intSmall()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any()
      )
    )

    testLaws(
      OptionalLaws.laws(
        optionalGen = Gen.string().map { Index.map<String, Int>().index(it) },
        aGen = Gen.map(Gen.string(), Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any()
      )
    )

    testLaws(
      LensLaws.laws(
        lensGen = Gen.string().map { At.map<String, Int>().at(it) },
        aGen = Gen.map(Gen.string(), Gen.int()),
        bGen = Gen.option(Gen.int()),
        funcGen = Gen.functionAToB(Gen.option(Gen.int())),
        EQA = Eq.any(),
        EQB = Eq.any(),
        MB = Monoid.option(Semigroup.int())
      )
    )
  }
}
