package arrow

import arrow.core.Either
import arrow.core.Validated
import arrow.core.identity
import arrow.internal.EmptyValue
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.jvm.JvmInline

/**
 * `RestrictsSuspension` version of `Cont<R, A>`.
 * This version runs eagerly, can can be used in non-suspending code.
 */
public fun <R, A> restrictedCont(f: suspend RestrictedContEffect<R>.() -> A): RestrictedCont<R, A> =
  RestrictedContDsl(f)

@RestrictsSuspension
public interface RestrictedContEffect<R> {

  /**
   * Short-circuit the [Cont] computation with value [R].
   */
  public suspend fun <B> shift(r: R): B

  public suspend fun <A> RestrictedCont<R, A>.bind(): A {
    var left: Any? = EmptyValue
    var right: Any? = EmptyValue
    fold({ r ->
      left = r
    }, { a ->
      right = a
    })
    return if (left === EmptyValue) EmptyValue.unbox(right)
    else shift(EmptyValue.unbox(left))
  }
}

public interface RestrictedCont<R, A> {
  public fun <B> fold(f: (R) -> B, g: (A) -> B): B

  public fun toEither(): Either<R, A> =
    fold({ Either.Left(it) }) { Either.Right(it) }

  public fun toValidated(): Validated<R, A> =
    fold({ Validated.Invalid(it) }) { Validated.Valid(it) }

  public fun <B> map(f: (A) -> B): RestrictedCont<R, B> =
    flatMap { a ->
      restrictedCont { f(a) }
    }

  public fun <B> flatMap(f: (A) -> RestrictedCont<R, B>): RestrictedCont<R, B> =
    restrictedCont { f(bind()).bind() }

  public fun attempt(): RestrictedCont<R, Result<A>> =
    restrictedCont { kotlin.runCatching { bind() } }

  public fun handleError(f: (R) -> A): RestrictedCont<Nothing, A> =
    restrictedCont { fold(f, ::identity) }

  public fun <R2> handleErrorWith(f: (R) -> RestrictedCont<R2, A>): RestrictedCont<R2, A> =
    restrictedCont {
      toEither().fold({ r ->
        f(r).bind()
      }, ::identity)
    }

  public fun <B> redeem(f: (R) -> B, g: (A) -> B): RestrictedCont<Nothing, B> =
    restrictedCont { fold(f, g) }

  public fun <R2, B> redeemWith(
    f: (R) -> RestrictedCont<R2, B>,
    g: (A) -> RestrictedCont<R2, B>
  ): RestrictedCont<R2, B> =
    restrictedCont { fold(f, g).bind() }
}

@JvmInline
private value class RestrictedContDsl<R, A>(private val cont: suspend RestrictedContEffect<R>.() -> A) :
  RestrictedCont<R, A> {
  override fun <B> fold(f: (R) -> B, g: (A) -> B): B {
    var reset: Any? = EmptyValue
    val effect = object : RestrictedContEffect<R> {
      override suspend fun <B> shift(r: R): B =
        suspendCoroutineUninterceptedOrReturn { cont ->
          reset = f(r)
          COROUTINE_SUSPENDED
        }
    }
    val a: Any? = cont.startCoroutineUninterceptedOrReturn(effect, Continuation(EmptyCoroutineContext) { shifted ->
      // If we reach here, then it means we shifted.
      shifted.getOrThrow()
    })
    return if (a === COROUTINE_SUSPENDED && reset !== EmptyValue) EmptyValue.unbox(reset)
    else g(a as A)
  }
}
