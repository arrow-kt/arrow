package arrow.effects.data

import arrow.effects.Duration
import arrow.test.UnitSpec
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

/**
 *
 */
@RunWith(KTestJUnitRunner::class)
class DurationTest : UnitSpec() {

    init {
        "plus should be commutative" {
            forAll(Gen.long(), TimeUnitGen(), Gen.long(), TimeUnitGen()) { i: Long, u: TimeUnit, j: Long, v: TimeUnit ->
                val a = Duration(i, u)
                val b = Duration(j, v)
                a + b == b + a
            }
        }
    }

}

private class TimeUnitGen : Gen<TimeUnit> {

    companion object {
        val units = TimeUnit.values()
    }

    override fun generate(): TimeUnit = units[Gen.choose(0, units.size -1).generate()]
}