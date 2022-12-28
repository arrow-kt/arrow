package arrow.core.continuations

import arrow.core.identity
import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.guaranteeCase
import io.kotest.assertions.fail
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout

class StructuredConcurrencySpec :
  StringSpec({
    "async - suspendCancellableCoroutine.invokeOnCancellation is called with Shifted Continuation" {
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
                shift("hello")
              }
              .await()
            never.await()
          }
        }
        .runCont() shouldBe "hello"

      withTimeout(2.seconds) {
        cancelled.await().shouldNotBeNull().message shouldBe "Shifted Continuation"
      }
    }

    "Computation blocks run on parent context" {
      val parentCtx = currentContext()
      effect<Nothing, Unit> { currentContext() shouldBe parentCtx }.runCont()
    }

    "Concurrent shift - async await" {
      checkAll(Arb.int(), Arb.int()) { a, b ->
        effect {
            coroutineScope {
              val fa = async<String> { shift(a) }
              val fb = async<String> { shift(b) }
              fa.await() + fb.await()
            }
          }
          .runCont() shouldBeIn listOf(a, b)
      }
    }

    "Concurrent shift - async await exit results" {
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
                    shift(a)
                  }
                val fb = asyncTask(startLatches.first(), fbExit)
                fa.await()
                fb.await()
              }
            }) { case -> require(scopeExit.complete(case)) }
            fail("Should never come here")
          }
          .runCont() shouldBe a
        withTimeout(2.seconds) {
          scopeExit.await().shouldBeTypeOf<ExitCase.Cancelled>()
          fbExit.await().shouldBeTypeOf<ExitCase.Cancelled>()
          nestedExits.awaitAll().forEach { it.shouldBeTypeOf<ExitCase.Cancelled>() }
        }
      }
    }

    "Concurrent shift - async" {
      checkAll(Arb.int(), Arb.int()) { a, b ->
        effect {
            coroutineScope {
              val fa = async { shift<Nothing>(a) }
              val fb = async { shift<Nothing>(b) }
              "I will be overwritten by shift - coroutineScope waits until all async are finished"
            }
          }
          .fold({ fail("Async is never awaited, and thus ignored.") }, ::identity) shouldBe
          "I will be overwritten by shift - coroutineScope waits until all async are finished"
      }
    }

    "Concurrent shift - async exit results" {
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
                val fa =
                  async<Unit> {
                    startLatches.zip(nestedExits) { start, promise -> asyncTask(start, promise) }
                    startLatches.awaitAll()
                    shift(a)
                  }
                str
              }
            }) { case -> require(exitScope.complete(case)) }
          }
          .runCont() shouldBe str

        withTimeout(2.seconds) {
          nestedExits.awaitAll().forEach { it.shouldBeTypeOf<ExitCase.Cancelled>() }
        }
      }
    }

    "Concurrent shift - launch" {
      checkAll(Arb.int(), Arb.int()) { a, b ->
        effect {
            coroutineScope {
              launch { shift(a) }
              launch { shift(b) }
              "shift does not escape `launch`"
            }
          }
          .runCont() shouldBe "shift does not escape `launch`"
      }
    }

    "Concurrent shift - launch exit results" {
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
                val fa = launch {
                  startLatches.zip(nestedExits) { start, promise -> launchTask(start, promise) }
                  startLatches.awaitAll()
                  shift(a)
                }
                str
              }
            }) { case -> require(scopeExit.complete(case)) }
          }
          .runCont() shouldBe str
        withTimeout(2.seconds) {
          scopeExit.await().shouldBeTypeOf<ExitCase.Completed>()
          nestedExits.awaitAll().forEach { it.shouldBeTypeOf<ExitCase.Cancelled>() }
        }
      }
    }

    // `shift` escapes `cont` block, and gets rethrown inside `coroutineScope`.
    // Effectively awaiting/executing DSL code, outside of the DSL...
    "async funky scenario #1 - Extract `shift` from `cont` through `async`" {
      checkAll(Arb.int(), Arb.int()) { a, b ->
        runCatching {
            coroutineScope {
              val shiftedAsync =
                effect<Int, Deferred<String>> {
                    val fa = async<Int> { shift(a) }
                    async { shift(b) }
                  }
                  .fold({ fail("shift was never awaited, so it never took effect") }, ::identity)
              shiftedAsync.await()
            }
          }
          .exceptionOrNull()
          ?.message shouldBe "Shifted Continuation"
      }
    }
  })
