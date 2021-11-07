import arrow.core.Either
import arrow.core.identity
import arrow.core.left
import arrow.core.right
import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.guaranteeCase
import arrow.fx.coroutines.never
import io.kotest.assertions.fail
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.intercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.startCoroutine
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.seconds
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout

@OptIn(ExperimentalTime::class)
class ContSpec : StringSpec({

  suspend fun <A> Cont<Nothing, A>.value(): A =
    fold(::identity, ::identity)

  suspend fun Cont<*, *>.runCont(): Unit =
    fold({ }, { })

  "try/catch - can recover from shift" {
    checkAll(Arb.int(), Arb.string()) { i, s ->
      cont<String, Int> {
        try {
          shift(s)
        } catch (e: Throwable) {
          i
        }
      }.fold({ fail("Should never come here") }, ::identity) shouldBe i
    }
  }

  "try/catch - can recover from shift suspended" {
    checkAll(Arb.int(), Arb.string()) { i, s ->
      cont<String, Int> {
        try {
          shift(s.suspend())
        } catch (e: Throwable) {
          i
        }
      }.fold({ fail("Should never come here") }, ::identity) shouldBe i
    }
  }

  "try/catch - finally works" {
    checkAll(Arb.string(), Arb.int()) { s, i ->
      val promise = CompletableDeferred<Int>()
      cont<String, Int> {
        try {
          shift(s)
        } finally {
          require(promise.complete(i))
        }
      }.fold(::identity) { fail("Should never come here") } shouldBe s
      promise.await() shouldBe i
    }
  }

  "try/catch - finally works suspended" {
    checkAll(Arb.string(), Arb.int()) { s, i ->
      val promise = CompletableDeferred<Int>()
      cont<String, Int> {
        try {
          shift(s.suspend())
        } finally {
          require(promise.complete(i))
        }
      }.fold(::identity) { fail("Should never come here") } shouldBe s
      promise.await() shouldBe i
    }
  }

  "try/catch - First shift is ignored and second is returned" {
    checkAll(Arb.int(), Arb.string(), Arb.string()) { i, s, s2 ->
      cont<String, Int> {
        val x: Int = try {
          shift(s)
        } catch (e: Throwable) {
          i
        }
        shift(s2)
      }.fold(::identity) { fail("Should never come here") } shouldBe s2
    }
  }

  "try/catch - First shift is ignored and second is returned suspended" {
    checkAll(Arb.int(), Arb.string(), Arb.string()) { i, s, s2 ->
      cont<String, Int> {
        val x: Int = try {
          shift(s.suspend())
        } catch (e: Throwable) {
          i
        }
        shift(s2.suspend())
      }.fold(::identity) { fail("Should never come here") } shouldBe s2
    }
  }

  "immediate values" {
    cont<Nothing, Int> {
      1
    }.value() shouldBe 1
  }

  "suspended value" {
    cont<Nothing, Int> {
      1.suspend()
    }.value() shouldBe 1
  }

  "immediate short-circuit" {
    cont<String, Nothing> {
      shift("hello")
    }.fold(::identity, ::identity) shouldBe "hello"
  }

  "suspended short-circuit" {
    cont<String, Nothing> {
      shift("hello".suspend())
    }.fold(::identity, ::identity) shouldBe "hello"
  }

  "Rethrows immediate exceptions" {
    val e = RuntimeException("test")
    Either.catch {
      cont<Nothing, Nothing> {
        throw e
      }.runCont()
    } shouldBe Either.Left(e)
  }

  "Rethrows suspended exceptions" {
    val e = RuntimeException("test")
    Either.catch {
      cont<Nothing, Nothing> {
        e.suspend()
      }.runCont()
    } shouldBe Either.Left(e)
  }

  "Can short-circuit immediately from nested blocks" {
    cont<String, Int> {
      cont<Nothing, Long> { shift("test") }.runCont()
      fail("Should never reach this point")
    }.fold(::identity, ::identity) shouldBe "test"
  }

  "Can short-circuit suspended from nested blocks" {
    cont<String, Int> {
      cont<Nothing, Long> { shift("test".suspend()) }.runCont()
      fail("Should never reach this point")
    }.fold(::identity, ::identity) shouldBe "test"
  }

  "Can short-circuit immediately after suspending from nested blocks" {
    cont<String, Int> {
      cont<Nothing, Long> {
        1L.suspend()
        shift("test".suspend())
      }.runCont()
      fail("Should never reach this point")
    }.fold(::identity, ::identity) shouldBe "test"
  }

  // Fails https://github.com/Kotlin/kotlinx.coroutines/issues/3005
  "ensure null in either computation" {
    checkAll(Arb.boolean(), Arb.int(), Arb.string()) { predicate, success, shift ->
      either<String, Int> {
        ensure(predicate) { shift }
        success
      } shouldBe if (predicate) success.right() else shift.left()
    }
  }

  // Fails https://github.com/Kotlin/kotlinx.coroutines/issues/3005
  "ensureNotNull in either computation" {
    fun square(i: Int): Int = i * i

    checkAll(Arb.int().orNull(), Arb.string()) { i: Int?, shift: String ->
      val res = either<String, Int> {
        val ii = i
        ensureNotNull(ii) { shift }
        square(ii) // Smart-cast by contract
      }
      val expected = i?.let(::square)?.right() ?: shift.left()
      res shouldBe expected
    }
  }

  "Short-circuiting co-operates with KotlinX Structured Concurrency" {
    val latch = CompletableDeferred<Unit>()
    val cancelled = CompletableDeferred<Throwable?>()

    cont<String, Nothing> {
      coroutineScope {
        val never = async<Nothing>(Dispatchers.Default) { completeOnCancellation(latch, cancelled) }
        val fail = async<Int>(Dispatchers.Default) {
          latch.await()
          shift("hello")
        }
        fail.await() // When this fails, it should also close never
        never.await()
      }
    }.fold(::identity, ::identity) shouldBe "hello"

    withTimeout(Duration.Companion.seconds(2)) {
      // KotlinX internally wraps exceptions
      cancelled.await().shouldNotBeNull()?.cause?.message shouldBe "Shifted Continuation"
//      cancelled.await().shouldNotBeNull().message shouldBe "Shifted Continuation"
    }
  }

  "Computation blocks run on parent context" {
    val parentCtx = currentContext()
    cont<Nothing, Unit> {
      currentContext() shouldBe parentCtx
    }.runCont()
  }

  "Concurrent shift - async await" {
    checkAll(Arb.int(), Arb.int()) { a, b ->
      cont<Int, String> {
        coroutineScope {
          val fa = async<String> { shift(a) }
          val fb = async<String> { shift(b) }
          fa.await() + fb.await()
        }
      }.fold(::identity, ::identity) shouldBeIn listOf(a, b)
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
          never<Unit>()
        }) { case ->
          require(exit.complete(case))
        }
      }

      cont<Int, String> {
        guaranteeCase({
          coroutineScope {
            val fa = async<Unit> {
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
        }) { case ->
          require(scopeExit.complete(case))
        }
        fail("Should never come here")
      }.fold(::identity, ::identity) shouldBe a
      withTimeout(2.seconds) {
        scopeExit.await().shouldBeTypeOf<ExitCase.Failure>()
        // This is ExitCase.Cancelled since it was cancelled by KotlinX CoroutineScope
        fbExit.await().shouldBeTypeOf<ExitCase.Cancelled>()
        nestedExits.awaitAll().forEach { it.shouldBeTypeOf<ExitCase.Cancelled>() }
      }
    }
  }

  "Concurrent shift - async funky scenario #1" {
    val scenario = "I will NOT be overridden by shift, scope is waiting on async on the outside"
    checkAll(Arb.int(), Arb.int()) { a, b ->
      kotlin.runCatching {
        coroutineScope {
          cont<Int, String> {
            val fa = async<Nothing> { shift(a) }
            val fb = async<Nothing> { shift(b) }
            scenario
          }.fold({
            fail("shift never works here")
          }, ::identity) shouldBe scenario
        }
      }.exceptionOrNull()?.message shouldBe "Shifted Continuation"
    }
  }

  "Concurrent shift - async funky scenario #2" {
    val scenario = "I will NOT be overridden by shift, scope is waiting on async on the outside"
    checkAll(Arb.int(), Arb.int()) { a, b ->
      kotlin.runCatching {
        coroutineScope {
          cont<Int, Deferred<String>> {
            val fa = async<Nothing> { shift(a) }
            async { shift(b) }
          }.fold({
            fail("shift never works here")
          }, ::identity).await()
        }
      }.exceptionOrNull()?.message shouldBe "Shifted Continuation"
    }
  }

  "Concurrent shift - async" {
    checkAll(Arb.int(), Arb.int()) { a, b ->
      cont<Int, String> {
        coroutineScope {
          val fa = async<Nothing> { shift(a) }
          val fb = async<Nothing> { shift(b) }
          "I will be overwritten by shift - coroutineScope waits until all async are finished"
        }
      }.fold(::identity, ::identity) shouldBeIn listOf(a, b)
    }
  }

  "Concurrent shift - async exit results" {
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
          never<Unit>()
        }) { case ->
          require(exit.complete(case))
        }
      }

      cont<Int, String> {
        guaranteeCase({
          coroutineScope {
            val fa = async<Unit> {
              startLatches.drop(1).zip(nestedExits) { start, promise ->
                asyncTask(start, promise)
              }
              startLatches.awaitAll()
              shift(a)
            }
            val fb = asyncTask(startLatches.first(), fbExit)
          }
        }) { case ->
          require(scopeExit.complete(case))
        }
        fail("Should never come here")
      }.fold(::identity, ::identity) shouldBe a
      withTimeout(2.seconds) {
        scopeExit.await().shouldBeTypeOf<ExitCase.Failure>()
        // This is ExitCase.Cancelled since it was cancelled by KotlinX CoroutineScope
        fbExit.await().shouldBeTypeOf<ExitCase.Cancelled>()
        nestedExits.awaitAll().forEach { it.shouldBeTypeOf<ExitCase.Cancelled>() }
      }
    }
  }

  "Concurrent shift - launch" {
    checkAll(Arb.int(), Arb.int()) { a, b ->
      cont<Int, String> {
        coroutineScope {
          launch { shift(a) }
          launch { shift(b) }
          "This will be overriden by shift - scope waits until all launch finished"
        }
      }.fold(::identity, ::identity) shouldBeIn listOf(a, b)
    }
  }

  "Concurrent shift - launch exit results" {
    checkAll(Arb.int()) { a ->
      val scopeExit = CompletableDeferred<ExitCase>()
      val fbExit = CompletableDeferred<ExitCase>()
      val startLatches = (0..11).map { CompletableDeferred<Unit>() }
      val nestedExits = (0..10).map { CompletableDeferred<ExitCase>() }

      fun CoroutineScope.launchTask(
        start: CompletableDeferred<Unit>,
        exit: CompletableDeferred<ExitCase>
      ): Job = launch {
        guaranteeCase({
          start.complete(Unit)
          never<Unit>()
        }) { case ->
          require(exit.complete(case))
        }
      }

      cont<Int, String> {
        guaranteeCase({
          coroutineScope {
            val fa = launch {
              startLatches.drop(1).zip(nestedExits) { start, promise ->
                launchTask(start, promise)
              }
              startLatches.awaitAll()
              shift(a)
            }
            val fb = launchTask(startLatches.first(), fbExit)
          }
        }) { case ->
          require(scopeExit.complete(case))
        }
        fail("Should never come here")
      }.fold(::identity, ::identity) shouldBe a
      withTimeout(2.seconds) {
        scopeExit.await().shouldBeTypeOf<ExitCase.Failure>()
        // This is ExitCase.Cancelled since it was cancelled by KotlinX CoroutineScope
        fbExit.await().shouldBeTypeOf<ExitCase.Cancelled>()
        nestedExits.awaitAll().forEach { it.shouldBeTypeOf<ExitCase.Cancelled>() }
      }
    }
  }
})

suspend fun currentContext(): CoroutineContext =
  kotlin.coroutines.coroutineContext

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

suspend fun <A> completeOnCancellation(
  latch: CompletableDeferred<Unit>,
  cancelled: CompletableDeferred<Throwable?>
): A =
  suspendCancellableCoroutine { cont ->
    cont.invokeOnCancellation { cause ->
      if (!cancelled.complete(cause)) throw AssertionError("cancelled latch was completed twice")
      else Unit
    }

    if (!latch.complete(Unit)) throw AssertionError("latch was completed twice")
    else Unit
  }
