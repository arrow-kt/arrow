package arrow.fx.coroutines

import arrow.atomic.Atomic
import arrow.atomic.update
import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.continuations.either
import arrow.core.left
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull

class ParMapTest : StringSpec({
  "parMap is stack-safe" {
    val count = 20_000
    val ref = Atomic(0)
    (0 until count).parMap { _: Int ->
      ref.update { it + 1 }
    }
    ref.get() shouldBe count
  }

  "parMap runs in parallel" {
    val promiseA = CompletableDeferred<Unit>()
    val promiseB = CompletableDeferred<Unit>()
    val promiseC = CompletableDeferred<Unit>()

    listOf(
      suspend {
        promiseA.await()
        promiseC.complete(Unit)
      },
      suspend {
        promiseB.await()
        promiseA.complete(Unit)
      },
      suspend {
        promiseB.complete(Unit)
        promiseC.await()
      }
    ).parMap { it.invoke() }
  }

  "parTraverse results in the correct error" {
    checkAll(
      Arb.int(min = 10, max = 20),
      Arb.int(min = 1, max = 9),
      Arb.throwable()
    ) { n, killOn, e ->
      Either.catch {
        (0 until n).parMap { i ->
          if (i == killOn) throw e else Unit
        }
      } should leftException(e)
    }
  }

  "parMap(concurrency = 1) only runs one task at a time" {
    val promiseA = CompletableDeferred<Unit>()

    withTimeoutOrNull(100.milliseconds) {
      listOf(
        suspend { promiseA.await() },
        suspend { promiseA.complete(Unit) }
      ).parMap(concurrency = 1) { it.invoke() }
    } shouldBe null
  }

  "parMap with either results in the correct left" {
    checkAll(
      Arb.int(min = 10, max = 20),
      Arb.int(min = 1, max = 9),
      Arb.string()
    ) { n, killOn, e ->
      either {
        (0 until n).parMap { i ->
          if (i == killOn) raise(e) else Unit
        }
      } shouldBe e.left()
    }
  }

  "parMapOrAccumulate is stack-safe" {
    val count = 20_000
    val ref = Atomic(0)
    (0 until count).parMapOrAccumulate(combine = emptyError) { _: Int ->
      ref.update { it + 1 }
    }
    ref.get() shouldBe count
  }

  "parMapOrAccumulate runs in parallel" {
    val promiseA = CompletableDeferred<Unit>()
    val promiseB = CompletableDeferred<Unit>()
    val promiseC = CompletableDeferred<Unit>()

    listOf(
      suspend {
        promiseA.await()
        promiseC.complete(Unit)
      },
      suspend {
        promiseB.await()
        promiseA.complete(Unit)
      },
      suspend {
        promiseB.complete(Unit)
        promiseC.await()
      }
    ).parMapOrAccumulate(combine = emptyError) { it.invoke() }
  }

  "parMapOrAccumulate results in the correct error" {
    checkAll(
      Arb.int(min = 10, max = 20),
      Arb.int(min = 1, max = 9),
      Arb.throwable()
    ) { n, killOn, e ->
      Either.catch {
        (0 until n).parMapOrAccumulate(combine = emptyError) { i ->
          if (i == killOn) throw e else Unit
        }
      } should leftException(e)
    }
  }

  "parMapOrAccumulate(concurrency = 1) only runs one task at a time" {
    val promiseA = CompletableDeferred<Unit>()

    withTimeoutOrNull(100.milliseconds) {
      listOf(
        suspend { promiseA.await() },
        suspend { promiseA.complete(Unit) }
      ).parMapOrAccumulate(concurrency = 1, combine = emptyError) { it.invoke() }
    } shouldBe null
  }

  "parMapOrAccumulate accumulates shifts" {
    checkAll(Arb.string()) { e ->
      (0 until 100).parMapOrAccumulate { _ ->
        raise(e)
      } shouldBe NonEmptyList(e, (1 until 100).map { e }).left()
    }
  }
})

private val emptyError: (Nothing, Nothing) -> Nothing =
  { _, _ -> throw AssertionError("Should not be called") }
