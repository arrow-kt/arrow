package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.identity
import arrow.core.left
import arrow.core.right
import io.kotest.assertions.fail
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string
import kotlinx.atomicfu.atomic
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.io.PrintStream
import java.nio.charset.StandardCharsets
import java.util.concurrent.ThreadFactory
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.intercepted
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.startCoroutine

data class SideEffect(var counter: Int = 0) {
  fun increment() {
    counter++
  }
}

val singleThreadName = "single"
val single = Resource.singleThreadContext(singleThreadName)

val threadName: suspend () -> String =
  { Thread.currentThread().name }

class NamedThreadFactory(private val mkName: (Int) -> String) : ThreadFactory {
  private val count = atomic(0)
  override fun newThread(r: Runnable): Thread =
    Thread(r, mkName(count.value))
      .apply { isDaemon = true }
}

fun unsafeEquals(other: CancelToken): Matcher<CancelToken> = object : Matcher<CancelToken> {
  override fun test(value: CancelToken): MatcherResult {
    val r1 = Platform.unsafeRunSync { value.cancel.invoke() }
    val r2 = Platform.unsafeRunSync { other.cancel.invoke() }
    return MatcherResult(r1 == r2, "Expected: $r2 but found: $r1", "$r2 and $r1 should be equal")
  }
}

suspend fun assertCancellable(f: suspend () -> Unit): Unit {
  val p = Promise<ExitCase>()
  val start = Promise<Unit>()

  val fiber = ForkAndForget {
    guaranteeCase(
      fa = {
        start.complete(Unit)
        f()
      },
      finalizer = { ex -> p.complete(ex) }
    )
  }

  start.get()
  fiber.cancel()
  p.get() shouldBe ExitCase.Cancelled
}

/**
 * Catches `System.err` output, for testing purposes.
 */
fun catchSystemErr(thunk: () -> Unit): String {
  val outStream = ByteArrayOutputStream()
  catchSystemErrInto(outStream, thunk)
  return String(outStream.toByteArray(), StandardCharsets.UTF_8)
}

/**
 * Catches `System.err` output into `outStream`, for testing purposes.
 */
@Synchronized
fun <A> catchSystemErrInto(outStream: OutputStream, thunk: () -> A): A {
  val oldErr = System.err
  val fakeErr = PrintStream(outStream)
  System.setErr(fakeErr)
  return try {
    thunk()
  } finally {
    System.setErr(oldErr)
    fakeErr.close()
  }
}

fun Arb.Companion.throwable(): Arb<Throwable> =
  Arb.string().map(::RuntimeException)

fun <A> Arb.Companion.result(right: Arb<A>): Arb<Result<A>> {
  val failure: Arb<Result<A>> = Arb.throwable().map { e -> Result.failure<A>(e) }
  val success: Arb<Result<A>> = right.map { a -> Result.success(a) }
  return Arb.choice(failure, success)
}

fun <L, R> Arb.Companion.either(left: Arb<L>, right: Arb<R>): Arb<Either<L, R>> {
  val failure: Arb<Either<L, R>> = left.map { l -> l.left() }
  val success: Arb<Either<L, R>> = right.map { r -> r.right() }
  return Arb.choice(failure, success)
}

fun Arb.Companion.intRange(min: Int = Int.MIN_VALUE, max: Int = Int.MAX_VALUE): Arb<IntRange> =
  Arb.bind(Arb.int(min, max), Arb.int(min, max)) { a, b ->
    if (a < b) a..b else b..a
  }

fun Arb.Companion.longRange(min: Long = Long.MIN_VALUE, max: Long = Long.MAX_VALUE): Arb<LongRange> =
  Arb.bind(Arb.long(min, max), Arb.long(min, max)) { a, b ->
    if (a < b) a..b else b..a
  }

fun Arb.Companion.charRange(): Arb<CharRange> =
  Arb.bind(Arb.char(), Arb.char()) { a, b ->
    if (a < b) a..b else b..a
  }

fun <O> Arb.Companion.function(arb: Arb<O>): Arb<() -> O> =
  arb.map { { it } }

fun <O> Arb.Companion.suspended(arb: Arb<O>): Arb<suspend () -> O> =
  arb.map { suspend { it.suspend() } }

fun Arb.Companion.unit(): Arb<Unit> =
  Arb.constant(Unit)

/** Useful for testing success & error scenarios with an `Either` generator **/
internal fun <A> Either<Throwable, A>.rethrow(): A =
  fold({ throw it }, ::identity)

internal fun <A> Result<A>.toEither(): Either<Throwable, A> =
  fold({ a -> Either.Right(a) }, { e -> Either.Left(e) })

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

internal fun <A> A.suspended(): suspend () -> A =
  suspend { suspend() }

internal suspend fun <A> Either<Throwable, A>.suspend(): A =
  suspendCoroutineUninterceptedOrReturn { cont ->
    suspend { this }.startCoroutine(Continuation(ComputationPool) {
      it.fold(
        {
          it.fold(
            { e -> cont.intercepted().resumeWithException(e) },
            { a -> cont.intercepted().resume(a) }
          )
        },
        { e -> cont.intercepted().resumeWithException(e) }
      )
    })

    COROUTINE_SUSPENDED
  }

internal fun <A> Either<Throwable, A>.suspended(): suspend () -> A =
  suspend { suspend() }

/**
 * Example usage:
 * ```kotlin
 * val exception = assertThrows<IllegalArgumentException> {
 *     throw IllegalArgumentException("Talk to a duck")
 * }
 * assertEquals("Talk to a duck", exception.message)
 * ```
 * @see Assertions.assertThrows
 */
inline fun <A> assertThrowable(executable: () -> A): Throwable {
  val a = try {
    executable.invoke()
  } catch (e: Throwable) {
    e
  }

  return if (a is Throwable) a else fail("Expected an exception but found: $a")
}

internal suspend fun CoroutineContext.shift(): Unit =
  suspendCoroutineUninterceptedOrReturn { cont ->
    suspend { this }.startCoroutine(Continuation(this) {
      cont.resume(Unit)
    })

    COROUTINE_SUSPENDED
  }
