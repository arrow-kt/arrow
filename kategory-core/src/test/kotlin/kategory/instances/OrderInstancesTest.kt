package kategory.instances

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.properties.Gen
import kategory.OrderLaws
import kategory.UnitSpec
import kategory.genFunctionAToB
import kategory.order
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
    }
}
