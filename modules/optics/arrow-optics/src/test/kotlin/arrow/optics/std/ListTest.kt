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
import arrow.test.generators.*
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
      aGen = Gen.list(Gen.int()),
      bGen = Gen.int(),
      funcGen = Gen.functionAToB(Gen.int()),
      EQA = Eq.any(),
      EQOptionB = Eq.any()
    ))

    testLaws(OptionalLaws.laws(
      optional = ListK.tail(),
      aGen = Gen.list(Gen.int()),
      bGen = Gen.list(Gen.int()),
      funcGen = Gen.functionAToB(Gen.list(Gen.int())),
      EQA = Eq.any(),
      EQOptionB = Eq.any()
    ))

    testLaws(IsoLaws.laws(
      iso = ListK.toOptionNel(),
      aGen = Gen.list(Gen.int()),
      bGen = Gen.option(Gen.nonEmptyList(Gen.int())),
      funcGen = Gen.functionAToB(Gen.option(Gen.nonEmptyList(Gen.int()))),
      EQA = Eq.any(),
      EQB = Eq.any(),
      bMonoid = Option.monoid(NonEmptyList.semigroup<Int>())
    ))

    testLaws(IsoLaws.laws(
      iso = ListExtensions.toListK(),
      aGen = Gen.list(Gen.int()),
      bGen = Gen.list(Gen.int()).map { it.k() },
      funcGen = Gen.functionAToB(Gen.list(Gen.int()).map { it.k() }),
      EQA = Eq.any(),
      EQB = Eq.any(),
      bMonoid = ListK.monoid())
    )

  }

}
