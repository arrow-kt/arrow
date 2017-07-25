package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class TrampolineTest : UnitSpec() {

    val idInterpreter: FunctionK<Function0.F, Id.F> = object : FunctionK<Function0.F, Id.F> {
        override fun <A> invoke(fa: HK<Function0.F, A>): Id<A> {
            val op = fa.ev()
            return Id(op())
        }
    }

    init {
        "trampoline over 10000 should return false and not break the stack" {
            odd(10000).foldMap(idInterpreter, Id).value() shouldBe false
        }

        "trampoline over 10001 should return true and not break the stack" {
            odd(10001).foldMap(idInterpreter, Id).value() shouldBe true
        }
    }

    fun odd(n: Int): TrampolineF<Boolean> {
        return when (n) {
            0 -> Trampoline.done(false)
            else -> {
                Trampoline.defer { even(n - 1) }
            }
        }
    }

    fun even(n: Int): TrampolineF<Boolean> {
        return when (n) {
            0 -> Trampoline.done(true)
            else -> {
                Trampoline.defer { odd(n - 1) }
            }
        }
    }
}
