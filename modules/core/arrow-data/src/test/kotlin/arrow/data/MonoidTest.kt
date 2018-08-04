package arrow.data

import arrow.instances.monoid
import arrow.test.UnitSpec
import arrow.test.laws.InvariantLaws
import arrow.typeclasses.*
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
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