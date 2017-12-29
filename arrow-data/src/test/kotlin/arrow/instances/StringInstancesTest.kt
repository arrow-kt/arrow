package arrow.instances

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import arrow.*
import arrow.test.laws.EqLaws
import org.junit.runner.RunWith
import arrow.test.UnitSpec

@RunWith(KTestJUnitRunner::class)
class StringInstancesTest : UnitSpec() {
    init {
        "instances can be resolved implicitly" {
            semigroup<String>() shouldNotBe null
            monoid<String>() shouldNotBe null
            eq<String>() shouldNotBe null
        }

        testLaws(EqLaws.laws { it.toString() })
    }
}
