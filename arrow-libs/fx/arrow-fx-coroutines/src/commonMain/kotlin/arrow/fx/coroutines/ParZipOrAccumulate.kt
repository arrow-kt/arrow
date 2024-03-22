package arrow.fx.coroutines

import arrow.core.NonEmptyList
import arrow.core.raise.Raise
import arrow.core.raise.zipOrAccumulate
import arrow.fx.coroutines.FailureValue.bindNel
import arrow.fx.coroutines.FailureValue.mightFail
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineScope

//region 2-arity
public suspend inline fun <E, A, B, C> Raise<E>.parZipOrAccumulate(
  crossinline combine: (E, E) -> E,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline f: suspend CoroutineScope.(A, B) -> C
): C =
  parZipOrAccumulate(EmptyCoroutineContext, combine, fa, fb, f)

public suspend inline fun <E, A, B, C> Raise<E>.parZipOrAccumulate(
  context: CoroutineContext,
  crossinline combine: (E, E) -> E,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline f: suspend CoroutineScope.(A, B) -> C
): C =
  parZip(
    context,
    { mightFail { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fb(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b ->
    zipOrAccumulate(combine, { bindNel<E, A>(a) }, { bindNel<E, B>(b) }) { aa, bb -> f(aa, bb) }
  }

public suspend inline fun <E, A, B, C> Raise<NonEmptyList<E>>.parZipOrAccumulate(
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline f: suspend CoroutineScope.(A, B) -> C
): C =
  parZipOrAccumulate(EmptyCoroutineContext, fa, fb, f)

public suspend inline fun <E, A, B, C> Raise<NonEmptyList<E>>.parZipOrAccumulate(
  context: CoroutineContext,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline f: suspend CoroutineScope.(A, B) -> C
): C =
  parZip(
    context,
    { mightFail { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fb(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b ->
    zipOrAccumulate({ bindNel<E, A>(a) }, { bindNel<E, B>(b) }) { aa, bb -> f(aa, bb) }
  }
//endregion

//region 3-arity
public suspend inline fun <E, A, B, C, D> Raise<E>.parZipOrAccumulate(
  crossinline combine: (E, E) -> E,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline f: suspend CoroutineScope.(A, B, C) -> D
): D =
  parZipOrAccumulate(EmptyCoroutineContext, combine, fa, fb, fc, f)

public suspend inline fun <E, A, B, C, D> Raise<E>.parZipOrAccumulate(
  context: CoroutineContext,
  crossinline combine: (E, E) -> E,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline f: suspend CoroutineScope.(A, B, C) -> D
): D =
  parZip(
    context,
    { mightFail { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fc(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b, c ->
    zipOrAccumulate(combine, { bindNel<E, A>(a) }, { bindNel<E, B>(b) }, { bindNel<E, C>(c) }) { aa, bb, cc -> f(aa, bb, cc) }
  }

public suspend inline fun <E, A, B, C, D> Raise<NonEmptyList<E>>.parZipOrAccumulate(
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline f: suspend CoroutineScope.(A, B, C) -> D
): D =
  parZipOrAccumulate(EmptyCoroutineContext, fa, fb, fc, f)

public suspend inline fun <E, A, B, C, D> Raise<NonEmptyList<E>>.parZipOrAccumulate(
  context: CoroutineContext,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline f: suspend CoroutineScope.(A, B, C) -> D
): D =
  parZip(
    context,
    { mightFail { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fc(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b, c ->
    zipOrAccumulate(
      { bindNel<E, A>(a) },
      { bindNel<E, B>(b) },
      { bindNel<E, C>(c) }
    ) { aa, bb, cc -> f(aa, bb, cc) }
  }
//endregion

//region 4-arity
public suspend inline fun <E, A, B, C, D, F> Raise<E>.parZipOrAccumulate(
  crossinline combine: (E, E) -> E,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline fd: suspend ScopedRaiseAccumulate<E>.() -> D,
  crossinline f: suspend CoroutineScope.(A, B, C, D) -> F
): F =
  parZipOrAccumulate(EmptyCoroutineContext, combine, fa, fb, fc, fd, f)

public suspend inline fun <E, A, B, C, D, F> Raise<E>.parZipOrAccumulate(
  context: CoroutineContext,
  crossinline combine: (E, E) -> E,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline fd: suspend ScopedRaiseAccumulate<E>.() -> D,
  crossinline f: suspend CoroutineScope.(A, B, C, D) -> F
): F =
  parZip(
    context,
    { mightFail { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fc(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fd(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b, c, d ->
    zipOrAccumulate(
      combine,
      { bindNel<E, A>(a) },
      { bindNel<E, B>(b) },
      { bindNel<E, C>(c) },
      { bindNel<E, D>(d) }
    ) { aa, bb, cc, dd -> f(aa, bb, cc, dd) }
  }

public suspend inline fun <E, A, B, C, D, F> Raise<NonEmptyList<E>>.parZipOrAccumulate(
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline fd: suspend ScopedRaiseAccumulate<E>.() -> D,
  crossinline f: suspend CoroutineScope.(A, B, C, D) -> F
): F =
  parZipOrAccumulate(EmptyCoroutineContext, fa, fb, fc, fd, f)

public suspend inline fun <E, A, B, C, D, F> Raise<NonEmptyList<E>>.parZipOrAccumulate(
  context: CoroutineContext,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline fd: suspend ScopedRaiseAccumulate<E>.() -> D,
  crossinline f: suspend CoroutineScope.(A, B, C, D) -> F
): F =
  parZip(
    context,
    { mightFail { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fc(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fd(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b, c, d ->
    zipOrAccumulate(
      { bindNel<E, A>(a) },
      { bindNel<E, B>(b) },
      { bindNel<E, C>(c) },
      { bindNel<E, D>(d) }
    ) { aa, bb, cc, dd -> f(aa, bb, cc, dd) }
  }
//endregion

//region 5-arity
public suspend inline fun <E, A, B, C, D, F, G> Raise<E>.parZipOrAccumulate(
  crossinline combine: (E, E) -> E,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline fd: suspend ScopedRaiseAccumulate<E>.() -> D,
  crossinline ff: suspend ScopedRaiseAccumulate<E>.() -> F,
  crossinline f: suspend CoroutineScope.(A, B, C, D, F) -> G
): G =
  parZipOrAccumulate(EmptyCoroutineContext, combine, fa, fb, fc, fd, ff, f)

public suspend inline fun <E, A, B, C, D, F, G> Raise<E>.parZipOrAccumulate(
  context: CoroutineContext,
  crossinline combine: (E, E) -> E,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline fd: suspend ScopedRaiseAccumulate<E>.() -> D,
  crossinline ff: suspend ScopedRaiseAccumulate<E>.() -> F,
  crossinline f: suspend CoroutineScope.(A, B, C, D, F) -> G
): G =
  parZip(
    context,
    { mightFail { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fc(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fd(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { ff(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b, c, d, f ->
    zipOrAccumulate(
      combine,
      { bindNel<E, A>(a) },
      { bindNel<E, B>(b) },
      { bindNel<E, C>(c) },
      { bindNel<E, D>(d) },
      { bindNel<E, F>(f) }
    ) { aa, bb, cc, dd, ff -> f(aa, bb, cc, dd, ff) }
  }

public suspend inline fun <E, A, B, C, D, F, G> Raise<NonEmptyList<E>>.parZipOrAccumulate(
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline fd: suspend ScopedRaiseAccumulate<E>.() -> D,
  crossinline ff: suspend ScopedRaiseAccumulate<E>.() -> F,
  crossinline f: suspend CoroutineScope.(A, B, C, D, F) -> G
): G =
  parZipOrAccumulate(EmptyCoroutineContext, fa, fb, fc, fd, ff, f)

public suspend inline fun <E, A, B, C, D, F, G> Raise<NonEmptyList<E>>.parZipOrAccumulate(
  context: CoroutineContext,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline fd: suspend ScopedRaiseAccumulate<E>.() -> D,
  crossinline ff: suspend ScopedRaiseAccumulate<E>.() -> F,
  crossinline f: suspend CoroutineScope.(A, B, C, D, F) -> G
): G =
  parZip(
    context,
    { mightFail { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fc(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fd(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { ff(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b, c, d, f ->
    zipOrAccumulate(
      { bindNel<E, A>(a) },
      { bindNel<E, B>(b) },
      { bindNel<E, C>(c) },
      { bindNel<E, D>(d) },
      { bindNel<E, F>(f) }
    ) { aa, bb, cc, dd, ff -> f(aa, bb, cc, dd, ff) }
  }
//endregion

//region 6-arity
public suspend inline fun <E, A, B, C, D, F, G, H> Raise<E>.parZipOrAccumulate(
  crossinline combine: (E, E) -> E,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline fd: suspend ScopedRaiseAccumulate<E>.() -> D,
  crossinline ff: suspend ScopedRaiseAccumulate<E>.() -> F,
  crossinline fg: suspend ScopedRaiseAccumulate<E>.() -> G,
  crossinline f: suspend CoroutineScope.(A, B, C, D, F, G) -> H
): H =
  parZipOrAccumulate(EmptyCoroutineContext, combine, fa, fb, fc, fd, ff, fg, f)

public suspend inline fun <E, A, B, C, D, F, G, H> Raise<E>.parZipOrAccumulate(
  context: CoroutineContext,
  crossinline combine: (E, E) -> E,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline fd: suspend ScopedRaiseAccumulate<E>.() -> D,
  crossinline ff: suspend ScopedRaiseAccumulate<E>.() -> F,
  crossinline fg: suspend ScopedRaiseAccumulate<E>.() -> G,
  crossinline f: suspend CoroutineScope.(A, B, C, D, F, G) -> H
): H =
  parZip(
    context,
    { mightFail { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fc(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fd(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { ff(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fg(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b, c, d, f, g ->
    zipOrAccumulate(
      combine,
      { bindNel<E, A>(a) },
      { bindNel<E, B>(b) },
      { bindNel<E, C>(c) },
      { bindNel<E, D>(d) },
      { bindNel<E, F>(f) },
      { bindNel<E, G>(g) }
    ) { aa, bb, cc, dd, ff, gg -> f(aa, bb, cc, dd, ff, gg) }
  }

public suspend inline fun <E, A, B, C, D, F, G, H> Raise<NonEmptyList<E>>.parZipOrAccumulate(
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline fd: suspend ScopedRaiseAccumulate<E>.() -> D,
  crossinline ff: suspend ScopedRaiseAccumulate<E>.() -> F,
  crossinline fg: suspend ScopedRaiseAccumulate<E>.() -> G,
  crossinline f: suspend CoroutineScope.(A, B, C, D, F, G) -> H
): H =
  parZipOrAccumulate(EmptyCoroutineContext, fa, fb, fc, fd, ff, fg, f)

public suspend inline fun <E, A, B, C, D, F, G, H> Raise<NonEmptyList<E>>.parZipOrAccumulate(
  context: CoroutineContext,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline fd: suspend ScopedRaiseAccumulate<E>.() -> D,
  crossinline ff: suspend ScopedRaiseAccumulate<E>.() -> F,
  crossinline fg: suspend ScopedRaiseAccumulate<E>.() -> G,
  crossinline f: suspend CoroutineScope.(A, B, C, D, F, G) -> H
): H =
  parZip(
    context,
    { mightFail { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fc(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fd(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { ff(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fg(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b, c, d, f, g ->
    zipOrAccumulate(
      { bindNel<E, A>(a) },
      { bindNel<E, B>(b) },
      { bindNel<E, C>(c) },
      { bindNel<E, D>(d) },
      { bindNel<E, F>(f) },
      { bindNel<E, G>(g) }
    ) { aa, bb, cc, dd, ff, gg -> f(aa, bb, cc, dd, ff, gg) }
  }
//endregion

//region 7-arity
public suspend inline fun <E, A, B, C, D, F, G, H, I> Raise<E>.parZipOrAccumulate(
  crossinline combine: (E, E) -> E,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline fd: suspend ScopedRaiseAccumulate<E>.() -> D,
  crossinline ff: suspend ScopedRaiseAccumulate<E>.() -> F,
  crossinline fg: suspend ScopedRaiseAccumulate<E>.() -> G,
  crossinline fh: suspend ScopedRaiseAccumulate<E>.() -> H,
  crossinline f: suspend CoroutineScope.(A, B, C, D, F, G, H) -> I
): I =
  parZipOrAccumulate(EmptyCoroutineContext, combine, fa, fb, fc, fd, ff, fg, fh, f)

public suspend inline fun <E, A, B, C, D, F, G, H, I> Raise<E>.parZipOrAccumulate(
  context: CoroutineContext,
  crossinline combine: (E, E) -> E,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline fd: suspend ScopedRaiseAccumulate<E>.() -> D,
  crossinline ff: suspend ScopedRaiseAccumulate<E>.() -> F,
  crossinline fg: suspend ScopedRaiseAccumulate<E>.() -> G,
  crossinline fh: suspend ScopedRaiseAccumulate<E>.() -> H,
  crossinline f: suspend CoroutineScope.(A, B, C, D, F, G, H) -> I
): I =
  parZip(
    context,
    { mightFail { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fc(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fd(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { ff(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fg(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fh(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b, c, d, f, g, h ->
    zipOrAccumulate(
      combine,
      { bindNel<E, A>(a) },
      { bindNel<E, B>(b) },
      { bindNel<E, C>(c) },
      { bindNel<E, D>(d) },
      { bindNel<E, F>(f) },
      { bindNel<E, G>(g) },
      { bindNel<E, H>(h) }
    ) { aa, bb, cc, dd, ff, gg, hh -> f(aa, bb, cc, dd, ff, gg, hh) }
  }

public suspend inline fun <E, A, B, C, D, F, G, H, I> Raise<NonEmptyList<E>>.parZipOrAccumulate(
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline fd: suspend ScopedRaiseAccumulate<E>.() -> D,
  crossinline ff: suspend ScopedRaiseAccumulate<E>.() -> F,
  crossinline fg: suspend ScopedRaiseAccumulate<E>.() -> G,
  crossinline fh: suspend ScopedRaiseAccumulate<E>.() -> H,
  crossinline f: suspend CoroutineScope.(A, B, C, D, F, G, H) -> I
): I =
  parZipOrAccumulate(EmptyCoroutineContext, fa, fb, fc, fd, ff, fg, fh, f)

public suspend inline fun <E, A, B, C, D, F, G, H, I> Raise<NonEmptyList<E>>.parZipOrAccumulate(
  context: CoroutineContext,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline fd: suspend ScopedRaiseAccumulate<E>.() -> D,
  crossinline ff: suspend ScopedRaiseAccumulate<E>.() -> F,
  crossinline fg: suspend ScopedRaiseAccumulate<E>.() -> G,
  crossinline fh: suspend ScopedRaiseAccumulate<E>.() -> H,
  crossinline f: suspend CoroutineScope.(A, B, C, D, F, G, H) -> I
): I =
  parZip(
    context,
    { mightFail { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fc(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fd(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { ff(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fg(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fh(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b, c, d, f, g, h ->
    zipOrAccumulate(
      { bindNel<E, A>(a) },
      { bindNel<E, B>(b) },
      { bindNel<E, C>(c) },
      { bindNel<E, D>(d) },
      { bindNel<E, F>(f) },
      { bindNel<E, G>(g) },
      { bindNel<E, H>(h) }
    ) { aa, bb, cc, dd, ff, gg, hh -> f(aa, bb, cc, dd, ff, gg, hh) }
  }
//endregion

//region 8-arity
public suspend inline fun <E, A, B, C, D, F, G, H, I, J> Raise<E>.parZipOrAccumulate(
  crossinline combine: (E, E) -> E,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline fd: suspend ScopedRaiseAccumulate<E>.() -> D,
  crossinline ff: suspend ScopedRaiseAccumulate<E>.() -> F,
  crossinline fg: suspend ScopedRaiseAccumulate<E>.() -> G,
  crossinline fh: suspend ScopedRaiseAccumulate<E>.() -> H,
  crossinline fi: suspend ScopedRaiseAccumulate<E>.() -> I,
  crossinline f: suspend CoroutineScope.(A, B, C, D, F, G, H, I) -> J
): J =
  parZipOrAccumulate(EmptyCoroutineContext, combine, fa, fb, fc, fd, ff, fg, fh, fi, f)

public suspend inline fun <E, A, B, C, D, F, G, H, I, J> Raise<E>.parZipOrAccumulate(
  context: CoroutineContext,
  crossinline combine: (E, E) -> E,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline fd: suspend ScopedRaiseAccumulate<E>.() -> D,
  crossinline ff: suspend ScopedRaiseAccumulate<E>.() -> F,
  crossinline fg: suspend ScopedRaiseAccumulate<E>.() -> G,
  crossinline fh: suspend ScopedRaiseAccumulate<E>.() -> H,
  crossinline fi: suspend ScopedRaiseAccumulate<E>.() -> I,
  crossinline f: suspend CoroutineScope.(A, B, C, D, F, G, H, I) -> J
): J =
  parZip(
    context,
    { mightFail { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fc(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fd(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { ff(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fg(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fh(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fi(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b, c, d, f, g, h, i ->
    zipOrAccumulate(
      combine,
      { bindNel<E, A>(a) },
      { bindNel<E, B>(b) },
      { bindNel<E, C>(c) },
      { bindNel<E, D>(d) },
      { bindNel<E, F>(f) },
      { bindNel<E, G>(g) },
      { bindNel<E, H>(h) },
      { bindNel<E, I>(i) }
    ) { aa, bb, cc, dd, ff, gg, hh, ii -> f(aa, bb, cc, dd, ff, gg, hh, ii) }
  }

public suspend inline fun <E, A, B, C, D, F, G, H, I, J> Raise<NonEmptyList<E>>.parZipOrAccumulate(
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline fd: suspend ScopedRaiseAccumulate<E>.() -> D,
  crossinline ff: suspend ScopedRaiseAccumulate<E>.() -> F,
  crossinline fg: suspend ScopedRaiseAccumulate<E>.() -> G,
  crossinline fh: suspend ScopedRaiseAccumulate<E>.() -> H,
  crossinline fi: suspend ScopedRaiseAccumulate<E>.() -> I,
  crossinline f: suspend CoroutineScope.(A, B, C, D, F, G, H, I) -> J
): J =
  parZipOrAccumulate(EmptyCoroutineContext, fa, fb, fc, fd, ff, fg, fh, fi, f)

public suspend inline fun <E, A, B, C, D, F, G, H, I, J> Raise<NonEmptyList<E>>.parZipOrAccumulate(
  context: CoroutineContext,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline fd: suspend ScopedRaiseAccumulate<E>.() -> D,
  crossinline ff: suspend ScopedRaiseAccumulate<E>.() -> F,
  crossinline fg: suspend ScopedRaiseAccumulate<E>.() -> G,
  crossinline fh: suspend ScopedRaiseAccumulate<E>.() -> H,
  crossinline fi: suspend ScopedRaiseAccumulate<E>.() -> I,
  crossinline f: suspend CoroutineScope.(A, B, C, D, F, G, H, I) -> J
): J =
  parZip(
    context,
    { mightFail { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fc(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fd(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { ff(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fg(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fh(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fi(ScopedRaiseAccumulate(this, this@parZip)) } },
  ) { a, b, c, d, f, g, h, i ->
    zipOrAccumulate(
      { bindNel<E, A>(a) },
      { bindNel<E, B>(b) },
      { bindNel<E, C>(c) },
      { bindNel<E, D>(d) },
      { bindNel<E, F>(f) },
      { bindNel<E, G>(g) },
      { bindNel<E, H>(h) },
      { bindNel<E, I>(i) }
    ) { aa, bb, cc, dd, ff, gg, hh, ii -> f(aa, bb, cc, dd, ff, gg, hh, ii) }
  }
//endregion

//region 9-arity
public suspend inline fun <E, A, B, C, D, F, G, H, I, J, K> Raise<E>.parZipOrAccumulate(
  crossinline combine: (E, E) -> E,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline fd: suspend ScopedRaiseAccumulate<E>.() -> D,
  crossinline ff: suspend ScopedRaiseAccumulate<E>.() -> F,
  crossinline fg: suspend ScopedRaiseAccumulate<E>.() -> G,
  crossinline fh: suspend ScopedRaiseAccumulate<E>.() -> H,
  crossinline fi: suspend ScopedRaiseAccumulate<E>.() -> I,
  crossinline fj: suspend ScopedRaiseAccumulate<E>.() -> J,
  crossinline f: suspend CoroutineScope.(A, B, C, D, F, G, H, I, J) -> K
): K =
  parZipOrAccumulate(EmptyCoroutineContext, combine, fa, fb, fc, fd, ff, fg, fh, fi, fj, f)

public suspend inline fun <E, A, B, C, D, F, G, H, I, J, K> Raise<E>.parZipOrAccumulate(
  context: CoroutineContext,
  crossinline combine: (E, E) -> E,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline fd: suspend ScopedRaiseAccumulate<E>.() -> D,
  crossinline ff: suspend ScopedRaiseAccumulate<E>.() -> F,
  crossinline fg: suspend ScopedRaiseAccumulate<E>.() -> G,
  crossinline fh: suspend ScopedRaiseAccumulate<E>.() -> H,
  crossinline fi: suspend ScopedRaiseAccumulate<E>.() -> I,
  crossinline fj: suspend ScopedRaiseAccumulate<E>.() -> J,
  crossinline f: suspend CoroutineScope.(A, B, C, D, F, G, H, I, J) -> K
): K =
  parZip(
    context,
    { mightFail { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fc(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fd(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { ff(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fg(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fh(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fi(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fj(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b, c, d, f, g, h, i, j ->
    zipOrAccumulate(
      combine,
      { bindNel<E, A>(a) },
      { bindNel<E, B>(b) },
      { bindNel<E, C>(c) },
      { bindNel<E, D>(d) },
      { bindNel<E, F>(f) },
      { bindNel<E, G>(g) },
      { bindNel<E, H>(h) },
      { bindNel<E, I>(i) },
      { bindNel<E, J>(j) }
    ) { aa, bb, cc, dd, ff, gg, hh, ii, jj -> f(aa, bb, cc, dd, ff, gg, hh, ii, jj) }
  }

public suspend inline fun <E, A, B, C, D, F, G, H, I, J, K> Raise<NonEmptyList<E>>.parZipOrAccumulate(
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline fd: suspend ScopedRaiseAccumulate<E>.() -> D,
  crossinline ff: suspend ScopedRaiseAccumulate<E>.() -> F,
  crossinline fg: suspend ScopedRaiseAccumulate<E>.() -> G,
  crossinline fh: suspend ScopedRaiseAccumulate<E>.() -> H,
  crossinline fi: suspend ScopedRaiseAccumulate<E>.() -> I,
  crossinline fj: suspend ScopedRaiseAccumulate<E>.() -> J,
  crossinline f: suspend CoroutineScope.(A, B, C, D, F, G, H, I, J) -> K
): K =
  parZipOrAccumulate(EmptyCoroutineContext, fa, fb, fc, fd, ff, fg, fh, fi, fj, f)

public suspend inline fun <E, A, B, C, D, F, G, H, I, J, K> Raise<NonEmptyList<E>>.parZipOrAccumulate(
  context: CoroutineContext,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline fd: suspend ScopedRaiseAccumulate<E>.() -> D,
  crossinline ff: suspend ScopedRaiseAccumulate<E>.() -> F,
  crossinline fg: suspend ScopedRaiseAccumulate<E>.() -> G,
  crossinline fh: suspend ScopedRaiseAccumulate<E>.() -> H,
  crossinline fi: suspend ScopedRaiseAccumulate<E>.() -> I,
  crossinline fj: suspend ScopedRaiseAccumulate<E>.() -> J,
  crossinline f: suspend CoroutineScope.(A, B, C, D, F, G, H, I, J) -> K
): K =
  parZip(
    context,
    { mightFail { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fc(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fd(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { ff(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fg(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fh(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fi(ScopedRaiseAccumulate(this, this@parZip)) } },
    { mightFail { fj(ScopedRaiseAccumulate(this, this@parZip)) } },
  ) { a, b, c, d, f, g, h, i, j ->
    zipOrAccumulate(
      { bindNel<E, A>(a) },
      { bindNel<E, B>(b) },
      { bindNel<E, C>(c) },
      { bindNel<E, D>(d) },
      { bindNel<E, F>(f) },
      { bindNel<E, G>(g) },
      { bindNel<E, H>(h) },
      { bindNel<E, I>(i) },
      { bindNel<E, J>(j) }
    ) { aa, bb, cc, dd, ff, gg, hh, ii, jj -> f(aa, bb, cc, dd, ff, gg, hh, ii, jj) }
  }
//endregion
