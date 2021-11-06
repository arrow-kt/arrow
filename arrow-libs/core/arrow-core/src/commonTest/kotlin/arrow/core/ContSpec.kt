package arrow.core

import arrow.core.test.UnitSpec
import arrow.core.test.generators.throwable
import io.kotest.assertions.fail
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.intercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.startCoroutine
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout

@OptIn(ExperimentalTime::class)
class ContSpec : UnitSpec({

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

  "immediate values" {
    checkAll(Arb.int()) { i ->
      cont<Nothing, Int> {
        i
      }.value() shouldBe i
    }
  }

  "suspended value" {
    checkAll(Arb.int()) { i ->
      cont<Nothing, Int> {
        i.suspend()
      }.value() shouldBe i
    }
  }

  "immediate short-circuit" {
    checkAll(Arb.string()) { s ->
      cont<String, Nothing> {
        shift(s)
      }.fold(::identity, ::identity) shouldBe s
    }
  }

  "suspended short-circuit" {
    checkAll(Arb.string()) { s ->
      cont<String, Nothing> {
        shift(s.suspend())
      }.fold(::identity, ::identity) shouldBe s
    }
  }

  "Rethrows immediate exceptions" {
    checkAll(Arb.throwable()) { e ->
      Either.catch {
        cont<Nothing, Nothing> {
          throw e
        }.runCont()
      } shouldBe Either.Left(e)
    }
  }

  "Rethrows suspended exceptions" {
    checkAll(Arb.throwable()) { e ->
      Either.catch {
        cont<Nothing, Nothing> {
          e.suspend()
        }.runCont()
      } shouldBe Either.Left(e)
    }
  }

  "Can short-circuit immediately from nested blocks" {
    checkAll(Arb.string()) { s ->
      cont<String, Int> {
        cont<Nothing, Long> { shift(s) }.runCont()
        fail("Should never reach this point")
      }.fold(::identity, ::identity) shouldBe s
    }
  }

  "Can short-circuit suspended from nested blocks" {
    checkAll(Arb.string()) { s ->
      cont<String, Int> {
        cont<Nothing, Long> { shift(s.suspend()) }.runCont()
        fail("Should never reach this point")
      }.fold(::identity, ::identity) shouldBe s
    }
  }

  "Can short-circuit immediately after suspending from nested blocks" {
    checkAll(Arb.string()) { s ->
      cont<String, Int> {
        cont<Nothing, Long> {
          1L.suspend()
          shift(s.suspend())
        }.runCont()
        fail("Should never reach this point")
      }.fold(::identity, ::identity) shouldBe s
    }
  }

  // Fails https://github.com/Kotlin/kotlinx.coroutines/issues/3005
  "ensure null in either computation" {
    checkAll(Arb.boolean(), Arb.int(), Arb.string()) { predicate, success, shift ->
      cont<String, Int> {
        ensure(predicate) { shift }
        success
      }.toEither() shouldBe if (predicate) success.right() else shift.left()
    }
  }

  // Fails https://github.com/Kotlin/kotlinx.coroutines/issues/3005
  "ensureNotNull in either computation" {
    fun square(i: Int): Int = i * i

    checkAll(Arb.int().orNull(), Arb.string()) { i: Int?, shift->
      val res = cont<String, Int> {
        ensureNotNull(i) { shift }
        square(i) // Smart-cast by contract
      }.toEither()
      val expected = i?.let(::square)?.right() ?: shift.left()
      res shouldBe expected
    }
  }

  "Short-circuiting co-operates with KotlinX Structured Concurrency" {
    checkAll(Arb.string()) { s ->
      val latch = CompletableDeferred<Unit>()
      val cancelled = CompletableDeferred<Throwable?>()

      cont<String, Nothing> {
        coroutineScope {
          val never = async<Nothing>(Dispatchers.Default) { completeOnCancellation(latch, cancelled) }
          val fail = async<Int>(Dispatchers.Default) {
            latch.await()
            shift(s)
          }
          fail.await() // When this fails, it should also close never
          never.await()
        }
      }.fold(::identity, ::identity) shouldBe s

      withTimeout(Duration.Companion.seconds(2)) {
        cancelled.await().shouldNotBeNull().message shouldBe "Shifted Continuation"
      }
    }
  }

  "Computation blocks run on parent context" {
    val parentCtx = currentContext()
    cont<Nothing, Unit> {
      currentContext() shouldBe parentCtx
    }.runCont()
  }

  "Concurrent shift" {
    checkAll(Arb.int(), Arb.int()) { a, b ->
      cont<Int, String> {
        coroutineScope {
          val fa = async<Nothing>(start = CoroutineStart.UNDISPATCHED) { shift(a) }
          val fb = async<Nothing>(start = CoroutineStart.UNDISPATCHED) { shift(b) }
          fa.await()
          fb.await()
        }
      }.fold(::identity, ::identity) shouldBeIn listOf(a, b)
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
