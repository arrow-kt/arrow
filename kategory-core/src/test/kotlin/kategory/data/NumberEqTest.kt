package kategory.data

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import kategory.UnitSpec
import kategory.eq
import kategory.laws.EqLaws
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

        testLaws(EqLaws.laws { it.toLong() })
        testLaws(EqLaws.laws { it })
        testLaws(EqLaws.laws { it.toDouble() })
        testLaws(EqLaws.laws { it.toFloat() })
        testLaws(EqLaws.laws { it.toByte() })
        testLaws(EqLaws.laws { it.toShort() })

    }
}
