package arrow.eval

import arrow.platform.stackSafeIteration
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

internal data class SuspendSideEffect(var counter: Int = 0) {
  suspend fun increment() {
    counter++
  }
}

private fun recur(limit: Int, sideEffect: SuspendSideEffect): suspend (Int) -> SuspendEval<Int> {
  return { num ->
    if (num <= limit) {
      sideEffect.increment()
      SuspendEval.defer {
        recur(limit, sideEffect).invoke(num + 1)
      }
    } else {
      Eval.now(-1)
    }
  }
}

class SuspendEvalTest {
  @Test
  fun mapWrappedValue() = runTest {
    val sideEffect = SuspendSideEffect()
    val mapped = Eval.now(0)
      .mapSuspend { sideEffect.increment(); it + 1 }
    sideEffect.counter shouldBe 0
    mapped.run() shouldBe 1
    sideEffect.counter shouldBe 1
    mapped.run() shouldBe 1
    sideEffect.counter shouldBe 2
    mapped.run() shouldBe 1
    sideEffect.counter shouldBe 3
  }

  @Test
  fun laterEvaluatesOnce() = runTest {
    val sideEffect = SuspendSideEffect()
    val mapped = SuspendEval.later { sideEffect.increment(); sideEffect.counter }
    sideEffect.counter shouldBe 0
    mapped.run() shouldBe 1
    sideEffect.counter shouldBe 1
    mapped.run() shouldBe 1
    sideEffect.counter shouldBe 1
    mapped.run() shouldBe 1
    sideEffect.counter shouldBe 1
  }

  @Test
  fun laterMemoizes() = runTest {
    val sideEffect = SuspendSideEffect()
    val mapped = SuspendEval.later { sideEffect.increment(); sideEffect.counter }.memoize()
    sideEffect.counter shouldBe 0
    mapped.run() shouldBe 1
    sideEffect.counter shouldBe 1
    mapped.run() shouldBe 1
    sideEffect.counter shouldBe 1
    mapped.run() shouldBe 1
    sideEffect.counter shouldBe 1
  }

  @Test
  fun alwaysEvaluatesMany() = runTest {
    val sideEffect = SuspendSideEffect()
    val mapped = SuspendEval.always { sideEffect.increment(); sideEffect.counter }
    sideEffect.counter shouldBe 0
    mapped.run() shouldBe 1
    sideEffect.counter shouldBe 1
    mapped.run() shouldBe 2
    sideEffect.counter shouldBe 2
    mapped.run() shouldBe 3
    sideEffect.counter shouldBe 3
  }

  @Test
  fun alwaysMemoizes() = runTest {
    val sideEffect = SuspendSideEffect()
    val mapped = SuspendEval.always { sideEffect.increment(); sideEffect.counter }.memoize()
    sideEffect.counter shouldBe 0
    mapped.run() shouldBe 1
    sideEffect.counter shouldBe 1
    mapped.run() shouldBe 1
    sideEffect.counter shouldBe 1
    mapped.run() shouldBe 1
    sideEffect.counter shouldBe 1
  }

  @Test
  fun deferShouldLazilyEvaluateOtherVals() = runTest {
    val sideEffect = SuspendSideEffect()
    val mapped = SuspendEval.defer {
      sideEffect.increment()
      SuspendEval.later { sideEffect.increment(); sideEffect.counter }
    }
    sideEffect.counter shouldBe 0
    mapped.run() shouldBe 2
    sideEffect.counter shouldBe 2
    mapped.run() shouldBe 4
    sideEffect.counter shouldBe 4
    mapped.run() shouldBe 6
    sideEffect.counter shouldBe 6
  }

  @Test
  fun deferShouldMemoizeLater() = runTest {
    val sideEffect = SuspendSideEffect()
    val mapped = SuspendEval.defer {
      sideEffect.increment()
      SuspendEval.later { sideEffect.increment(); sideEffect.counter }
    }.memoize()
    sideEffect.counter shouldBe 0
    mapped.run() shouldBe 2
    sideEffect.counter shouldBe 2
    mapped.run() shouldBe 2
    sideEffect.counter shouldBe 2
    mapped.run() shouldBe 2
    sideEffect.counter shouldBe 2
  }

  @Test
  fun deferShouldMemoizeNow() = runTest {
    val sideEffect = SuspendSideEffect()
    val mapped = SuspendEval.defer {
      sideEffect.increment()
      Eval.now(sideEffect.counter)
    }.memoize()
    sideEffect.counter shouldBe 0
    mapped.run() shouldBe 1
    sideEffect.counter shouldBe 1
    mapped.run() shouldBe 1
    sideEffect.counter shouldBe 1
    mapped.run() shouldBe 1
    sideEffect.counter shouldBe 1
  }

  @Test
  fun flatMapShouldNotBlowTheStack() = runTest {
    val limit = stackSafeIteration()
    val sideEffect = SuspendSideEffect()
    val flatMapped = Eval.now(0).flatMapSuspend(recur(limit, sideEffect))
    sideEffect.counter shouldBe 0
    flatMapped.run() shouldBe -1
    sideEffect.counter shouldBe limit + 1
  }

  @Test
  fun atMostOnceSemantics() = runTest {
    var executionCount = 0

    val name: SuspendEval<String> =
      SuspendEval.atMostOnce {
        executionCount++
        delay(1.seconds)
        "John"
      }

    awaitAll(*Array(20) { async { name.run() } })
    executionCount shouldBe 1
  }
}
