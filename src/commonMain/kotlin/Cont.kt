import arrow.core.Either
import arrow.core.Ior
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.Validated
import arrow.core.identity
import arrow.core.nonFatalOrThrow
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume
import kotlin.jvm.JvmInline

public fun <R, A> cont(f: suspend ContEffect<R>.() -> A): Cont<R, A> =
  ContImpl(f)

/**
 * [Cont] represents a suspending computation that runs will either
 *  - Complete with a value of [A].
 *  - Short-circuit with a value of [R].
 *
 * So [Cont] is defined by [fold], to map both values of [R] and [A] to a value of `B`.
 */
public interface Cont<R, A> {
  suspend fun <B> fold(f: suspend (R) -> B, g: suspend (A) -> B): B

  suspend fun <B> fold(
    error: suspend (Throwable) -> B,
    f: suspend (R) -> B,
    g: suspend (A) -> B
  ): B = try {
    fold(f, g)
  } catch (e: Throwable) {
    error(e.nonFatalOrThrow())
  }

  suspend fun toEither(): Either<R, A> =
    fold({ Either.Left(it) }) { Either.Right(it) }

  suspend fun toIor(): Ior<R, A> =
    fold({ Ior.Left(it) }) { Ior.Right(it) }

  suspend fun toValidated(): Validated<R, A> =
    fold({ Validated.Invalid(it) }) { Validated.Valid(it) }

  suspend fun toOption(orElse: suspend (R) -> Option<A>): Option<A> =
    fold(orElse, ::Some)

  fun attempt(): Cont<R, Result<A>> =
    cont {
      try {
        Result.success(bind())
      } catch (e: Throwable) {
        Result.failure(e.nonFatalOrThrow())
      }
    }

  fun <B> map(f: suspend (A) -> B): Cont<R, B> =
    cont { fold(this::shift, f) }

  fun <B> flatMap(f: suspend (A) -> Cont<R, B>): Cont<R, B> =
    cont { fold(this::shift, f).bind() }

  fun handleError(f: suspend (R) -> A): Cont<Nothing, A> =
    cont { fold(f, ::identity) }

  fun <R2> handleErrorWith(f: suspend (R) -> Cont<R2, A>): Cont<R2, A> =
    cont { fold({ f(it).bind() }, ::identity) }

  fun <B> redeem(f: suspend (R) -> B, g: suspend (A) -> B): Cont<Nothing, B> =
    cont { fold(f, g) }

  fun <R2, B> redeemWith(f: suspend (R) -> Cont<R2, B>, g: suspend (A) -> Cont<R2, B>): Cont<R2, B> =
    cont { fold(f, g).bind() }
}

fun <R, A, B> Iterable<A>.traverseCont(f: (A) -> Cont<R, B>): Cont<R, List<B>> =
  cont { map { f(it).bind() } }

fun <R, A> Iterable<Cont<R, A>>.sequence(): Cont<R, List<A>> =
  traverseCont(::identity)

/** Context of the [Cont] DSL. */
interface ContEffect<R> {
  /**
   * Short-circuit the [Cont] computation with value [R].
   */
  public suspend fun <B> shift(r: R): B

  /** ApplicativeError alias for shift */
  public suspend fun <B> raiseError(r: R): B =
    shift(r)

  public suspend fun <B> Cont<R, B>.bind(): B =
    fold(this@ContEffect::shift, ::identity)

  public suspend fun <B> Either<R, B>.bind(): B =
    when (this) {
      is Either.Left -> shift(value)
      is Either.Right -> value
    }

  public suspend fun <B> Validated<R, B>.bind(): B =
    when (this) {
      is Validated.Valid -> value
      is Validated.Invalid -> shift(value)
    }

  public suspend fun <B> Result<B>.bind(transform: (Throwable) -> R): B =
    fold(::identity) { throwable ->
      shift(transform(throwable))
    }

  public suspend fun <B> Option<B>.bind(shift: () -> R): B =
    when (this) {
      None -> shift(shift())
      is Some -> value
    }

  // Monadic version of kotlin.require
  public suspend fun ensure(value: Boolean, shift: () -> R): Unit =
    if (value) Unit else shift(shift())
}


// Monadic version of kotlin.requireNotNull
@OptIn(ExperimentalContracts::class) // Contracts not available on open functions, so top-level.
public suspend fun <R, B : Any> ContEffect<R>.ensureNotNull(value: B?, shift: () -> R): B {
  contract { returns() implies (value != null) }
  return value ?: shift(shift())
}

// Full internal runtime implementation of Cont below

public open class ControlThrowable(
  override val message: String? = null,
  override val cause: Throwable? = null
) : Throwable(message, cause) {
  // Expect/actual JVM (fillStackTrace)
}

// Reification of Cont program
private class ShiftCancellationException(
  val token: Token,
  val shifted: Any?,
  val fold: suspend (Any?) -> Any?,
  override val cause: CancellationException = CancellationException()
) : ControlThrowable("Shifted Continuation", cause)

// Class that represents a unique token by hash comparison
private class Token {
  override fun toString(): String = "Token(${hashCode().toUInt().toString(16)})"
}

@JvmInline
private value class ContImpl<R, A>(private val f: suspend ContEffect<R>.() -> A) : Cont<R, A> {
  // We create a `Token` for fold Continuation, so we can properly differentiate between nested folds
  override suspend fun <B> fold(f: suspend (R) -> B, g: suspend (A) -> B): B =
    suspendCoroutineUninterceptedOrReturn { cont ->
      val token = Token()
      val effect = object : ContEffect<R> {
        // Shift away from this Continuation by intercepting it, and completing it with ShiftCancellationException
        // This is needed because this function will never yield a result,
        // so it needs to be cancelled to properly support coroutine cancellation
        override suspend fun <B> shift(r: R): B =
          // Some interesting consequences of how Continuation Cancellation works in Kotlin.
          // We have to throw CancellationException to signal the Continuation was cancelled, and we shifted away.
          // This however also means that the user can try/catch shift and recover from the CancellationException and thus effectively recovering from the cancellation/shift.
          // This means try/catch is also capable of recovering from monadic errors.
          // See: ContSpec - try/catch tests
          throw ShiftCancellationException(token, r, f as suspend (Any?) -> Any?)
      }

      try {
        suspend { g(f(effect)) }.startCoroutineUninterceptedOrReturn(FoldContinuation(token, cont.context, cont))
      } catch (e: ShiftCancellationException) {
        if (token == e.token) {
          val f: suspend () -> B = { e.fold(e.shifted) as B }
          f.startCoroutineUninterceptedOrReturn(cont)
        } else throw e
      }
    }
}

private class FoldContinuation<B>(
  private val token: Token,
  override val context: CoroutineContext,
  private val cont: Continuation<B>
) : Continuation<B> {
  override fun resumeWith(result: Result<B>) {
    result.fold(cont::resume) { throwable ->
      if (throwable is ShiftCancellationException && token == throwable.token) {
        val f: suspend () -> B = { throwable.fold(throwable.shifted) as B }
        when (val res = f.startCoroutineUninterceptedOrReturn(cont)) {
          COROUTINE_SUSPENDED -> Unit
          else -> cont.resume(res as B)
        }
      } else cont.resumeWith(result)
    }
  }
}
