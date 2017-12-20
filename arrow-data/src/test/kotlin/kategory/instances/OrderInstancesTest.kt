package arrow.instances

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.properties.Gen
import arrow.OrderLaws
import arrow.UnitSpec
import arrow.genFunctionAToB
import arrow.order
import arrow.toOrder
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class OrderInstancesTest : UnitSpec() {
    init {

        testLaws(
                OrderLaws.laws(
                        O = order(),
                        fGen = Gen.int(),
                        funcGen = genFunctionAToB(Gen.int())
                )
        )

        "instances can be resolved implicitly" {
            order<Byte>() shouldNotBe null
            order<Short>() shouldNotBe null
            order<Int>() shouldNotBe null
            order<Long>() shouldNotBe null
            order<Float>() shouldNotBe null
            order<Double>() shouldNotBe null
        }

        "from comparable" {
            val toOrder = toOrder<Int>()
            toOrder.gt(10,1) shouldBe true
            toOrder.lt(10,1) shouldBe false
        }

    }
}
