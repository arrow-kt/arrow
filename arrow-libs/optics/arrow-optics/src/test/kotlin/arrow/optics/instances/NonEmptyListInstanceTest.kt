package arrow.optics.instances

import arrow.core.ListK
import arrow.core.Option
import arrow.core.extensions.listk.eq.eq
import arrow.core.extensions.option.eq.eq
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.nonEmptyList
import arrow.optics.Traversal
import arrow.optics.nonEmptyList
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.test.laws.TraversalLaws
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class NonEmptyListInstanceTest : UnitSpec() {

  init {

    testLaws(
      TraversalLaws.laws(
        traversal = Traversal.nonEmptyList(),
        aGen = Gen.nonEmptyList(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQOptionB = Eq.any(),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = FilterIndex.nonEmptyList<String>().filter { true },
        aGen = Gen.nonEmptyList(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      OptionalLaws.laws(
        optionalGen = Gen.int().map { Index.nonEmptyList<String>().index(it) },
        aGen = Gen.nonEmptyList(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any()
      )
    )
  }
}
