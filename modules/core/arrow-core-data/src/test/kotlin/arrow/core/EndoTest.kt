package arrow.core

import arrow.core.extensions.endo.monoid.monoid
import arrow.test.UnitSpec
import arrow.test.generators.endo
import arrow.test.laws.MonoidLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class EndoTest : UnitSpec() {
  val EQ: Eq<Endo<Int>> = Eq { a, b ->
    a.f(9) == b.f(1)
  }

  init {
    testLaws(
      MonoidLaws.laws(Endo.monoid(), Gen.endo(Gen.int()), EQ)
    )
  }
}
