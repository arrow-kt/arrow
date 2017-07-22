package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class Function1Test : UnitSpec() {
    init {
        testLaws(MonadLaws.laws(Function1.monad<Int>(), object : Eq<HK<Function1.F, Int>> {
            override fun eqv(a: HK<Function1.F, Int>, b: HK<Function1.F, Int>): Boolean =
                    a.invoke(1) == b.invoke(1)
        }))

        "Function1Monad.binding should for comprehend over all values of multiple Function0" {
            val M = Function1.monad<Int>()
            M.binding {
                val x = Function1 { _: Int -> 1 }.bind()
                val y = !Function1 { _: Int -> 2 }
                val z = bind { Function1 { _: Int -> 3 } }
                yields(x + y + z)
            }.invoke(0) shouldBe 6
        }
    }
}