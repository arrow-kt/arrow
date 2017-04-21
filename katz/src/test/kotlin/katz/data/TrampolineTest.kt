package katz

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.forAll
import katz.Either.Left
import katz.Either.Right
import katz.data.Trampoline
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class TrampolineTest : UnitSpec() {
    init {
        "trampoline over 10 should return false" {
            Trampoline.More { odd(10) }.runT() shouldBe false
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
