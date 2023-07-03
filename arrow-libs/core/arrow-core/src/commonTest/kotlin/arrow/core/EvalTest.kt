package arrow.core

import arrow.core.test.stackSafeIteration
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

internal data class SideEffect(var counter: Int = 0) {
  fun increment() {
    counter++
  }
}

private fun recur(limit: Int, sideEffect: SideEffect): (Int) -> Eval2<Int> {
  return { num ->
    if (num <= limit) {
      sideEffect.increment()
      Eval2Utils.defer {
        recur(limit, sideEffect).invoke(num + 1)
      }
    } else {
      Eval2Utils.now(-1)
    }
  }
}

class EvalTest : StringSpec({

    "should map wrapped value" {
      val sideEffect = SideEffect()
      val mapped = Eval2Utils.now(0)
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
      val mapped = Eval2Utils.later { sideEffect.increment(); sideEffect.counter }
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
      val mapped = Eval2Utils.later { sideEffect.increment(); sideEffect.counter }.memoize()
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
      val mapped = Eval2Utils.always { sideEffect.increment(); sideEffect.counter }
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
      val mapped = Eval2Utils.always { sideEffect.increment(); sideEffect.counter }.memoize()
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
      val mapped = Eval2Utils.defer { sideEffect.increment(); Eval2Utils.later { sideEffect.increment(); sideEffect.counter } }
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
      val mapped = Eval2Utils.defer { sideEffect.increment(); Eval2Utils.later { sideEffect.increment(); sideEffect.counter } }.memoize()
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
      val mapped = Eval2Utils.defer { sideEffect.increment(); Eval2Utils.now(sideEffect.counter) }.memoize()
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
      val flatMapped = Eval2Utils.now(0).flatMap(recur(limit, sideEffect))
      sideEffect.counter shouldBe 0
      flatMapped.value() shouldBe -1
      sideEffect.counter shouldBe limit + 1
    }

})
