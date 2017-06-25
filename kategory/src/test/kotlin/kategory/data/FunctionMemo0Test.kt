package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class FunctionMemo0Test : UnitSpec() {
    init {

        testLaws(MonadLaws.laws(FunctionMemo0))

        "Function0 should trigger lazily" {
            val counter: SideEffect = SideEffect()
            val function0 = FunctionMemo0 {
                counter.increment()
                counter.counter
            }
            counter.counter shouldBe 0
            function0.f() shouldBe 1
            counter.counter shouldBe 1
        }

        "Function0 should memoize a call to f" {
            val counter: SideEffect = SideEffect()
            val function0 = FunctionMemo0 {
                counter.increment()
                counter.counter
            }
            counter.counter shouldBe 0
            function0.f() shouldBe 1
            function0.f() shouldBe 1
        }

        "Function0Monad.binding should for comprehend over all values of multiple Function0" {
            FunctionMemo0.binding {
                val x = FunctionMemo0 { 1 }.bind()
                val y = !FunctionMemo0 { 2 }
                val z = bind { FunctionMemo0 { 3 } }
                yields(x + y + z)
            }.ev().invoke() shouldBe 6
        }

        "Function0Comonad.cobinding should for comprehend over all values of multiple Function0" {
            FunctionMemo0.cobinding {
                val x = FunctionMemo0 { 1 }.extract()
                val y = !FunctionMemo0 { 2 }
                val z = extract { FunctionMemo0 { 3 } }
                yields(x + y + z)
            } shouldBe 6
        }

        "Function0Comonad.duplicate should create an instance of Function0<Function0<A>>" {
            FunctionMemo0.duplicate(FunctionMemo0 { 3 }).ev().invoke().ev().invoke() shouldBe
                    FunctionMemo0 { FunctionMemo0 { 3 } }.ev().invoke().ev().invoke()
        }
    }
}
