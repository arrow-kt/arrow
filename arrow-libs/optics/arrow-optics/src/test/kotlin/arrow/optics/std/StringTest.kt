package arrow.optics.std

import arrow.optics.test.generators.char
import arrow.core.test.UnitSpec
import arrow.optics.Iso
import arrow.optics.stringToList
import arrow.optics.test.laws.IsoLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import io.kotlintest.properties.Gen

class StringTest : UnitSpec() {

  init {

    testLaws(
      IsoLaws.laws(
        iso = Iso.stringToList(),
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
