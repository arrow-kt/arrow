package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.io.PrintStream
import java.nio.charset.StandardCharsets
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.intercepted
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.startCoroutine
import kotlin.math.max
import kotlin.math.min

data class SideEffect(var counter: Int = 0) {
  fun increment() {
    counter++
  }
}

val single = singleThreadContext("single")

val threadName: suspend () -> String =
  { Thread.currentThread().name }

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
      release = { ex -> p.complete(ex) }
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

fun Arb.Companion.intRange(min: Int = 0, max: Int = 1000): Arb<IntRange> =
  Arb.int(min, max).flatMap { a ->
    Arb.int(min, max).map { b ->
      val first = min(a, b)
      val last = max(a, b)
      first..last
    }
  }

/** Useful for testing success & error scenarios with an `Either` generator **/
internal suspend fun <A> Either<Throwable, A>.rethrow(): A =
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
