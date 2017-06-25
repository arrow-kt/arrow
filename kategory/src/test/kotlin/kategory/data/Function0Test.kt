package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class Function0Test : UnitSpec() {
    init {

        testLaws(MonadLaws.laws(Function0))

        "Function0 should trigger lazily" {
            val counter: SideEffect = SideEffect()
            val function0 = Function0 {
                counter.increment()
                counter.counter
            }
            counter.counter shouldBe 0
            function0.f() shouldBe 1
            counter.counter shouldBe 1
        }

        "Function0 should memoize a call to f" {
            val counter: SideEffect = SideEffect()
            val function0 = Function0 {
                counter.increment()
                counter.counter
            }
            counter.counter shouldBe 0
            function0.f() shouldBe 1
            function0.f() shouldBe 1
        }

        "Function0Monad.binding should for comprehend over all values of multiple Function0" {
            Function0.binding {
                val x = Function0 { 1 }.bind()
                val y = !Function0 { 2 }
                val z = bind { Function0 { 3 } }
                yields(x + y + z)
            }.ev().invoke() shouldBe 6
        }

        "Function0Comonad.cobinding should for comprehend over all values of multiple Function0" {
            Function0.cobinding {
                val x = Function0 { 1 }.extract()
                val y = !Function0 { 2 }
                val z = extract { Function0 { 3 } }
                yields(x + y + z)
            } shouldBe 6
        }

        "Function0Comonad.duplicate should create an instance of Function0<Function0<A>>" {
            Function0.duplicate(Function0 { 3 }).ev().invoke().ev().invoke() shouldBe
                    Function0 { Function0 { 3 } }.ev().invoke().ev().invoke()
        }
    }
}
