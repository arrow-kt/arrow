package kategory.instances

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import kategory.UnitSpec
import kategory.eq
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class EqInstancesTest : UnitSpec() {

    init {

        "instances can be resolved implicitly" {
            eq<Byte>() shouldNotBe null
            eq<Short>() shouldNotBe null
            eq<Int>() shouldNotBe null
            eq<Long>() shouldNotBe null
            eq<Float>() shouldNotBe null
            eq<Double>() shouldNotBe null
        }

    }

}