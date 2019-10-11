package arrow.optics

import arrow.core.NonEmptyList
import arrow.core.extensions.monoid
import arrow.test.UnitSpec
import arrow.test.generators.functionAToB
import arrow.test.generators.nonEmptyList
import arrow.test.laws.LensLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import io.kotlintest.properties.Gen

class NonEmptyListTest : UnitSpec() {

  init {

    testLaws(
      LensLaws.laws(
        lens = NonEmptyList.head(),
        aGen = Gen.nonEmptyList(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQB = Eq.any(),
        MB = String.monoid()
      )
    )

    testLaws(LensLaws.laws(
      lens = NonEmptyList.tail(),
      aGen = Gen.nonEmptyList(Gen.string()),
      bGen = Gen.list(Gen.string()),
      funcGen = Gen.functionAToB(Gen.list(Gen.string())),
      EQA = Eq.any(),
      EQB = Eq.any(),
      MB = object : Monoid<List<String>> {
        override fun empty(): List<String> = emptyList()
        override fun List<String>.combine(b: List<String>): List<String> = this + b
      }
    ))
  }
}
