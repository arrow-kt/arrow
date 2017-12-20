package arrow.data

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import arrow.UnitSpec
import arrow.eq
import arrow.laws.EqLaws
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class NumberEqTest : UnitSpec() {
    init {

        "instances can be resolved implicitly" {
            eq<Long>() shouldNotBe null
            eq<Int>() shouldNotBe null
            eq<Double>() shouldNotBe null
            eq<Float>() shouldNotBe null
            eq<Byte>() shouldNotBe null
            eq<Short>() shouldNotBe null
            eq<java.lang.Integer>() shouldNotBe null
        }

        testLaws(
            EqLaws.laws { it.toLong() },
            EqLaws.laws { it },
            EqLaws.laws { it.toDouble() },
            EqLaws.laws { it.toFloat() },
            EqLaws.laws { it.toByte() },
            EqLaws.laws { it.toShort() }
        )

    }
}
