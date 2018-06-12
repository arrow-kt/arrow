package arrow.optics

import arrow.data.NonEmptyList
import arrow.instances.monoid
import arrow.test.UnitSpec
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genNonEmptyList
import arrow.test.generators.genOption
import arrow.test.laws.IsoLaws
import arrow.test.laws.LensLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class NonEmptyListTest : UnitSpec() {

  init {

    testLaws(LensLaws.laws(
      lens = NonEmptyList.head(),
      aGen = genNonEmptyList(Gen.string()),
      bGen = Gen.string(),
      funcGen = genFunctionAToB(Gen.string()),
      EQA = Eq.any(),
      EQB = Eq.any(),
      MB = String.monoid())
    )

  }

}
