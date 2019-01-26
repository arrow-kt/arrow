package arrow.optics

import arrow.core.ListExtensions
import arrow.core.Option
import arrow.core.extensions.option.monoid.monoid
import arrow.data.ListK
import arrow.data.NonEmptyList
import arrow.data.extensions.listk.monoid.monoid
import arrow.data.extensions.nonemptylist.semigroup.semigroup
import arrow.data.k
import arrow.test.UnitSpec
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genNonEmptyList
import arrow.test.generators.genOption
import arrow.test.laws.IsoLaws
import arrow.test.laws.OptionalLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class ListTest : UnitSpec() {

  init {

    testLaws(OptionalLaws.laws(
      optional = ListK.head(),
      aGen = genNonEmptyList(Gen.int()).map { it.all },
      bGen = Gen.int(),
      funcGen = genFunctionAToB(Gen.int()),
      EQA = Eq.any(),
      EQOptionB = Eq.any()
    ))

    testLaws(OptionalLaws.laws(
      optional = ListK.tail(),
      aGen = genNonEmptyList(Gen.int()).map { it.all },
      bGen = genNonEmptyList(Gen.int()).map { it.all },
      funcGen = genFunctionAToB(genNonEmptyList(Gen.int()).map { it.all }),
      EQA = Eq.any(),
      EQOptionB = Eq.any()
    ))

    testLaws(IsoLaws.laws(
      iso = ListK.toOptionNel(),
      aGen = Gen.list(Gen.int()),
      bGen = genOption(genNonEmptyList(Gen.int())),
      funcGen = genFunctionAToB(genOption(genNonEmptyList(Gen.int()))),
      EQA = Eq.any(),
      EQB = Eq.any(),
      bMonoid = Option.monoid(NonEmptyList.semigroup<Int>())
    ))

    testLaws(IsoLaws.laws(
      iso = ListExtensions.toListK(),
      aGen = Gen.list(Gen.int()),
      bGen = Gen.list(Gen.int()).map { it.k() },
      funcGen = genFunctionAToB(Gen.list(Gen.int()).map { it.k() }),
      EQA = Eq.any(),
      EQB = Eq.any(),
      bMonoid = ListK.monoid())
    )

  }

}
