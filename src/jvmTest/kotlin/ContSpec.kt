import arrow.core.Either
import arrow.core.identity
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
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.intercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.startCoroutine
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine

@OptIn(ExperimentalTime::class)
class ContSpec : StringSpec({
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

  suspend fun test() {
    val leakedAsync = coroutineScope<suspend () -> Deferred<Unit>> {
      suspend {
        async {
          println("I am never going to run, until I get called invoked from outside")
        }
      }
    }
    leakedAsync.invoke().await()
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
    }.runCont() shouldBe "hello"
  }

  "suspended short-circuit" {
    cont<String, Nothing> {
      shift("hello".suspend())
    }.runCont() shouldBe "hello"
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
    }.runCont() shouldBe "test"
  }

  "Can short-circuit suspended from nested blocks" {
    cont<String, Int> {
      cont<Nothing, Long> { shift("test".suspend()) }.runCont()
      fail("Should never reach this point")
    }.runCont() shouldBe "test"
  }

  "Can short-circuit immediately after suspending from nested blocks" {
    cont<String, Int> {
      cont<Nothing, Long> {
        1L.suspend()
        shift("test".suspend())
      }.runCont()
      fail("Should never reach this point")
    }.runCont() shouldBe "test"
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
