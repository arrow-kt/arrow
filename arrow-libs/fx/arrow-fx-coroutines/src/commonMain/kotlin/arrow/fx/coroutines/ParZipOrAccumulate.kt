package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.raise.either
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineScope

public suspend inline fun <E, A, B, C> parZipOrAccumulate(
  combine: (E, E) -> E,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline f: suspend CoroutineScope.(A, B) -> C
): Either<E, C> =
  parZipOrAccumulate(EmptyCoroutineContext, combine, fa, fb, f)

public suspend inline fun <E, A, B, C> parZipOrAccumulate(
  context: CoroutineContext,
  combine: (E, E) -> E,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline f: suspend CoroutineScope.(A, B) -> C
): Either<E, C> =
  parZipOrAccumulate(context, fa, fb, f).mapLeft { it.reduce(combine) }

public suspend inline fun <E, A, B, C> parZipOrAccumulate(
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline f: suspend CoroutineScope.(A, B) -> C
): Either<NonEmptyList<E>, C> =
  parZipOrAccumulate(EmptyCoroutineContext, fa, fb, f)

public suspend inline fun <E, A, B, C> parZipOrAccumulate(
  context: CoroutineContext,
  crossinline fa: suspend ScopedRaiseAccumulate<E>.() -> A,
  crossinline fb: suspend ScopedRaiseAccumulate<E>.() -> B,
  crossinline f: suspend CoroutineScope.(A, B) -> C
): Either<NonEmptyList<E>, C> =
  parZip(
    context,
    { either { fa(ScopedRaiseAccumulate(this, this@parZip)) } },
    { either { fb(ScopedRaiseAccumulate(this, this@parZip)) } }
  ) { a, b ->
    Either.zipOrAccumulate(a, b) { aa, bb -> f(aa, bb) }
  }
