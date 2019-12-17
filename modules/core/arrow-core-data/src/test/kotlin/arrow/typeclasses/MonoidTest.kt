package arrow.typeclasses

import arrow.Kind
import arrow.core.extensions.monoid
import arrow.core.extensions.monoid.invariant.invariant
import arrow.test.UnitSpec
import arrow.test.laws.InvariantLaws
import arrow.test.laws.equalUnderTheLaw
import io.kotlintest.properties.Gen

class MonoidTest : UnitSpec() {

  val EQ: Eq<MonoidOf<Int>> = Eq.invoke { a, b ->
    a.fix().run { 3.combine(1) } == b.fix().run { 3.combine(1) }
  }

  fun EQK() = object : EqK<ForMonoid> {
    override fun <A> Kind<ForMonoid, A>.eqK(other: Kind<ForMonoid, A>, EQ: Eq<A>): Boolean =
      (this.fix() to other.fix()).let { (ls, rs) ->

        // question: does it still make sense to have this test?
        // couldn't think of a generic way of passing in A values here
        // using empty seems pointless.
        val l = ls.run {
          ls.empty().combine(ls.empty())
        }
        val r = rs.fix().run { rs.empty().combine(rs.empty()) }

        l.equalUnderTheLaw(r, EQ)
      }
  }

  val GEN = Gen.constant(Int.monoid()) as Gen<Kind<ForMonoid, Int>>

  init {
    testLaws(
      InvariantLaws.laws(Monoid.invariant<Int>(), GEN, EQK())
    )
  }
}
