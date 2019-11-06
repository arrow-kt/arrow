package arrow.typeclasses

import arrow.core.extensions.monoid
import arrow.core.extensions.monoid.invariant.invariant
import arrow.test.UnitSpec
import arrow.test.laws.InvariantLaws

class MonoidTest : UnitSpec() {

    val EQ: Eq<MonoidOf<Int>> = Eq.invoke { a, b ->
      a.fix().run { 3.combine(1) } == b.fix().run { 3.combine(1) }
    }

    init {
        testLaws(
            InvariantLaws.laws(Monoid.invariant<Int>(), { Int.monoid() }, EQ)
        )
    }
}
