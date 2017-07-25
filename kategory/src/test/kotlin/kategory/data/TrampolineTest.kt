package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class TrampolineTest : UnitSpec() {
    init {
        "trampoline over 10000 should return false and not break the stack" {
            Trampoline.More { odd(10000) }.runT() shouldBe false
        }

        "trampoline over 10001 should return true and not break the stack" {
            Trampoline.More { odd(10001) }.runT() shouldBe true
        }
    }

    fun odd(n: Int): Trampoline<Boolean> {
        return when (n) {
            0 -> Trampoline.Done(false)
            else -> {
                println(n)
                Trampoline.More { even(n - 1) }
            }
        }
    }

    fun even(n: Int): Trampoline<Boolean> {
        return when (n) {
            0 -> Trampoline.Done(true)
            else -> {
                println(n)
                Trampoline.More { odd(n - 1) }
            }
        }
    }
}
