@file:OptIn(ExperimentalContracts::class)

package arrow.fx.coroutines

import arrow.core.NonEmptyList
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.zipOrAccumulate
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

//region 2-arity
public suspend inline fun <E, A, B, C> Raise<E>.parZipOrAccumulate(
  crossinline combine: (E, E) -> E,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline transform: suspend CoroutineScope.(A, B) -> C
): C {
  contract {
    callsInPlace(fa, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
    callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
  }
  return parZipOrAccumulate(EmptyCoroutineContext, combine, fa, fb, transform)
}

public suspend inline fun <E, A, B, C> Raise<E>.parZipOrAccumulate(
  context: CoroutineContext,
  crossinline combine: (E, E) -> E,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline transform: suspend CoroutineScope.(A, B) -> C
): C {
  contract {
    callsInPlace(fa, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
    callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
  }
  return parZip(
    context,
    { either { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fb(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b ->
    zipOrAccumulate(combine, { a.bindNel() }, { b.bindNel() }) { aa, bb -> transform(aa, bb) }
  }
}

public suspend inline fun <E, A, B, C> Raise<NonEmptyList<E>>.parZipOrAccumulate(
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline transform: suspend CoroutineScope.(A, B) -> C
): C {
  contract {
    callsInPlace(fa, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
    callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
  }
  return parZipOrAccumulate(EmptyCoroutineContext, fa, fb, transform)
}

public suspend inline fun <E, A, B, C> Raise<NonEmptyList<E>>.parZipOrAccumulate(
  context: CoroutineContext,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline transform: suspend CoroutineScope.(A, B) -> C
): C {
  contract {
    callsInPlace(fa, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
    callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
  }
  return parZip(
    context,
    { either { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fb(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b ->
    zipOrAccumulate({ a.bindNel() }, { b.bindNel() }) { aa, bb -> transform(aa, bb) }
  }
}
//endregion

//region 3-arity
public suspend inline fun <E, A, B, C, D> Raise<E>.parZipOrAccumulate(
  crossinline combine: (E, E) -> E,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline transform: suspend CoroutineScope.(A, B, C) -> D
): D {
  contract {
    callsInPlace(fa, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fc, InvocationKind.AT_MOST_ONCE)
    callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
  }
  return parZipOrAccumulate(EmptyCoroutineContext, combine, fa, fb, fc, transform)
}

public suspend inline fun <E, A, B, C, D> Raise<E>.parZipOrAccumulate(
  context: CoroutineContext,
  crossinline combine: (E, E) -> E,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline transform: suspend CoroutineScope.(A, B, C) -> D
): D {
  contract {
    // Contract is valid because for D to be returned, transform must be called
    // with A, B, C, and hence fa, fb, fc must be called.
    callsInPlace(fa, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fc, InvocationKind.AT_MOST_ONCE)
    callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
  }
  return parZip(
    context,
    { either { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fc(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b, c ->
    zipOrAccumulate(combine, { a.bindNel() }, { b.bindNel() }, { c.bindNel() }) { aa, bb, cc -> transform(aa, bb, cc) }
  }
}

public suspend inline fun <E, A, B, C, D> Raise<NonEmptyList<E>>.parZipOrAccumulate(
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline transform: suspend CoroutineScope.(A, B, C) -> D
): D {
  contract {
    callsInPlace(fa, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fc, InvocationKind.AT_MOST_ONCE)
    callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
  }
  return parZipOrAccumulate(EmptyCoroutineContext, fa, fb, fc, transform)
}

public suspend inline fun <E, A, B, C, D> Raise<NonEmptyList<E>>.parZipOrAccumulate(
  context: CoroutineContext,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline transform: suspend CoroutineScope.(A, B, C) -> D
): D {
  contract {
    callsInPlace(fa, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fc, InvocationKind.AT_MOST_ONCE)
    callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
  }
  return parZip(
    context,
    { either { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fc(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b, c ->
    zipOrAccumulate({ a.bindNel() }, { b.bindNel() }, { c.bindNel() }) { aa, bb, cc -> transform(aa, bb, cc) }
  }
}
//endregion

//region 4-arity
public suspend inline fun <E, A, B, C, D, F> Raise<E>.parZipOrAccumulate(
  crossinline combine: (E, E) -> E,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline fd: suspend ScopedRaiseAccumulate<E>.() -> D,
  crossinline transform: suspend CoroutineScope.(A, B, C, D) -> F
): F {
  contract {
    callsInPlace(fa, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fc, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fd, InvocationKind.AT_MOST_ONCE)
    callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
  }
  return parZipOrAccumulate(EmptyCoroutineContext, combine, fa, fb, fc, fd, transform)
}

public suspend inline fun <E, A, B, C, D, F> Raise<E>.parZipOrAccumulate(
  context: CoroutineContext,
  crossinline combine: (E, E) -> E,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline fd: suspend ScopedRaiseAccumulate<E>.() -> D,
  crossinline transform: suspend CoroutineScope.(A, B, C, D) -> F
): F {
  contract {
    callsInPlace(fa, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fc, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fd, InvocationKind.AT_MOST_ONCE)
    callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
  }
  return parZip(
    context,
    { either { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fc(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fd(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b, c, d ->
    zipOrAccumulate(combine, { a.bindNel() }, { b.bindNel() }, { c.bindNel() }, { d.bindNel() }) { aa, bb, cc, dd -> transform(aa, bb, cc, dd) }
  }
}

public suspend inline fun <E, A, B, C, D, F> Raise<NonEmptyList<E>>.parZipOrAccumulate(
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline fd: suspend ScopedRaiseAccumulate<E>.() -> D,
  crossinline transform: suspend CoroutineScope.(A, B, C, D) -> F
): F {
  contract {
    callsInPlace(fa, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fc, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fd, InvocationKind.AT_MOST_ONCE)
    callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
  }
  return parZipOrAccumulate(EmptyCoroutineContext, fa, fb, fc, fd, transform)
}

public suspend inline fun <E, A, B, C, D, F> Raise<NonEmptyList<E>>.parZipOrAccumulate(
  context: CoroutineContext,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline fd: suspend ScopedRaiseAccumulate<E>.() -> D,
  crossinline transform: suspend CoroutineScope.(A, B, C, D) -> F
): F {
  contract {
    callsInPlace(fa, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fc, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fd, InvocationKind.AT_MOST_ONCE)
    callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
  }
  return parZip(
    context,
    { either { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fc(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fd(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b, c, d ->
    zipOrAccumulate({ a.bindNel() }, { b.bindNel() }, { c.bindNel() }, { d.bindNel() }) { aa, bb, cc, dd -> transform(aa, bb, cc, dd) }
  }
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
  crossinline transform: suspend CoroutineScope.(A, B, C, D, F) -> G
): G {
  contract {
    callsInPlace(fa, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fc, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fd, InvocationKind.AT_MOST_ONCE)
    callsInPlace(ff, InvocationKind.AT_MOST_ONCE)
    callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
  }
  return parZipOrAccumulate(EmptyCoroutineContext, combine, fa, fb, fc, fd, ff, transform)
}

public suspend inline fun <E, A, B, C, D, F, G> Raise<E>.parZipOrAccumulate(
  context: CoroutineContext,
  crossinline combine: (E, E) -> E,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline fd: suspend ScopedRaiseAccumulate<E>.() -> D,
  crossinline ff: suspend ScopedRaiseAccumulate<E>.() -> F,
  crossinline transform: suspend CoroutineScope.(A, B, C, D, F) -> G
): G {
  contract {
    callsInPlace(fa, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fc, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fd, InvocationKind.AT_MOST_ONCE)
    callsInPlace(ff, InvocationKind.AT_MOST_ONCE)
    callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
  }
  return parZip(
    context,
    { either { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fc(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fd(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { ff(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b, c, d, f ->
    zipOrAccumulate(combine, { a.bindNel() }, { b.bindNel() }, { c.bindNel() }, { d.bindNel() }, { f.bindNel() }) { aa, bb, cc, dd, ff -> transform(aa, bb, cc, dd, ff) }
  }
}

public suspend inline fun <E, A, B, C, D, F, G> Raise<NonEmptyList<E>>.parZipOrAccumulate(
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline fd: suspend ScopedRaiseAccumulate<E>.() -> D,
  crossinline ff: suspend ScopedRaiseAccumulate<E>.() -> F,
  crossinline transform: suspend CoroutineScope.(A, B, C, D, F) -> G
): G {
  contract {
    callsInPlace(fa, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fc, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fd, InvocationKind.AT_MOST_ONCE)
    callsInPlace(ff, InvocationKind.AT_MOST_ONCE)
    callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
  }
  return parZipOrAccumulate(EmptyCoroutineContext, fa, fb, fc, fd, ff, transform)
}

public suspend inline fun <E, A, B, C, D, F, G> Raise<NonEmptyList<E>>.parZipOrAccumulate(
  context: CoroutineContext,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline fd: suspend ScopedRaiseAccumulate<E>.() -> D,
  crossinline ff: suspend ScopedRaiseAccumulate<E>.() -> F,
  crossinline transform: suspend CoroutineScope.(A, B, C, D, F) -> G
): G {
  contract {
    callsInPlace(fa, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fc, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fd, InvocationKind.AT_MOST_ONCE)
    callsInPlace(ff, InvocationKind.AT_MOST_ONCE)
    callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
  }
  return parZip(
    context,
    { either { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fc(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fd(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { ff(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b, c, d, f ->
    zipOrAccumulate({ a.bindNel() }, { b.bindNel() }, { c.bindNel() }, { d.bindNel() }, { f.bindNel() }) { aa, bb, cc, dd, ff -> transform(aa, bb, cc, dd, ff) }
  }
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
  crossinline transform: suspend CoroutineScope.(A, B, C, D, F, G) -> H
): H {
  contract {
    callsInPlace(fa, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fc, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fd, InvocationKind.AT_MOST_ONCE)
    callsInPlace(ff, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fg, InvocationKind.AT_MOST_ONCE)
    callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
  }
  return parZipOrAccumulate(EmptyCoroutineContext, combine, fa, fb, fc, fd, ff, fg, transform)
}

public suspend inline fun <E, A, B, C, D, F, G, H> Raise<E>.parZipOrAccumulate(
  context: CoroutineContext,
  crossinline combine: (E, E) -> E,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline fd: suspend ScopedRaiseAccumulate<E>.() -> D,
  crossinline ff: suspend ScopedRaiseAccumulate<E>.() -> F,
  crossinline fg: suspend ScopedRaiseAccumulate<E>.() -> G,
  crossinline transform: suspend CoroutineScope.(A, B, C, D, F, G) -> H
): H {
  contract {
    callsInPlace(fa, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fc, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fd, InvocationKind.AT_MOST_ONCE)
    callsInPlace(ff, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fg, InvocationKind.AT_MOST_ONCE)
    callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
  }
  return parZip(
    context,
    { either { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fc(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fd(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { ff(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fg(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b, c, d, f, g ->
    zipOrAccumulate(combine, { a.bindNel() }, { b.bindNel() }, { c.bindNel() }, { d.bindNel() }, { f.bindNel() }, { g.bindNel() }) { aa, bb, cc, dd, ff, gg -> transform(aa, bb, cc, dd, ff, gg) }
  }
}

public suspend inline fun <E, A, B, C, D, F, G, H> Raise<NonEmptyList<E>>.parZipOrAccumulate(
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline fd: suspend ScopedRaiseAccumulate<E>.() -> D,
  crossinline ff: suspend ScopedRaiseAccumulate<E>.() -> F,
  crossinline fg: suspend ScopedRaiseAccumulate<E>.() -> G,
  crossinline transform: suspend CoroutineScope.(A, B, C, D, F, G) -> H
): H {
  contract {
    callsInPlace(fa, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fc, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fd, InvocationKind.AT_MOST_ONCE)
    callsInPlace(ff, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fg, InvocationKind.AT_MOST_ONCE)
    callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
  }
  return parZipOrAccumulate(EmptyCoroutineContext, fa, fb, fc, fd, ff, fg, transform)
}

public suspend inline fun <E, A, B, C, D, F, G, H> Raise<NonEmptyList<E>>.parZipOrAccumulate(
  context: CoroutineContext,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline fd: suspend ScopedRaiseAccumulate<E>.() -> D,
  crossinline ff: suspend ScopedRaiseAccumulate<E>.() -> F,
  crossinline fg: suspend ScopedRaiseAccumulate<E>.() -> G,
  crossinline transform: suspend CoroutineScope.(A, B, C, D, F, G) -> H
): H {
  contract {
    callsInPlace(fa, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fc, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fd, InvocationKind.AT_MOST_ONCE)
    callsInPlace(ff, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fg, InvocationKind.AT_MOST_ONCE)
    callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
  }
  return parZip(
    context,
    { either { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fc(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fd(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { ff(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fg(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b, c, d, f, g ->
    zipOrAccumulate({ a.bindNel() }, { b.bindNel() }, { c.bindNel() }, { d.bindNel() }, { f.bindNel() }, { g.bindNel() }) { aa, bb, cc, dd, ff, gg -> transform(aa, bb, cc, dd, ff, gg) }
  }
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
  crossinline transform: suspend CoroutineScope.(A, B, C, D, F, G, H) -> I
): I {
  contract {
    callsInPlace(fa, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fc, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fd, InvocationKind.AT_MOST_ONCE)
    callsInPlace(ff, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fg, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fh, InvocationKind.AT_MOST_ONCE)
    callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
  }
  return parZipOrAccumulate(EmptyCoroutineContext, combine, fa, fb, fc, fd, ff, fg, fh, transform)
}

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
  crossinline transform: suspend CoroutineScope.(A, B, C, D, F, G, H) -> I
): I {
  contract {
    callsInPlace(fa, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fc, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fd, InvocationKind.AT_MOST_ONCE)
    callsInPlace(ff, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fg, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fh, InvocationKind.AT_MOST_ONCE)
    callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
  }
  return parZip(
    context,
    { either { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fc(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fd(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { ff(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fg(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fh(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b, c, d, f, g, h ->
    zipOrAccumulate(combine, { a.bindNel() }, { b.bindNel() }, { c.bindNel() }, { d.bindNel() }, { f.bindNel() }, { g.bindNel() }, { h.bindNel() }) { aa, bb, cc, dd, ff, gg, hh -> transform(aa, bb, cc, dd, ff, gg, hh) }
  }
}

public suspend inline fun <E, A, B, C, D, F, G, H, I> Raise<NonEmptyList<E>>.parZipOrAccumulate(
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline fd: suspend ScopedRaiseAccumulate<E>.() -> D,
  crossinline ff: suspend ScopedRaiseAccumulate<E>.() -> F,
  crossinline fg: suspend ScopedRaiseAccumulate<E>.() -> G,
  crossinline fh: suspend ScopedRaiseAccumulate<E>.() -> H,
  crossinline transform: suspend CoroutineScope.(A, B, C, D, F, G, H) -> I
): I {
  contract {
    callsInPlace(fa, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fc, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fd, InvocationKind.AT_MOST_ONCE)
    callsInPlace(ff, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fg, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fh, InvocationKind.AT_MOST_ONCE)
    callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
  }
  return parZipOrAccumulate(EmptyCoroutineContext, fa, fb, fc, fd, ff, fg, fh, transform)
}

public suspend inline fun <E, A, B, C, D, F, G, H, I> Raise<NonEmptyList<E>>.parZipOrAccumulate(
  context: CoroutineContext,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline fc: suspend ScopedRaiseAccumulate<E>.() -> C,
  crossinline fd: suspend ScopedRaiseAccumulate<E>.() -> D,
  crossinline ff: suspend ScopedRaiseAccumulate<E>.() -> F,
  crossinline fg: suspend ScopedRaiseAccumulate<E>.() -> G,
  crossinline fh: suspend ScopedRaiseAccumulate<E>.() -> H,
  crossinline transform: suspend CoroutineScope.(A, B, C, D, F, G, H) -> I
): I {
  contract {
    callsInPlace(fa, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fc, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fd, InvocationKind.AT_MOST_ONCE)
    callsInPlace(ff, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fg, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fh, InvocationKind.AT_MOST_ONCE)
    callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
  }
  return parZip(
    context,
    { either { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fb(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fc(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fd(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { ff(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fg(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fh(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b, c, d, f, g, h ->
    zipOrAccumulate({ a.bindNel() }, { b.bindNel() }, { c.bindNel() }, { d.bindNel() }, { f.bindNel() }, { g.bindNel() }, { h.bindNel() }) { aa, bb, cc, dd, ff, gg, hh -> transform(aa, bb, cc, dd, ff, gg, hh) }
  }
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
  crossinline transform: suspend CoroutineScope.(A, B, C, D, F, G, H, I) -> J
): J {
  contract {
    callsInPlace(fa, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fc, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fd, InvocationKind.AT_MOST_ONCE)
    callsInPlace(ff, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fg, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fh, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fi, InvocationKind.AT_MOST_ONCE)
    callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
  }
  return parZipOrAccumulate(EmptyCoroutineContext, combine, fa, fb, fc, fd, ff, fg, fh, fi, transform)
}

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
  crossinline transform: suspend CoroutineScope.(A, B, C, D, F, G, H, I) -> J
): J {
  contract {
    callsInPlace(fa, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fc, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fd, InvocationKind.AT_MOST_ONCE)
    callsInPlace(ff, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fg, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fh, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fi, InvocationKind.AT_MOST_ONCE)
    callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
  }
  return parZip(
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
    zipOrAccumulate(combine, { a.bindNel() }, { b.bindNel() }, { c.bindNel() }, { d.bindNel() }, { f.bindNel() }, { g.bindNel() }, { h.bindNel() }, { i.bindNel() }) { aa, bb, cc, dd, ff, gg, hh, ii -> transform(aa, bb, cc, dd, ff, gg, hh, ii) }
  }
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
  crossinline transform: suspend CoroutineScope.(A, B, C, D, F, G, H, I) -> J
): J {
  contract {
    callsInPlace(fa, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fc, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fd, InvocationKind.AT_MOST_ONCE)
    callsInPlace(ff, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fg, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fh, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fi, InvocationKind.AT_MOST_ONCE)
    callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
  }
  return parZipOrAccumulate(EmptyCoroutineContext, fa, fb, fc, fd, ff, fg, fh, fi, transform)
}

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
  crossinline transform: suspend CoroutineScope.(A, B, C, D, F, G, H, I) -> J
): J {
  contract {
    callsInPlace(fa, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fc, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fd, InvocationKind.AT_MOST_ONCE)
    callsInPlace(ff, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fg, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fh, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fi, InvocationKind.AT_MOST_ONCE)
    callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
  }
  return parZip(
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
    zipOrAccumulate({ a.bindNel() }, { b.bindNel() }, { c.bindNel() }, { d.bindNel() }, { f.bindNel() }, { g.bindNel() }, { h.bindNel() }, { i.bindNel() }) { aa, bb, cc, dd, ff, gg, hh, ii -> transform(aa, bb, cc, dd, ff, gg, hh, ii) }
  }
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
  crossinline transform: suspend CoroutineScope.(A, B, C, D, F, G, H, I, J) -> K
): K {
  contract {
    callsInPlace(fa, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fc, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fd, InvocationKind.AT_MOST_ONCE)
    callsInPlace(ff, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fg, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fh, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fi, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fj, InvocationKind.AT_MOST_ONCE)
    callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
  }
  return parZipOrAccumulate(EmptyCoroutineContext, combine, fa, fb, fc, fd, ff, fg, fh, fi, fj, transform)
}

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
  crossinline transform: suspend CoroutineScope.(A, B, C, D, F, G, H, I, J) -> K
): K {
  contract {
    callsInPlace(fa, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fc, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fd, InvocationKind.AT_MOST_ONCE)
    callsInPlace(ff, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fg, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fh, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fi, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fj, InvocationKind.AT_MOST_ONCE)
    callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
  }
  return parZip(
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
    zipOrAccumulate(combine, { a.bindNel() }, { b.bindNel() }, { c.bindNel() }, { d.bindNel() }, { f.bindNel() }, { g.bindNel() }, { h.bindNel() }, { i.bindNel() }, { j.bindNel() }) { aa, bb, cc, dd, ff, gg, hh, ii, jj -> transform(aa, bb, cc, dd, ff, gg, hh, ii, jj) }
  }
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
  crossinline transform: suspend CoroutineScope.(A, B, C, D, F, G, H, I, J) -> K
): K {
  contract {
    callsInPlace(fa, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fc, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fd, InvocationKind.AT_MOST_ONCE)
    callsInPlace(ff, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fg, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fh, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fi, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fj, InvocationKind.AT_MOST_ONCE)
    callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
  }
  return parZipOrAccumulate(EmptyCoroutineContext, fa, fb, fc, fd, ff, fg, fh, fi, fj, transform)
}

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
  crossinline transform: suspend CoroutineScope.(A, B, C, D, F, G, H, I, J) -> K
): K {
  contract {
    callsInPlace(fa, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fc, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fd, InvocationKind.AT_MOST_ONCE)
    callsInPlace(ff, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fg, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fh, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fi, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fj, InvocationKind.AT_MOST_ONCE)
    callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
  }
  return parZip(
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
    zipOrAccumulate({ a.bindNel() }, { b.bindNel() }, { c.bindNel() }, { d.bindNel() }, { f.bindNel() }, { g.bindNel() }, { h.bindNel() }, { i.bindNel() }, { j.bindNel() }) { aa, bb, cc, dd, ff, gg, hh, ii, jj -> transform(aa, bb, cc, dd, ff, gg, hh, ii, jj) }
  }
}
//endregion
