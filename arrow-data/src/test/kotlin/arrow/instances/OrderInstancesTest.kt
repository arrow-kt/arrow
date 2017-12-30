package arrow.instances

import arrow.syntax.order.toOrder
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.properties.Gen
import arrow.test.laws.OrderLaws
import arrow.test.UnitSpec
import arrow.test.generators.genFunctionAToB
import arrow.typeclasses.order
import io.kotlintest.matchers.shouldBe
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
