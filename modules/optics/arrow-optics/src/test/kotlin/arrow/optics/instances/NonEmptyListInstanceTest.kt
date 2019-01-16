package arrow.optics.instances

import arrow.core.Option
import arrow.data.ListK
import arrow.data.NonEmptyList
import arrow.data.extensions.listk.eq.eq
import arrow.core.extensions.option.eq.eq
import arrow.optics.extensions.nonemptylist.each.each
import arrow.optics.extensions.nonemptylist.filterIndex.filterIndex
import arrow.optics.extensions.nonemptylist.index.index
import arrow.test.UnitSpec
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genNonEmptyList
import arrow.test.laws.OptionalLaws
import arrow.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class NonEmptyListInstanceTest : UnitSpec() {

  init {

    testLaws(
      TraversalLaws.laws(
        traversal = NonEmptyList.each<String>().each(),
        aGen = genNonEmptyList(Gen.string()),
        bGen = Gen.string(),
        funcGen = genFunctionAToB(Gen.string()),
        EQA = Eq.any(),
        EQOptionB = Eq.any(),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = NonEmptyList.filterIndex<String>().filter { true },
        aGen = genNonEmptyList(Gen.string()),
        bGen = Gen.string(),
        funcGen = genFunctionAToB(Gen.string()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      OptionalLaws.laws(
        optionalGen = Gen.int().map { NonEmptyList.index<String>().index(it) },
        aGen = genNonEmptyList(Gen.string()),
        bGen = Gen.string(),
        funcGen = genFunctionAToB(Gen.string()),
        EQOptionB = Eq.any(),
        EQA = Eq.any()
      )
    )

  }
}
