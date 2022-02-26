package arrow.core.continuations

import arrow.core.Either

@Suppress("ClassName")
public object either {
  public inline fun <E, A> eager(crossinline f: suspend EagerEffectScope<E>.() -> A): Either<E, A> =
    eagerEffect(f).toEither()

  public suspend inline operator fun <E, A> invoke(crossinline f: suspend EffectScope<E>.() -> A): Either<E, A> =
    effect(f).toEither()
}
