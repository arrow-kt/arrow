@file:OptIn(ExperimentalContracts::class)

package arrow.core

import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Compose a [transform] operation on the success value [A] into [B] whilst flattening [Result].
 * @see mapCatching if you want run a function that catches and maps with `(A) -> B`
 */
public inline fun <A, B> Result<A>.flatMap(transform: (value: A) -> Result<B>): Result<B> {
  contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
  return map(transform).fold(::identity, ::failure)
}

/**
 * Compose a recovering [transform] operation on the failure value [Throwable] whilst flattening [Result].
 * @see recoverCatching if you want run a function that catches and maps recovers with `(Throwable) -> A`.
 */
public inline fun <A> Result<A>.handleErrorWith(transform: (throwable: Throwable) -> Result<A>): Result<A> =
  recoverCatching { transform(it).getOrThrow() }

/**
 * Compose both:
 *  - a [transform] operation on the success value [A] into [B] whilst flattening [Result].
 *  - a recovering [transform] operation on the failure value [Throwable] whilst flattening [Result].
 *
 * Combining the powers of [flatMap] and [handleErrorWith].
 */
public inline fun <A, B> Result<A>.redeemWith(
  handleErrorWith: (throwable: Throwable) -> Result<B>,
  transform: (value: A) -> Result<B>
): Result<B> {
  contract {
    callsInPlace(handleErrorWith, InvocationKind.AT_MOST_ONCE)
    callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
  }
  return fold(transform, handleErrorWith)
}
