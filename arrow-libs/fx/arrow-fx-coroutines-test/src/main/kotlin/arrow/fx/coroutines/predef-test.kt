package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.Validated
import arrow.core.ValidatedNel
import arrow.core.identity
import arrow.core.invalid
import arrow.core.invalidNel
import arrow.core.left
import arrow.core.right
import arrow.core.valid
import arrow.core.validNel
import io.kotest.assertions.fail
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.equalityMatcher
import io.kotest.matchers.types.shouldBeInstanceOf
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
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.intercepted
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.resume
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
  val env = Environment(EmptyCoroutineContext)
  override fun test(value: CancelToken): MatcherResult {
    val r1 = env.unsafeRunSync { value.cancel.invoke() }
    val r2 = env.unsafeRunSync { other.cancel.invoke() }
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
  p.get().shouldBeInstanceOf<ExitCase.Cancelled>()
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

fun <L, R> Arb.Companion.either(left: Arb<L>, right: Arb<R>): Arb<Either<L, R>> {
  val failure: Arb<Either<L, R>> = left.map { l -> l.left() }
  val success: Arb<Either<L, R>> = right.map { r -> r.right() }
  return Arb.choice(failure, success)
}

fun <L, R> Arb.Companion.validated(left: Arb<L>, right: Arb<R>): Arb<Validated<L, R>> {
  val failure: Arb<Validated<L, R>> = left.map { l -> l.invalid() }
  val success: Arb<Validated<L, R>> = right.map { r -> r.valid() }
  return Arb.choice(failure, success)
}

fun <L, R> Arb.Companion.validatedNel(left: Arb<L>, right: Arb<R>): Arb<ValidatedNel<L, R>> {
  val failure: Arb<ValidatedNel<L, R>> = left.map { l -> l.invalidNel() }
  val success: Arb<ValidatedNel<L, R>> = right.map { r -> r.validNel() }
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

fun Arb.Companion.unit(): Arb<Unit> =
  Arb.constant(Unit)

/** Useful for testing success & error scenarios with an `Either` generator **/
fun <A> Either<Throwable, A>.rethrow(): A =
  fold({ throw it }, ::identity)

fun <A> Result<A>.toEither(): Either<Throwable, A> =
  fold({ a -> Either.Right(a) }, { e -> Either.Left(e) })

suspend fun Throwable.suspend(): Nothing =
  suspendCoroutineUninterceptedOrReturn { cont ->
    suspend { throw this }.startCoroutine(Continuation(ComputationPool) {
      cont.intercepted().resumeWith(it)
    })

    COROUTINE_SUSPENDED
  }

suspend fun <A> A.suspend(): A =
  suspendCoroutineUninterceptedOrReturn { cont ->
    suspend { this }.startCoroutine(Continuation(ComputationPool) {
      cont.intercepted().resumeWith(it)
    })

    COROUTINE_SUSPENDED
  }

fun <A> A.suspended(): suspend () -> A =
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

suspend fun CoroutineContext.shift(): Unit =
  suspendCoroutineUninterceptedOrReturn { cont ->
    suspend { this }.startCoroutine(Continuation(this) {
      cont.resume(Unit)
    })

    COROUTINE_SUSPENDED
  }

fun leftException(e: Throwable): Matcher<Either<Throwable, *>> =
  object : Matcher<Either<Throwable, *>> {
    override fun test(value: Either<Throwable, *>): MatcherResult =
      when (value) {
        is Either.Left -> when {
          value.a::class != e::class -> MatcherResult(
            false,
            "Expected exception of type ${e::class} but found ${value.a::class}",
            "Should not be exception of type ${e::class}"
          )
          value.a.message != e.message -> MatcherResult(
            false,
            "Expected exception with message ${e.message} but found ${value.a.message}",
            "Should not be exception with message ${e.message}"
          )
          else -> MatcherResult(
            true,
            "Expected exception of type ${e::class} and found ${value.a::class}",
            "Expected exception of type ${e::class} and found ${value.a::class}"
          )
        }
        is Either.Right -> MatcherResult(
          false,
          "Expected Either.Left with exception of type ${e::class} and found Right with ${value.b}",
          "Should not be Either.Left with exception"
        )
      }
  }

fun <A> either(e: Either<Throwable, A>): Matcher<Either<Throwable, A>> =
  object : Matcher<Either<Throwable, A>> {
    override fun test(value: Either<Throwable, A>): MatcherResult =
      when (value) {
        is Either.Left -> when {
          value.a::class != (e.swap().orNull() ?: Int)::class -> MatcherResult(
            false,
            "Expected $e but found $value",
            "Should not be $e"
          )
          value.a.message != (e.swap().orNull()?.message ?: -1) -> MatcherResult(
            false,
            "Expected $e but found $value",
            "Should not be $e"
          )
          else -> MatcherResult(
            true,
            "Expected exception of type ${e::class} and found ${value.a::class}",
            "Expected exception of type ${e::class} and found ${value.a::class}"
          )
        }
        is Either.Right -> equalityMatcher(e).test(value)
      }
  }
