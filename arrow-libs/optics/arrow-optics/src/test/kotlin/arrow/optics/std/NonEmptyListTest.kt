package arrow.optics.std

import arrow.core.NonEmptyList
import arrow.core.extensions.monoid
import arrow.optics.head
import arrow.optics.tail
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.nonEmptyList
import arrow.optics.test.laws.LensLaws
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
