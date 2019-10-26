package arrow.optics

import arrow.core.extensions.monoid
import arrow.test.UnitSpec
import arrow.test.generators.char
import arrow.test.laws.IsoLaws
import arrow.test.laws.MonoidLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import io.kotlintest.properties.Gen

class StringTest : UnitSpec() {

  init {

    testLaws(
      MonoidLaws.laws(String.monoid(), Gen.string(), Eq.any()),
      IsoLaws.laws(
      iso = String.toList(),
      aGen = Gen.string(),
      bGen = Gen.list(Gen.char()),
      funcGen = Gen.list(Gen.char()).map { list -> { chars: List<Char> -> list + chars } },
      EQA = Eq.any(),
      EQB = Eq.any(),
      bMonoid = object : Monoid<List<Char>> {
        override fun List<Char>.combine(b: List<Char>): List<Char> = this + b
        override fun empty(): List<Char> = emptyList()
      }
    ))
  }
}
