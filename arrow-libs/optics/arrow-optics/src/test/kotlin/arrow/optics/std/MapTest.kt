package arrow.optics.std

import arrow.core.MapK
import arrow.core.SetK
import arrow.core.extensions.setk.monoid.monoid
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.genSetK
import arrow.core.test.generators.mapK
import arrow.optics.Iso
import arrow.optics.mapToSet
import arrow.optics.test.laws.IsoLaws
import arrow.optics.toSetK
import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import io.kotlintest.properties.Gen

class MapTest : UnitSpec() {

  init {

    testLaws(
      IsoLaws.laws(
        iso = MapK.toSetK(),
        aGen = Gen.mapK(Gen.string(), Gen.create { Unit }),
        bGen = Gen.genSetK(Gen.string()),
        funcGen = Gen.functionAToB(Gen.genSetK(Gen.string())),
        EQA = Eq.any(),
        EQB = Eq.any(),
        bMonoid = SetK.monoid()
      )
    )

    testLaws(
      IsoLaws.laws(
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
      )
    )
  }
}
