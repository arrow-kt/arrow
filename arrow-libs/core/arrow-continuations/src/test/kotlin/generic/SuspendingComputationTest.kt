package generic

import arrow.core.Left
import arrow.core.Right
import arrow.core.computations.either
import io.kotlintest.fail
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
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.intercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.startCoroutine

class SuspendingComputationTest : StringSpec({

  "immediate values" {
    either<String, Int> {
      Right(1).bind()
    } shouldBe Right(1)
  }

  "suspended value" {
    either<String, Int> {
      Right(1).suspend().bind()
    } shouldBe Right(1)
  }

  "immediate short-circuit" {
    either<String, Int> {
      Left("hello").bind()
    } shouldBe Left("hello")
  }

  "suspended short-circuit" {
    either<String, Int> {
      Left("hello").suspend().bind()
    } shouldBe Left("hello")
  }

  "Rethrows immediate exceptions" {
    val e = RuntimeException("test")
    shouldThrow<RuntimeException> {
      either<String, Int> {
        Right(1).bind()
        Right(1).suspend().bind()
        throw e
      }
    } shouldBe e
  }

  "Rethrows suspended exceptions" {
    val e = RuntimeException("test")
    shouldThrow<RuntimeException> {
      either<String, Int> {
        Right(1).bind()
        Right(1).suspend().bind()
        e.suspend()
      }
    } shouldBe e
  }

  "Can short-circuit immediately from nested blocks" {
    either<String, Int> {
      val x = maybeEff {
        Left("test").bind()
        5L
      }

      println(x)
      1
    } shouldBe Left("test")
  }

  "Can short-circuit suspended from nested blocks" {
    either<String, Int> {
      val x = maybeEff {
        Left("test").suspend().bind()
        5L
      }

      println(x)
      1
    } shouldBe Left("test")
  }

  "Can short-circuit immediately after suspending from nested blocks" {
    either<String, Int> {
      val x = maybeEff {
        Just(1L).suspend().bind()
        Left("test").suspend().bind()
        5L
      }

      println(x)
      1
    } shouldBe Left("test")
  }

  "Can short-circuit suspended after suspending from nested blocks" {
    either<String, Int> {
      val x = maybeEff {
        Just(1L).suspend().bind()
        Left("test").suspend().bind()
        5L
      }

      println(x)
      1
    } shouldBe Left("test")
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
          Left("hello").bind()
        }
      )

      deferreds.awaitAll().sum()
    } shouldBe Left("hello")

    cancelled.await()
  }

  "Computation blocks run on parent context" {
    val parentCtx = currentContext()
    either<String, Unit> {
      currentContext() shouldBe parentCtx
    }
  }
})

suspend fun currentContext(): CoroutineContext =
  kotlin.coroutines.coroutineContext

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
    suspend { throw this }.startCoroutine(
      Continuation(Dispatchers.Default) {
        cont.intercepted().resumeWith(it)
      }
    )

    COROUTINE_SUSPENDED
  }

internal suspend fun <A> A.suspend(): A =
  suspendCoroutineUninterceptedOrReturn { cont ->
    suspend { this }.startCoroutine(
      Continuation(Dispatchers.Default) {
        cont.intercepted().resumeWith(it)
      }
    )

    COROUTINE_SUSPENDED
  }
