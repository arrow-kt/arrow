package arrow.optics

import arrow.core.ListInstances
import arrow.core.Option
import arrow.data.*
import arrow.instances.listk.monoid.monoid
import arrow.instances.nonemptylist.semigroup.semigroup
import arrow.instances.option.monoid.monoid
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
      aGen = Gen.list(Gen.int()),
      bGen = Gen.int(),
      funcGen = genFunctionAToB(Gen.int()),
      EQA = Eq.any(),
      EQOptionB = Eq.any()
    ))

    testLaws(OptionalLaws.laws(
      optional = ListK.tail(),
      aGen = Gen.list(Gen.int()),
      bGen = Gen.list(Gen.int()),
      funcGen = genFunctionAToB(Gen.list(Gen.int())),
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
      iso = ListInstances.toListK(),
      aGen = Gen.list(Gen.int()),
      bGen = Gen.list(Gen.int()).map { it.k() },
      funcGen = genFunctionAToB(Gen.list(Gen.int()).map { it.k() }),
      EQA = Eq.any(),
      EQB = Eq.any(),
      bMonoid = ListK.monoid())
    )

  }

}
