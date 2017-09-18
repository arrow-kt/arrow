package kategory.instances

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import kategory.UnitSpec
import kategory.order
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class OrderInstancesTest : UnitSpec() {
    init {
        "instances can be resolved implicitly" {
            order<Byte>() shouldNotBe null
            order<Short>() shouldNotBe null
            order<Int>() shouldNotBe null
            order<Long>() shouldNotBe null
            order<Float>() shouldNotBe null
            order<Double>() shouldNotBe null
        }
    }
}
