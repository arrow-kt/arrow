@file:OptIn(ExperimentalContracts::class)
package arrow.core

import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@PublishedApi
internal inline val UnitResult: Result<Unit>
  inline get() = success(Unit)

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
public inline fun <A> Result<A>.handleErrorWith(transform: (throwable: Throwable) -> Result<A>): Result<A> {
  contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
  return when (val exception = exceptionOrNull()) {
    null -> this
    else -> transform(exception)
  }
}

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

/**
 * Combines n-arity independent [Result] values with a [transform] function.
 */
public inline fun <A, B, C> Result<A>.zip(b: Result<B>, transform: (A, B) -> C): Result<C> {
  contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
  return zip(
    b,
    UnitResult,
    UnitResult,
    UnitResult,
    UnitResult,
    UnitResult,
    UnitResult,
    UnitResult,
    UnitResult
  ) { a, b, _, _, _, _, _, _, _, _ -> transform(a, b) }
}

public inline fun <A, B, C, D> Result<A>.zip(b: Result<B>, c: Result<C>, transform: (A, B, C) -> D): Result<D> {
  contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
  return zip(
    b,
    c,
    UnitResult,
    UnitResult,
    UnitResult,
    UnitResult,
    UnitResult,
    UnitResult,
    UnitResult
  ) { a, b, c, _, _, _, _, _, _, _ -> transform(a, b, c) }
}

public inline fun <A, B, C, D, E> Result<A>.zip(
  b: Result<B>,
  c: Result<C>,
  d: Result<D>,
  transform: (A, B, C, D) -> E
): Result<E> {
  contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
  return zip(
    b,
    c,
    d,
    UnitResult,
    UnitResult,
    UnitResult,
    UnitResult,
    UnitResult,
    UnitResult
  ) { a, b, c, d, _, _, _, _, _, _ -> transform(a, b, c, d) }
}

public inline fun <A, B, C, D, E, F> Result<A>.zip(
  b: Result<B>,
  c: Result<C>,
  d: Result<D>,
  e: Result<E>,
  transform: (A, B, C, D, E) -> F
): Result<F> {
  contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
  return zip(b, c, d, e, UnitResult, UnitResult, UnitResult, UnitResult, UnitResult) { a, b, c, d, e, f, _, _, _, _ ->
    transform(
      a,
      b,
      c,
      d,
      e
    )
  }
}

public inline fun <A, B, C, D, E, F, G> Result<A>.zip(
  b: Result<B>,
  c: Result<C>,
  d: Result<D>,
  e: Result<E>,
  f: Result<F>,
  transform: (A, B, C, D, E, F) -> G
): Result<G> {
  contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
  return zip(b, c, d, e, f, UnitResult, UnitResult, UnitResult, UnitResult) { a, b, c, d, e, f, _, _, _, _ ->
    transform(
      a,
      b,
      c,
      d,
      e,
      f
    )
  }
}

public inline fun <A, B, C, D, E, F, G, H> Result<A>.zip(
  b: Result<B>,
  c: Result<C>,
  d: Result<D>,
  e: Result<E>,
  f: Result<F>,
  g: Result<G>,
  transform: (A, B, C, D, E, F, G) -> H
): Result<H> {
  contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
  return zip(b, c, d, e, f, g, UnitResult, UnitResult, UnitResult) { a, b, c, d, e, f, g, _, _, _ -> transform(a, b, c, d, e, f, g) }
}

public inline fun <A, B, C, D, E, F, G, H, I> Result<A>.zip(
  b: Result<B>,
  c: Result<C>,
  d: Result<D>,
  e: Result<E>,
  f: Result<F>,
  g: Result<G>,
  h: Result<H>,
  transform: (A, B, C, D, E, F, G, H) -> I
): Result<I> {
  contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
  return zip(b, c, d, e, f, g, h, UnitResult, UnitResult) { a, b, c, d, e, f, g, h, _, _ -> transform(a, b, c, d, e, f, g, h) }
}

public inline fun <A, B, C, D, E, F, G, H, I, J> Result<A>.zip(
  b: Result<B>,
  c: Result<C>,
  d: Result<D>,
  e: Result<E>,
  f: Result<F>,
  g: Result<G>,
  h: Result<H>,
  i: Result<I>,
  transform: (A, B, C, D, E, F, G, H, I) -> J
): Result<J> {
  contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
  return zip(b, c, d, e, f, g, h, i, UnitResult) { a, b, c, d, e, f, g, h, i, _ -> transform(a, b, c, d, e, f, g, h, i) }
}

@Suppress("UNCHECKED_CAST")
public inline fun <A, B, C, D, E, F, G, H, I, J, K> Result<A>.zip(
  b: Result<B>,
  c: Result<C>,
  d: Result<D>,
  e: Result<E>,
  f: Result<F>,
  g: Result<G>,
  h: Result<H>,
  i: Result<I>,
  j: Result<J>,
  transform: (A, B, C, D, E, F, G, H, I, J) -> K,
): Result<K> {
  contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
  return if (isSuccess && b.isSuccess && c.isSuccess && d.isSuccess && e.isSuccess && f.isSuccess && g.isSuccess && h.isSuccess && i.isSuccess && j.isSuccess)
    success(
      transform(
        getOrNull() as A,
        b.getOrNull() as B,
        c.getOrNull() as C,
        d.getOrNull() as D,
        e.getOrNull() as E,
        f.getOrNull() as F,
        g.getOrNull() as G,
        h.getOrNull() as H,
        i.getOrNull() as I,
        j.getOrNull() as J
      )
    ) else
    composeErrors(
      exceptionOrNull(),
      b.exceptionOrNull(),
      c.exceptionOrNull(),
      d.exceptionOrNull(),
      e.exceptionOrNull(),
      f.exceptionOrNull(),
      g.exceptionOrNull(),
      h.exceptionOrNull(),
      i.exceptionOrNull(),
      j.exceptionOrNull(),
    )!!.let(::failure)
}

@PublishedApi
internal fun composeErrors(vararg other: Throwable?): Throwable? =
  other.reduceOrNull { a, b ->
    Nullable.zip(a, b, Throwable::addSuppressed)
    a ?: b
  }
