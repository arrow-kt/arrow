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
        a.fix() == b.fix()
    }

    init {
        testLaws(
            InvariantLaws.laws(Monoid.invariant<Int>(), { Int.monoid() }, EQ)
        )
    }
}