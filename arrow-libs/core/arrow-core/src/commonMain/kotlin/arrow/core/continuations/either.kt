package arrow.core.continuations

import arrow.core.Either

@Suppress("ClassName")
public object either {
  public inline fun <E, A> eager(noinline f: suspend EagerEffectScope<E>.() -> A): Either<E, A> =
    eagerEffect(f).toEither()

  public suspend operator fun <E, A> invoke(f: suspend Shift<E>.() -> A): Either<E, A> =
    effect(f).toEither()
}
