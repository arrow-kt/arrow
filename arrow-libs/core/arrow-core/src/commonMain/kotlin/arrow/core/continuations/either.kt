package arrow.core.continuations

import arrow.core.Either
import kotlin.experimental.ExperimentalTypeInference

@Suppress("ClassName")
public object either {
  public inline fun <E, A> eager(noinline f: suspend EagerShift<E>.() -> A): Either<E, A> =
    eagerEffect(f).toEither()
  
  @OptIn(ExperimentalTypeInference::class)
  public suspend operator fun <E, A> invoke(@BuilderInference f: suspend Shift<E>.() -> A): Either<E, A> =
    effect(f).toEither()
}
