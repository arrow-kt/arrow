package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.getOrElse
import arrow.core.raise.Raise
import arrow.core.raise.either
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
    { either { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fb(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b ->
    Either.zipOrAccumulate(a, b) { aa, bb -> f(aa, bb) }.getOrElse { raise(it.reduce(combine)) }
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
    { either { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fb(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b ->
    Either.zipOrAccumulate(a, b) { aa, bb -> f(aa, bb) }.bind()
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
    { either { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fc(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b, c ->
    Either.zipOrAccumulate(a, b, c) { aa, bb, cc -> f(aa, bb, cc) }.getOrElse { raise(it.reduce(combine)) }
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
    { either { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fc(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b, c ->
    Either.zipOrAccumulate(a, b, c) { aa, bb, cc -> f(aa, bb, cc) }.bind()
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
    { either { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fc(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fd(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b, c, d ->
    Either.zipOrAccumulate(a, b, c, d) { aa, bb, cc, dd -> f(aa, bb, cc, dd) }.getOrElse { raise(it.reduce(combine)) }
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
    { either { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fc(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fd(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b, c, d ->
    Either.zipOrAccumulate(a, b, c, d) { aa, bb, cc, dd -> f(aa, bb, cc, dd) }.bind()
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
    { either { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fc(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fd(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { ff(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b, c, d, f ->
    Either.zipOrAccumulate(a, b, c, d, f) { aa, bb, cc, dd, ff -> f(aa, bb, cc, dd, ff) }.getOrElse { raise(it.reduce(combine)) }
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
    { either { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fc(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fd(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { ff(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b, c, d, f ->
    Either.zipOrAccumulate(a, b, c, d, f) { aa, bb, cc, dd, ff -> f(aa, bb, cc, dd, ff) }.bind()
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
    { either { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fc(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fd(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { ff(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fg(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b, c, d, f, g ->
    Either.zipOrAccumulate(a, b, c, d, f, g) { aa, bb, cc, dd, ff, gg -> f(aa, bb, cc, dd, ff, gg) }.getOrElse { raise(it.reduce(combine)) }
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
    { either { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fc(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fd(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { ff(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fg(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b, c, d, f, g ->
    Either.zipOrAccumulate(a, b, c, d, f, g) { aa, bb, cc, dd, ff, gg -> f(aa, bb, cc, dd, ff, gg) }.bind()
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
    { either { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fc(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fd(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { ff(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fg(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fh(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b, c, d, f, g, h ->
    Either.zipOrAccumulate(a, b, c, d, f, g, h) { aa, bb, cc, dd, ff, gg, hh -> f(aa, bb, cc, dd, ff, gg, hh) }.getOrElse { raise(it.reduce(combine)) }
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
    { either { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fc(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fd(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { ff(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fg(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fh(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b, c, d, f, g, h ->
    Either.zipOrAccumulate(a, b, c, d, f, g, h) { aa, bb, cc, dd, ff, gg, hh -> f(aa, bb, cc, dd, ff, gg, hh) }.bind()
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
    { either { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fc(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fd(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { ff(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fg(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fh(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fi(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b, c, d, f, g, h, i ->
    Either.zipOrAccumulate(a, b, c, d, f, g, h, i) { aa, bb, cc, dd, ff, gg, hh, ii -> f(aa, bb, cc, dd, ff, gg, hh, ii) }.getOrElse { raise(it.reduce(combine)) }
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
    { either { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fc(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fd(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { ff(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fg(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fh(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fi(ScopedRaiseAccumulate(this, this@parZip)) } },
  ) { a, b, c, d, f, g, h, i ->
    Either.zipOrAccumulate(a, b, c, d, f, g, h, i) { aa, bb, cc, dd, ff, gg, hh, ii -> f(aa, bb, cc, dd, ff, gg, hh, ii) }.bind()
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
    { either { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fc(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fd(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { ff(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fg(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fh(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fi(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fj(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b, c, d, f, g, h, i, j ->
    Either.zipOrAccumulate(a, b, c, d, f, g, h, i, j) { aa, bb, cc, dd, ff, gg, hh, ii, jj -> f(aa, bb, cc, dd, ff, gg, hh, ii, jj) }.getOrElse { raise(it.reduce(combine)) }
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
    { either { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fc(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fd(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { ff(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fg(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fh(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fi(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fj(ScopedRaiseAccumulate(this, this@parZip)) } },
  ) { a, b, c, d, f, g, h, i, j ->
    Either.zipOrAccumulate(a, b, c, d, f, g, h, i, j) { aa, bb, cc, dd, ff, gg, hh, ii, jj -> f(aa, bb, cc, dd, ff, gg, hh, ii, jj) }.bind()
  }
//endregion
