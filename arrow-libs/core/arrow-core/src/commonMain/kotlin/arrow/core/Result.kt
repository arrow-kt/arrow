@file:OptIn(ExperimentalContracts::class)

package arrow.core

import kotlin.Result.Companion.failure
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
