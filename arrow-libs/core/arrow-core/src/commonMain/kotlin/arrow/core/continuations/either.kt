package arrow.core.continuations

import arrow.core.Either

@Deprecated(eitherDSLDeprecation, ReplaceWith("either", "arrow.core.raise.either"))
@Suppress("ClassName")
public object either {
  @Deprecated(eitherDSLDeprecation, ReplaceWith("either(f)", "arrow.core.raise.either"))
  public inline fun <E, A> eager(noinline f: suspend EagerEffectScope<E>.() -> A): Either<E, A> =
    eagerEffect(f).toEither()

  @Deprecated(eitherDSLDeprecation, ReplaceWith("either(f)", "arrow.core.raise.either"))
  public suspend operator fun <E, A> invoke(f: suspend EffectScope<E>.() -> A): Either<E, A> =
    effect(f).toEither()
}

private const val eitherDSLDeprecation =
  "The either DSL has been moved to arrow.core.raise.either.\n" +
    "Replace import arrow.core.computations.either with arrow.core.raise.either"
