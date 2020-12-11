package generic

import arrow.continuations.generic.ShortCircuit
import arrow.core.Left
import arrow.core.Right
import arrow.core.computations.either
import arrow.fx.coroutines.ComputationPool
import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.Promise
import arrow.fx.coroutines.bracketCase
import io.kotlintest.fail
import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.intercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.startCoroutine

class SuspendingComputationTest : StringSpec({

  "immediate values" {
    either<String, Int> {
      Right(1).invoke()
    } shouldBe Right(1)
  }

  "suspended value" {
    either<String, Int> {
      Right(1).suspend().invoke()
    } shouldBe Right(1)
  }

  "immediate short-circuit" {
    either<String, Int> {
      Left("hello").invoke()
    } shouldBe Left("hello")
  }

  "suspended short-circuit" {
    either<String, Int> {
      Left("hello").suspend().invoke()
    } shouldBe Left("hello")
  }

  "Rethrows immediate exceptions" {
    val e = RuntimeException("test")
    shouldThrow<RuntimeException> {
      either<String, Int> {
        Right(1).invoke()
        Right(1).suspend().invoke()
        throw e
      }
    } shouldBe e
  }

  "Rethrows suspended exceptions" {
    val e = RuntimeException("test")
    shouldThrow<RuntimeException> {
      either<String, Int> {
        Right(1).invoke()
        Right(1).suspend().invoke()
        e.suspend()
      }
    } shouldBe e
  }

  "Can short-circuit immediately from nested blocks" {
    either<String, Int> {
      val x = maybeEff {
        Left("test").invoke()
        5L
      }

      println(x)
      1
    } shouldBe Left("test")
  }

  "Can short-circuit suspended from nested blocks" {
    either<String, Int> {
      val x = maybeEff {
        Left("test").suspend().invoke()
        5L
      }

      println(x)
      1
    } shouldBe Left("test")
  }

  "Can short-circuit immediately after suspending from nested blocks" {
    either<String, Int> {
      val x = maybeEff {
        Just(1L).suspend().invoke()
        Left("test").suspend().invoke()
        5L
      }

      println(x)
      1
    } shouldBe Left("test")
  }

  "Can short-circuit suspended after suspending from nested blocks" {
    either<String, Int> {
      val x = maybeEff {
        Just(1L).suspend().invoke()
        Left("test").suspend().invoke()
        5L
      }

      println(x)
      1
    } shouldBe Left("test")
  }

  "Short-circuiting cancels Arrow Fx Coroutines" {
    val exit = Promise<ExitCase>()

    either<String, Int> {
      val i: Int = bracketCase(
        acquire = { Unit },
        use = {
          Left("hello").invoke()
        },
        release = { _, exitCase -> exit.complete(exitCase) }
      )

      5
    } shouldBe Left("hello")

    exit.get().shouldBeInstanceOf<ExitCase.Failure> {
      it.failure.shouldBeInstanceOf<ShortCircuit>()
    }
  }

  "Short-circuiting cancels KotlinX Coroutines" {
    val scope = CoroutineScope(Dispatchers.Default)
    val latch = CompletableDeferred<Unit>()
    val cancelled = CompletableDeferred<Unit>()

    either<String, Int> {
      val deferreds: List<Deferred<Int>> = listOf(
        scope.async {
          completeOnCancellation(latch, cancelled)
          1
        },
        scope.async<Int> {
          latch.await()
          Left("hello").invoke()
        }
      )

      deferreds.awaitAll().sum()
    } shouldBe Left("hello")

    cancelled.await()
  }
})

suspend fun completeOnCancellation(latch: CompletableDeferred<Unit>, cancelled: CompletableDeferred<Unit>): Unit =
  suspendCancellableCoroutine { cont ->
    cont.invokeOnCancellation {
      if (!cancelled.complete(Unit)) fail("cancelled latch was completed twice")
      else Unit
    }

    if (!latch.complete(Unit)) fail("latch was completed twice")
    else Unit
  }

internal suspend fun Throwable.suspend(): Nothing =
  suspendCoroutineUninterceptedOrReturn { cont ->
    suspend { throw this }.startCoroutine(Continuation(ComputationPool) {
      cont.intercepted().resumeWith(it)
    })

    COROUTINE_SUSPENDED
  }

internal suspend fun <A> A.suspend(): A =
  suspendCoroutineUninterceptedOrReturn { cont ->
    suspend { this }.startCoroutine(Continuation(ComputationPool) {
      cont.intercepted().resumeWith(it)
    })

    COROUTINE_SUSPENDED
  }
