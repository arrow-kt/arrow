package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class Function1Test : UnitSpec() {
    init {
        testLaws(MonadLaws.laws(Function1.monad<Int>(), object : Eq<Function1Kind<Int, Int>> {
            override fun eqv(a: Function1Kind<Int, Int>, b: Function1Kind<Int, Int>): Boolean =
                a(1) == b(1)
        }))

        "Function1Monad.binding should for comprehend over all values of multiple Function0" {
            val M = Function1.monad<Int>()
            M.binding {
                val x = Function1 { _: Int -> 1 }.bind()
                val y = bind { Function1 { _: Int -> 2 } }
                yields(x + y)
            }.invoke(0) shouldBe 3
        }
    }
}