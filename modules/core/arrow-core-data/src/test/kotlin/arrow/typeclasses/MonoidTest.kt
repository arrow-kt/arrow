package arrow.typeclasses

import arrow.Kind
import arrow.core.extensions.monoid
import arrow.core.extensions.monoid.invariant.invariant
import arrow.test.UnitSpec
import arrow.test.laws.InvariantLaws
import io.kotlintest.properties.Gen

class MonoidTest : UnitSpec() {

  val EQ: Eq<MonoidOf<Int>> = Eq.invoke { a, b ->
    a.fix().run { 3.combine(1) } == b.fix().run { 3.combine(1) }
  }

  val GEN = Gen.constant(Int.monoid()) as Gen<Kind<ForMonoid, Int>>

  init {
    testLaws(
      InvariantLaws.laws(Monoid.invariant<Int>(), GEN, EQ)
    )
  }
}
