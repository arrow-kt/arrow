package arrow.continuations

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.Either.Right
import arrow.core.Either.Left
import arrow.core.Eval
import arrow.core.computations.ensureNotNull
import arrow.core.computations.eval
import arrow.core.left
import arrow.core.right
import io.kotest.assertions.fail
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
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
    Either.catch {
      either<String, Int> {
        Right(1).bind()
        Right(1).suspend().bind()
        throw e
      }
    } shouldBe Left(e)
  }

  "Rethrows suspended exceptions" {
    val e = RuntimeException("test")
    Either.catch {
      either<String, Int> {
        Right(1).bind()
        Right(1).suspend().bind()
        e.suspend()
      }
    } shouldBe Either.Left(e)
  }

  "Can short-circuit immediately from nested blocks" {
    either<String, Int> {
      val x = eval {
        Left("test").bind()
        5L
      }

      println(x)
      1
    } shouldBe Left("test")
  }

  "Can short-circuit suspended from nested blocks" {
    either<String, Int> {
      val x = eval {
        Left("test").suspend().bind()
        5L
      }

      println(x)
      1
    } shouldBe Left("test")
  }

  "Can short-circuit immediately after suspending from nested blocks" {
    either<String, Int> {
      val x = eval {
        Eval.Now(1L).suspend().bind()
        Left("test").suspend().bind()
        5L
      }

      println(x)
      1
    } shouldBe Left("test")
  }

  "Can short-circuit suspended after suspending from nested blocks" {
    either<String, Int> {
      val x = eval {
        Eval.Now(1L).suspend().bind()
        Left("test").suspend().bind()
        5L
      }

      println(x)
      1
    } shouldBe Left("test")
  }

  "ensure null in either computation" {
    checkAll(Arb.boolean(), Arb.int(), Arb.string()) { predicate, rValue, lValue ->
      either<String, Int> {
        ensure(predicate) { lValue }
        rValue
      } shouldBe if (predicate) rValue.right() else lValue.left()
    }
  }

  "ensureNotNull in either computation" {
    fun square(i: Int): Int = i * i

    checkAll(Arb.int().orNull(), Arb.string()) { i: Int?, lValue: String ->
      val res = either<String, Int> {
        val ii = i
        ensureNotNull(ii) { lValue }
        square(ii) // Smart-cast by contract
      }
      val expected = i?.let(::square)?.right() ?: lValue.left()
      res shouldBe expected
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
