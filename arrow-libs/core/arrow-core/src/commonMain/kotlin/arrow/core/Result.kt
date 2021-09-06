package arrow.core

import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

@PublishedApi
internal inline val unitResult: Result<Unit>
  inline get() = success(Unit)

public inline fun <A, B> Result<A>.flatMap(transform: (value: A) -> Result<B>): Result<B> =
  fold({ a ->
    try {
      transform(a)
    } catch (e: Throwable) {
      failure(e)
    }
  }) { throwable ->
    failure(throwable)
  }

public inline fun <A> Result<A>.handleErrorWith(transform: (throwable: Throwable) -> Result<A>): Result<A> =
  fold({ a -> success(a) }) { throwable ->
    try {
      transform(throwable)
    } catch (e: Throwable) {
      failure(e)
    }
  }

public inline fun <A, B> Result<A>.redeemWith(
  handleErrorWith: (throwable: Throwable) -> Result<B>,
  transform: (value: A) -> Result<B>
): Result<B> =
  fold({ a ->
    try {
      transform(a)
    } catch (e: Throwable) {
      failure(e)
    }
  }) { throwable ->
    try {
      handleErrorWith(throwable)
    } catch (e: Throwable) {
      failure(e)
    }
  }

public inline fun <A, B, C> Result<A>.zip(b: Result<B>, map: (A, B) -> C): Result<C> =
  zip(
    b,
    unitResult,
    unitResult,
    unitResult,
    unitResult,
    unitResult,
    unitResult,
    unitResult,
    unitResult
  ) { a, b, _, _, _, _, _, _, _, _ -> map(a, b) }

public inline fun <A, B, C, D> Result<A>.zip(b: Result<B>, c: Result<C>, map: (A, B, C) -> D): Result<D> =
  zip(
    b,
    c,
    unitResult,
    unitResult,
    unitResult,
    unitResult,
    unitResult,
    unitResult,
    unitResult
  ) { a, b, c, _, _, _, _, _, _, _ -> map(a, b, c) }

public inline fun <A, B, C, D, E> Result<A>.zip(
  b: Result<B>,
  c: Result<C>,
  d: Result<D>,
  map: (A, B, C, D) -> E
): Result<E> =
  zip(
    b,
    c,
    d,
    unitResult,
    unitResult,
    unitResult,
    unitResult,
    unitResult,
    unitResult
  ) { a, b, c, d, _, _, _, _, _, _ -> map(a, b, c, d) }

public inline fun <A, B, C, D, E, F> Result<A>.zip(
  b: Result<B>,
  c: Result<C>,
  d: Result<D>,
  e: Result<E>,
  map: (A, B, C, D, E) -> F
): Result<F> =
  zip(b, c, d, e, unitResult, unitResult, unitResult, unitResult, unitResult) { a, b, c, d, e, f, _, _, _, _ ->
    map(
      a,
      b,
      c,
      d,
      e
    )
  }

public inline fun <A, B, C, D, E, F, G> Result<A>.zip(
  b: Result<B>,
  c: Result<C>,
  d: Result<D>,
  e: Result<E>,
  f: Result<F>,
  map: (A, B, C, D, E, F) -> G
): Result<G> =
  zip(b, c, d, e, f, unitResult, unitResult, unitResult, unitResult) { a, b, c, d, e, f, _, _, _, _ ->
    map(
      a,
      b,
      c,
      d,
      e,
      f
    )
  }

public inline fun <A, B, C, D, E, F, G, H> Result<A>.zip(
  b: Result<B>,
  c: Result<C>,
  d: Result<D>,
  e: Result<E>,
  f: Result<F>,
  g: Result<G>,
  map: (A, B, C, D, E, F, G) -> H
): Result<H> =
  zip(b, c, d, e, f, g, unitResult, unitResult, unitResult) { a, b, c, d, e, f, g, _, _, _ -> map(a, b, c, d, e, f, g) }

public inline fun <A, B, C, D, E, F, G, H, I> Result<A>.zip(
  b: Result<B>,
  c: Result<C>,
  d: Result<D>,
  e: Result<E>,
  f: Result<F>,
  g: Result<G>,
  h: Result<H>,
  map: (A, B, C, D, E, F, G, H) -> I
): Result<I> =
  zip(b, c, d, e, f, g, h, unitResult, unitResult) { a, b, c, d, e, f, g, h, _, _ -> map(a, b, c, d, e, f, g, h) }

public inline fun <A, B, C, D, E, F, G, H, I, J> Result<A>.zip(
  b: Result<B>,
  c: Result<C>,
  d: Result<D>,
  e: Result<E>,
  f: Result<F>,
  g: Result<G>,
  h: Result<H>,
  i: Result<I>,
  map: (A, B, C, D, E, F, G, H, I) -> J
): Result<J> =
  zip(b, c, d, e, f, g, h, i, unitResult) { a, b, c, d, e, f, g, h, i, _ -> map(a, b, c, d, e, f, g, h, i) }

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
  map: (A, B, C, D, E, F, G, H, I, J) -> K
): Result<K> = Nullable.zip(
  getOrNull(),
  b.getOrNull(),
  c.getOrNull(),
  d.getOrNull(),
  e.getOrNull(),
  f.getOrNull(),
  g.getOrNull(),
  h.getOrNull(),
  i.getOrNull(),
  j.getOrNull(),
  map
)?.let { success(it) } ?: composeErrors(
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

@PublishedApi
internal fun composeErrors(vararg other: Throwable?): Throwable? {
  var a: Throwable? = null
  other.forEach { b ->
    when {
      a == null -> a = b
      b != null -> a?.addSuppressed(b)
      else -> Unit
    }
  }
  return a
}
