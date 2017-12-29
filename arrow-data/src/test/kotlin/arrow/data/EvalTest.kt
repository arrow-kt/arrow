package arrow

import arrow.core.Eval
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import arrow.core.Eval.Now
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class EvalTest : UnitSpec() {
    val EQ: Eq<HK<EvalHK, Int>>  = Eq { a, b ->
        a.ev().value() == b.ev().value()
    }

    init {

        testLaws(
            MonadLaws.laws(Eval.monad(), EQ),
            ComonadLaws.laws(Eval.comonad(), ::Now, EQ)
        )

        "should map wrapped value" {
            val sideEffect = SideEffect()
            val mapped = Eval.now(0)
                    .map { sideEffect.increment(); it + 1 }
            sideEffect.counter shouldBe 0
            mapped.value() shouldBe 1
            sideEffect.counter shouldBe 1
            mapped.value() shouldBe 1
            sideEffect.counter shouldBe 2
            mapped.value() shouldBe 1
            sideEffect.counter shouldBe 3
        }

        "later should lazily evaluate values once" {
            val sideEffect = SideEffect()
            val mapped = Eval.later { sideEffect.increment(); sideEffect.counter }
            sideEffect.counter shouldBe 0
            mapped.value() shouldBe 1
            sideEffect.counter shouldBe 1
            mapped.value() shouldBe 1
            sideEffect.counter shouldBe 1
            mapped.value() shouldBe 1
            sideEffect.counter shouldBe 1
        }

        "later should memoize values" {
            val sideEffect = SideEffect()
            val mapped = Eval.later { sideEffect.increment(); sideEffect.counter }.memoize()
            sideEffect.counter shouldBe 0
            mapped.value() shouldBe 1
            sideEffect.counter shouldBe 1
            mapped.value() shouldBe 1
            sideEffect.counter shouldBe 1
            mapped.value() shouldBe 1
            sideEffect.counter shouldBe 1
        }

        "always should lazily evaluate values repeatedly" {
            val sideEffect = SideEffect()
            val mapped = Eval.always { sideEffect.increment(); sideEffect.counter }
            sideEffect.counter shouldBe 0
            mapped.value() shouldBe 1
            sideEffect.counter shouldBe 1
            mapped.value() shouldBe 2
            sideEffect.counter shouldBe 2
            mapped.value() shouldBe 3
            sideEffect.counter shouldBe 3
        }

        "always should memoize values" {
            val sideEffect = SideEffect()
            val mapped = Eval.always { sideEffect.increment(); sideEffect.counter }.memoize()
            sideEffect.counter shouldBe 0
            mapped.value() shouldBe 1
            sideEffect.counter shouldBe 1
            mapped.value() shouldBe 1
            sideEffect.counter shouldBe 1
            mapped.value() shouldBe 1
            sideEffect.counter shouldBe 1
        }

        "defer should lazily evaluate other Evals" {
            val sideEffect = SideEffect()
            val mapped = Eval.defer { sideEffect.increment(); Eval.later { sideEffect.increment(); sideEffect.counter } }
            sideEffect.counter shouldBe 0
            mapped.value() shouldBe 2
            sideEffect.counter shouldBe 2
            mapped.value() shouldBe 4
            sideEffect.counter shouldBe 4
            mapped.value() shouldBe 6
            sideEffect.counter shouldBe 6
        }

        "defer should memoize Eval#later" {
            val sideEffect = SideEffect()
            val mapped = Eval.defer { sideEffect.increment(); Eval.later { sideEffect.increment(); sideEffect.counter } }.memoize()
            sideEffect.counter shouldBe 0
            mapped.value() shouldBe 2
            sideEffect.counter shouldBe 2
            mapped.value() shouldBe 2
            sideEffect.counter shouldBe 2
            mapped.value() shouldBe 2
            sideEffect.counter shouldBe 2
        }

        "defer should memoize Eval#now" {
            val sideEffect = SideEffect()
            val mapped = Eval.defer { sideEffect.increment(); Eval.now(sideEffect.counter) }.memoize()
            sideEffect.counter shouldBe 0
            mapped.value() shouldBe 1
            sideEffect.counter shouldBe 1
            mapped.value() shouldBe 1
            sideEffect.counter shouldBe 1
            mapped.value() shouldBe 1
            sideEffect.counter shouldBe 1
        }

        "flatMap should complete without blowing up the stack" {
            val limit = 10000
            val sideEffect = SideEffect()
            val flatMapped = Eval.pure(0).flatMap(recur(limit, sideEffect))
            sideEffect.counter shouldBe 0
            flatMapped.value() shouldBe -1
            sideEffect.counter shouldBe limit + 1
        }
    }

    private fun recur(limit: Int, sideEffect: SideEffect): (Int) -> Eval<Int> {
        return { num ->
            if (num <= limit) {
                sideEffect.increment()
                Eval.defer {
                    recur(limit, sideEffect).invoke(num + 1)
                }
            } else {
                Eval.pure(-1)
            }
        }
    }
}
