package arrow.core

import arrow.core.computations.EvalEffect
import arrow.core.computations.RestrictedEvalEffect
import arrow.core.computations.eval
import arrow.core.test.UnitSpec
import arrow.core.test.concurrency.SideEffect
import arrow.core.test.laws.FxLaws
import arrow.core.test.stackSafeIteration
import io.kotest.property.Arb
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map

class EvalTest : UnitSpec() {

  init {

    testLaws(
      FxLaws.suspended<EvalEffect<*>, Eval<Int>, Int>(Arb.int().map(Eval.Companion::now), Arb.int().map(Eval.Companion::now), Eval<Int>::equals, eval::invoke) {
        it.bind()
      },
      FxLaws.eager<RestrictedEvalEffect<*>, Eval<Int>, Int>(Arb.int().map(Eval.Companion::now), Arb.int().map(Eval.Companion::now), Eval<Int>::equals, eval::eager) {
        it.bind()
      }
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
      val limit = stackSafeIteration()
      val sideEffect = SideEffect()
      val flatMapped = Eval.now(0).flatMap(recur(limit, sideEffect))
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
        Eval.now(-1)
      }
    }
  }
}
