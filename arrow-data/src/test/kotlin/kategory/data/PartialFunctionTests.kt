package arrow

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class PartialFunctionTests : UnitSpec() {

    init {

        "case syntax can be used to create partial functions" {
            val predicate = { n: Int -> n > 0 }
            val fa = case({ n: Int -> n > 0 } then { it * 2 })
            val fb = object : PartialFunction<Int, Int>() {
                override fun isDefinedAt(a: Int): Boolean = predicate(a)
                override fun invoke(p1: Int): Int = p1 * 2
            }
            fa(1) shouldBe fb(1)
            fa.isDefinedAt(-1) shouldBe false
        }

    }
}
