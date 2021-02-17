package arrow.optics.std

import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.optics.Iso
import arrow.optics.mapToSet
import arrow.optics.test.laws.IsoLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import io.kotlintest.properties.Gen

class MapTest : UnitSpec() {

  init {
    testLaws(IsoLaws.laws(
      iso = Iso.mapToSet(),
      aGen = Gen.map(Gen.string(), Gen.create { Unit }),
      bGen = Gen.set(Gen.string()),
      funcGen = Gen.functionAToB(Gen.set(Gen.string())),
      EQA = Eq.any(),
      EQB = Eq.any(),
      bMonoid = object : Monoid<Set<String>> {
        override fun empty(): Set<String> = emptySet()
        override fun Set<String>.combine(b: Set<String>): Set<String> = this + b
      }
    ))
  }
}
