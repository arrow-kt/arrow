package arrow.core.computations

import arrow.core.Eval
import arrow.core.Tuple10
import arrow.core.composeErrors
import arrow.core.computations.ResultEffect.result
import arrow.core.flatMap
import arrow.core.handleErrorWith
import arrow.core.redeemWith
import arrow.core.test.result
import arrow.core.test.suspend
import arrow.core.test.throwable
import arrow.core.zip
import io.kotest.assertions.fail
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.result.shouldBeFailureOfType
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.suspendCancellableCoroutine

class ResultTest : StringSpec({
    "flatMap" {
      checkAll(Arb.result(Arb.int()), Arb.result(Arb.string())) { ints, strs ->
        val res = ints.flatMap { strs }
        if (ints.isFailure) res shouldBe ints
        else res shouldBe strs
      }
    }

    "handleErrorWith" {
      checkAll(Arb.result(Arb.int()), Arb.result(Arb.string())) { ints, strs ->
        val res = ints.handleErrorWith { strs }
        if (ints.isFailure) res shouldBe strs
        else res shouldBe ints
      }
    }

    "redeemWith" {
      checkAll(Arb.result(Arb.int()), Arb.result(Arb.string()), Arb.result(Arb.string())) { ints, failed, success ->
        val res = ints.redeemWith({ failed }, { success })
        if (ints.isFailure) res shouldBe failed
        else res shouldBe success
      }
    }

    "zip" {
      checkAll(
        Arb.result(Arb.int()),
        Arb.result(Arb.int()),
        Arb.result(Arb.int()),
        Arb.result(Arb.int()),
        Arb.result(Arb.int()),
        Arb.result(Arb.int()),
        Arb.result(Arb.int()),
        Arb.result(Arb.int()),
        Arb.result(Arb.int()),
        Arb.result(Arb.int()),
      ) { a, b, c, d, e, f, g, h, i, j ->
        val res = a.zip(b, c, d, e, f, g, h, i, j, ::Tuple10)
        val all = listOf(a, b, c, d, e, f, g, h, i, j)
        if (all.all { it.isSuccess }) res shouldBe success(
          Tuple10(
            a.getOrThrow(),
            b.getOrThrow(),
            c.getOrThrow(),
            d.getOrThrow(),
            e.getOrThrow(),
            f.getOrThrow(),
            g.getOrThrow(),
            h.getOrThrow(),
            i.getOrThrow(),
            j.getOrThrow()
          )
        ) else res shouldBe failure(
          composeErrors(
            a.exceptionOrNull(),
            b.exceptionOrNull(),
            c.exceptionOrNull(),
            d.exceptionOrNull(),
            e.exceptionOrNull(),
            f.exceptionOrNull(),
            g.exceptionOrNull(),
            h.exceptionOrNull(),
            i.exceptionOrNull(),
            j.exceptionOrNull()
          ).shouldNotBeNull()
        )
      }
    }

    "immediate values" {
      checkAll(Arb.result(Arb.int())) { res ->
        result {
          res.bind()
        } shouldBe res
      }
    }

    "suspended value" {
      checkAll(Arb.result(Arb.int())) { res ->
        result {
          res.suspend().bind()
        } shouldBe res
      }
    }

    "Rethrows immediate exceptions" {
      checkAll(Arb.throwable(), Arb.int(), Arb.int()) { e, a, b ->
        result<Int> {
          success(a).bind()
          success(b).suspend().bind()
          throw e
        } shouldBe failure(e)
      }
    }

    "result captures exception" {
      checkAll(Arb.throwable(), Arb.int(), Arb.int()) { e, a, b ->
        result<Int> {
          success(a).bind()
          success(b).suspend().bind()
          e.suspend()
        } shouldBe failure(e)
      }
    }

    "Can short-circuit from nested blocks" {
      checkAll(Arb.throwable()) { e ->
        result {
          val x = eval {
            failure<Int>(e).suspend().bind()
            5L
          }

          x.value()
        } shouldBe failure<Int>(e)
      }
    }

    "Can short-circuit suspended from nested blocks" {
      checkAll(Arb.throwable().map { failure<Int>(it) }) { res ->
        result {
          val x = eval {
            res.suspend().bind()
            5L
          }

          x.value()
        } shouldBe res
      }
    }

    "Can short-circuit after bind from nested blocks" {
      checkAll(Arb.throwable().map { failure<Int>(it) }) { res ->
        result {
          val x = eval {
            Eval.Now(1L).suspend().bind()
            res.suspend().bind()
            5L
          }

          1
        } shouldBe res
      }
    }

    "Short-circuiting cancels KotlinX Coroutines" {
      suspend fun completeOnCancellation(latch: CompletableDeferred<Unit>, cancelled: CompletableDeferred<Unit>): Unit =
        suspendCancellableCoroutine { cont ->
          cont.invokeOnCancellation {
            if (!cancelled.complete(Unit)) fail("cancelled latch was completed twice")
            else Unit
          }

          if (!latch.complete(Unit)) fail("latch was completed twice")
          else Unit
        }

      val scope = CoroutineScope(Dispatchers.Default)
      val latch = CompletableDeferred<Unit>()
      val cancelled = CompletableDeferred<Unit>()
      result {
        val deferreds: List<Deferred<Int>> = listOf(
          scope.async {
            completeOnCancellation(latch, cancelled)
            success(1).bind()
          },
          scope.async {
            latch.await()
            failure<Int>(RuntimeException()).bind()
          }
        )

        deferreds.awaitAll().sum()
      }.shouldBeFailureOfType<RuntimeException>()

      cancelled.await()
    }

    "Computation blocks run on parent context" {
      suspend fun currentContext(): CoroutineContext =
        kotlin.coroutines.coroutineContext

      val parentCtx = currentContext()
      result {
        currentContext() shouldBe parentCtx
      }
    }
})
