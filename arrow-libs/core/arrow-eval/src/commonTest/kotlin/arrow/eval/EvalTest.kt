package arrow.eval

import arrow.platform.stackSafeIteration
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal data class SideEffect(var counter: Int = 0) {
  fun increment() {
    counter++
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

class EvalTest {
  @Test
  fun mapWrappedValue() {
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

  @Test
  fun laterEvaluatesOnce() {
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

  @Test
  fun laterMemoizes() {
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

  @Test
  fun alwaysEvaluatesMany() {
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

  @Test
  fun alwaysMemoizes() {
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

  @Test
  fun deferShouldLazilyEvaluateOtherVals() {
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

  @Test
  fun deferShouldMemoizeLater() {
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

  @Test
  fun deferShouldMemoizeNow() {
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

  @Test
  fun flatMapShouldNotBlowTheStack() {
    val limit = stackSafeIteration()
    val sideEffect = SideEffect()
    val flatMapped = Eval.now(0).flatMap(recur(limit, sideEffect))
    sideEffect.counter shouldBe 0
    flatMapped.value() shouldBe -1
    sideEffect.counter shouldBe limit + 1
  }

  @Test
  fun replicateShouldEvaluateInOrder() {
    val sideEffect = SideEffect()
    val replicated = Eval.always { sideEffect.increment(); sideEffect.counter }.replicate(3)
    sideEffect.counter shouldBe 0
    replicated.value() shouldBe listOf(1, 2, 3)
    replicated.value() shouldBe listOf(4, 5, 6)
  }

  @Test
  fun replicateShouldFoldRight() {
    val sideEffect = SideEffect()
    val replicated = Eval.always { sideEffect.increment(); sideEffect.counter }
      .replicate(3, { "" }) { a, acc -> acc + a }
    replicated.value() shouldBe "321"
  }

  @Test
  fun replicateShouldNotBlowTheStack() {
    val limit = stackSafeIteration()
    Eval.now(1).replicate(limit).value().size shouldBe limit
  }
}
