import arrow.continuations.generic.AtomicRef
import arrow.continuations.generic.loop
import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.Validated
import arrow.core.identity
import internal.EmptyValue
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.coroutines.Continuation
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.jvm.JvmInline

public fun <R, A> cont(f: suspend ContEffect<R>.() -> A): Cont<R, A> =
  Continuation(f)

/**
 * [Cont] represents a suspending computation that runs will either
 *  - Complete with a value of [A].
 *  - Short-circuit with a value of [R].
 *
 * So [Cont] is defined by [fold], to map both values of [R] and [A] to a value of `B`.
 */
public interface Cont<R, A> {
  suspend fun <B> fold(f: suspend (R) -> B, g: suspend (A) -> B): B

  suspend fun toEither(): Either<R, A> =
    fold({ Either.Left(it) }) { Either.Right(it) }

  suspend fun toValidated(): Validated<R, A> =
    fold({ Validated.Invalid(it) }) { Validated.Valid(it) }

  fun attempt(): Cont<R, Result<A>> =
    cont { runCatching { bind() } }

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

// We create a `Token` for every scope, so we can properly differentiate between nested scopes
private class ShiftCancellationException(val token: Token) : CancellationException("Shifted Continuation")

// Class that represents a unique token by hash comparison
private class Token {
  override fun toString(): String = "Token(${hashCode().toUInt().toString(16)})"
}

@JvmInline
private value class Shifted<A>(private val state: AtomicRef<Any?> = AtomicRef(EmptyValue)) {
  inline fun update(f: () -> A): Boolean {
    var backing: Any? = EmptyValue
    state.loop { a ->
      if (a === EmptyValue) {
        if (state.compareAndSet(
            EmptyValue,
            if (backing === EmptyValue) {
              backing = f()
              backing
            } else backing
          )
        ) return true
      } else {
        return false
      }
    }
  }

  fun value(): A = EmptyValue.unbox(state.get())
}

// Reification of Cont program
@JvmInline
private value class Continuation<R, A>(private val f: suspend ContEffect<R>.() -> A) : Cont<R, A> {
  override suspend fun <B> fold(f: suspend (R) -> B, g: suspend (A) -> B): B =
    suspendCoroutineUninterceptedOrReturn { cont ->
      val token = Token()
      val shifted = Shifted<B>()

      val effect = object : ContEffect<R> {
        // Shift away from this Continuation by intercepting it, and completing it with ShiftCancellationException
        // This is needed because this function will never yield a result,
        // so it needs to be cancelled to properly support coroutine cancellation
        override suspend fun <B> shift(r: R): B {
          // TODO if a concurrent coroutine called shift, do we also complete with `ShiftCancellationException`?
          //      NOTE: This _should_ only possible if coroutines are already coupled to each-other with structured concurrency
          //      So re-emitting CancellationException might not be needed ??
          //      Related test: https://github.com/nomisRev/Continuation/blob/main/src/commonTest/kotlin/ContSpec.kt#L161
          shifted.update { f(r) }
          throw ShiftCancellationException(token)
        }
      }

      try {
        suspend { g(f(effect)) }.startCoroutineUninterceptedOrReturn(Continuation(cont.context) { res ->
          res.fold(cont::resume) { throwable ->
            if (throwable is ShiftCancellationException && token == throwable.token) cont.resume(shifted.value())
            else cont.resumeWith(res)
          }
        })
      } catch (e: ShiftCancellationException) {
        if (token == e.token) shifted.value() else throw e
      }
    }
}
