package arrow.core.raise

import arrow.core.identity
import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.guaranteeCase
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.kotest.matchers.types.shouldBeTypeOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout

@Suppress("DeferredResultUnused")
class StructuredConcurrencySpec : StringSpec({
  "async - suspendCancellableCoroutine.invokeOnCancellation is called with Raised Continuation" {
    val started = CompletableDeferred<Unit>()
    val cancelled = CompletableDeferred<Throwable?>()

    effect {
      coroutineScope {
        val never = async {
          suspendCancellableCoroutine<Nothing> { cont ->
            cont.invokeOnCancellation { cause ->
              require(cancelled.complete(cause)) { "cancelled latch was completed twice" }
            }
            require(started.complete(Unit))
          }
        }
        async<Int> {
          started.await()
          raise("hello")
        }.await()
        never.await()
      }
    }.fold(::identity) { fail("Should never be here") } shouldBe "hello"

    withTimeout(2.seconds) {
      cancelled.await().shouldNotBeNull().message shouldBe RaiseCancellationExceptionCaptured
    }
  }

  "Computation blocks run on parent context" {
    val parentCtx = currentCoroutineContext()
    effect<Nothing, Unit> { currentCoroutineContext() shouldBe parentCtx }
      .fold({ fail("Should never be here") }, ::identity)
  }

  "Concurrent raise - async await" {
    checkAll(Arb.int(), Arb.int()) { a, b ->
      effect {
        coroutineScope {
          val fa = async<String> { raise(a) }
          val fb = async<String> { raise(b) }
          fa.await() + fb.await()
        }
      }
        .fold(::identity, ::identity) shouldBeIn listOf(a, b)
    }
  }

  "Concurrent raise - async await exit results" {
    checkAll(Arb.int()) { a ->
      val scopeExit = CompletableDeferred<ExitCase>()
      val fbExit = CompletableDeferred<ExitCase>()
      val startLatches = (0..11).map { CompletableDeferred<Unit>() }
      val nestedExits = (0..10).map { CompletableDeferred<ExitCase>() }

      fun CoroutineScope.asyncTask(
        start: CompletableDeferred<Unit>,
        exit: CompletableDeferred<ExitCase>
      ): Deferred<Unit> = async {
        guaranteeCase({
          start.complete(Unit)
          awaitCancellation()
        }) { case -> require(exit.complete(case)) }
      }

      effect<Int, String> {
        guaranteeCase({
          coroutineScope {
            val fa =
              async<Unit> {
                startLatches.drop(1).zip(nestedExits) { start, promise ->
                  asyncTask(start, promise)
                }
                startLatches.awaitAll()
                raise(a)
              }
            val fb = asyncTask(startLatches.first(), fbExit)
            fa.await()
            fb.await()
          }
        }) { case -> require(scopeExit.complete(case)) }
        fail("Should never come here")
      }
        .fold(::identity, ::identity) shouldBe a
      withTimeout(2.seconds) {
        scopeExit.await().shouldBeTypeOf<ExitCase.Cancelled>()
        fbExit.await().shouldBeTypeOf<ExitCase.Cancelled>()
        nestedExits.awaitAll().forEach { it.shouldBeTypeOf<ExitCase.Cancelled>() }
      }
    }
  }

  "Concurrent raise - async" {
    checkAll(Arb.int(), Arb.int()) { a, b ->
      effect {
        coroutineScope {
          async { raise(a) }
          async { raise(b) }
          "I will be overwritten by raise - coroutineScope waits until all async are finished"
        }
      }
        .fold({ fail("Async is never awaited, and thus ignored.") }, ::identity) shouldBe
        "I will be overwritten by raise - coroutineScope waits until all async are finished"
    }
  }

  "Concurrent raise - async exit results" {
    checkAll(Arb.int(), Arb.string()) { a, str ->
      val exitScope = CompletableDeferred<ExitCase>()
      val startLatches = (0..10).map { CompletableDeferred<Unit>() }
      val nestedExits = (0..10).map { CompletableDeferred<ExitCase>() }

      fun CoroutineScope.asyncTask(
        start: CompletableDeferred<Unit>,
        exit: CompletableDeferred<ExitCase>
      ): Deferred<Unit> = async {
        guaranteeCase({
          start.complete(Unit)
          awaitCancellation()
        }) { case -> require(exit.complete(case)) }
      }

      effect {
        guaranteeCase({
          coroutineScope {
            async<Unit> {
              startLatches.zip(nestedExits) { start, promise -> asyncTask(start, promise) }
              startLatches.awaitAll()
              raise(a)
            }
            str
          }
        }) { case -> require(exitScope.complete(case)) }
      }
        .fold(::identity, ::identity) shouldBe str

      withTimeout(2.seconds) {
        nestedExits.awaitAll().forEach { it.shouldBeTypeOf<ExitCase.Cancelled>() }
      }
    }
  }

  "Concurrent raise - launch" {
    checkAll(Arb.int(), Arb.int()) { a, b ->
      effect {
        coroutineScope {
          launch { raise(a) }
          launch { raise(b) }
          "raise does not escape `launch`"
        }
      }
        .fold(::identity, ::identity) shouldBe "raise does not escape `launch`"
    }
  }

  "Concurrent raise - launch exit results" {
    checkAll(Arb.int(), Arb.string()) { a, str ->
      val scopeExit = CompletableDeferred<ExitCase>()
      val startLatches = (0..10).map { CompletableDeferred<Unit>() }
      val nestedExits = (0..10).map { CompletableDeferred<ExitCase>() }

      fun CoroutineScope.launchTask(
        start: CompletableDeferred<Unit>,
        exit: CompletableDeferred<ExitCase>
      ): Job = launch {
        guaranteeCase({
          start.complete(Unit)
          awaitCancellation()
        }) { case -> require(exit.complete(case)) }
      }

      effect {
        guaranteeCase({
          coroutineScope {
            launch {
              startLatches.zip(nestedExits) { start, promise -> launchTask(start, promise) }
              startLatches.awaitAll()
              raise(a)
            }
            str
          }
        }) { case -> require(scopeExit.complete(case)) }
      }
        .fold(::identity, ::identity) shouldBe str
      withTimeout(2.seconds) {
        scopeExit.await().shouldBeTypeOf<ExitCase.Completed>()
        nestedExits.awaitAll().forEach { it.shouldBeTypeOf<ExitCase.Cancelled>() }
      }
    }
  }

  // `raise` escapes `cont` block, and gets rethrown inside `coroutineScope`.
  // Effectively awaiting/executing DSL code, outside of the DSL...
  "async funky scenario #1 - Extract `raise` from `effect` through `async`" {
    checkAll(Arb.int(), Arb.int()) { a, b ->
      shouldThrow<IllegalStateException> {
        coroutineScope {
          val shiftedAsync =
            effect<Int, Deferred<String>> {
              async<Int> { raise(a) }
              async { raise(b) }
            }
              .fold({ fail("shift was never awaited, so it never took effect") }, ::identity)
          shiftedAsync.await()
        }
      }.message shouldStartWith "raise or bind was called outside of its DSL scope"
    }
  }
})
