package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class Function0Test : UnitSpec() {
    init {

        testLaws(MonadLaws.laws(Function0, object : Eq<HK<Function0HK, Int>> {
            override fun eqv(a: HK<Function0HK, Int>, b: HK<Function0HK, Int>): Boolean =
                    a() == b()
        }))

        "Function0Monad.binding should for comprehend over all values of multiple Function0" {
            Function0.binding {
                val x = Function0 { 1 }.bind()
                val y = bind { Function0 { 2 } }
                yields(x + y)
            }.ev().invoke() shouldBe 3
        }

        "Function0Comonad.cobinding should for comprehend over all values of multiple Function0" {
            Function0.cobinding {
                val x = Function0 { 1 }.extract()
                val y = !Function0 { 2 }
                val z = extract { Function0 { 3 } }
                x + y + z
            } shouldBe 6
        }

        "Function0Comonad.duplicate should create an instance of Function0<Function0<A>>" {
            Function0.duplicate(Function0 { 3 }).invoke().invoke() shouldBe
                    Function0 { Function0 { 3 } }.invoke().invoke()
        }
    }
}
