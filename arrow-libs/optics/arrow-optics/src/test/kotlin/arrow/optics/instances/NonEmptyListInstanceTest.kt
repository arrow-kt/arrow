package arrow.optics.instances

import arrow.core.Option
import arrow.core.ListK
import arrow.core.NonEmptyList
import arrow.core.extensions.listk.eq.eq
import arrow.core.extensions.option.eq.eq
import arrow.optics.extensions.nonemptylist.each.each
import arrow.optics.extensions.nonemptylist.filterIndex.filterIndex
import arrow.optics.extensions.nonemptylist.index.index
import arrow.test.UnitSpec
import arrow.test.generators.functionAToB
import arrow.test.generators.nonEmptyList
import arrow.test.laws.OptionalLaws
import arrow.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class NonEmptyListInstanceTest : UnitSpec() {

  init {

    testLaws(
      TraversalLaws.laws(
        traversal = NonEmptyList.each<String>().each(),
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
        traversal = NonEmptyList.filterIndex<String>().filter { true },
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
        optionalGen = Gen.int().map { NonEmptyList.index<String>().index(it) },
        aGen = Gen.nonEmptyList(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQOptionB = Eq.any(),
        EQA = Eq.any()
      )
    )
  }
}
