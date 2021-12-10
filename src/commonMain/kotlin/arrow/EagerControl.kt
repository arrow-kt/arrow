package arrow

import arrow.core.Either
import arrow.core.Validated
import arrow.core.identity
import arrow.internal.EmptyValue
import kotlin.coroutines.RestrictsSuspension
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.jvm.JvmInline

/**
 * `RestrictsSuspension` version of `Cont<R, A>`. This version runs eagerly, can can be used in
 * non-suspending code.
 */
public fun <R, A> eagerControl(f: suspend EagerContEffect<R>.() -> A): EagerControl<R, A> =
  EagerControlDsl(f)

@RestrictsSuspension
public interface EagerContEffect<R> {

  /** Short-circuit the [Cont] computation with value [R]. */
  public suspend fun <B> shift(r: R): B

  public suspend fun <A> EagerControl<R, A>.bind(): A {
    var left: Any? = EmptyValue
    var right: Any? = EmptyValue
    fold({ r -> left = r }, { a -> right = a })
    return if (left === EmptyValue) EmptyValue.unbox(right) else shift(EmptyValue.unbox(left))
  }
}

public interface EagerControl<R, A> {
  public fun <B> fold(recover: (R) -> B, transform: (A) -> B): B

  public fun toEither(): Either<R, A> = fold({ Either.Left(it) }) { Either.Right(it) }

  public fun toValidated(): Validated<R, A> =
    fold({ Validated.Invalid(it) }) { Validated.Valid(it) }

  public fun <B> map(f: (A) -> B): EagerControl<R, B> = flatMap { a -> eagerControl { f(a) } }

  public fun <B> flatMap(f: (A) -> EagerControl<R, B>): EagerControl<R, B> = eagerControl {
    f(bind()).bind()
  }

  public fun attempt(): EagerControl<R, Result<A>> = eagerControl {
    kotlin.runCatching { bind() }
  }

  public fun handleError(f: (R) -> A): EagerControl<Nothing, A> = eagerControl {
    fold(f, ::identity)
  }

  public fun <R2> handleErrorWith(f: (R) -> EagerControl<R2, A>): EagerControl<R2, A> =
      eagerControl {
    toEither().fold({ r -> f(r).bind() }, ::identity)
  }

  public fun <B> redeem(f: (R) -> B, g: (A) -> B): EagerControl<Nothing, B> = eagerControl {
    fold(f, g)
  }

  public fun <R2, B> redeemWith(
    f: (R) -> EagerControl<R2, B>,
    g: (A) -> EagerControl<R2, B>
  ): EagerControl<R2, B> = eagerControl { fold(f, g).bind() }
}
