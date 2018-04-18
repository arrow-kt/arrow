package arrow.optics.syntax

import arrow.core.Option
import arrow.optics.nonePrism
import arrow.optics.somePrism

/**
 * Focus into the optional [arrow.core.Some].
 */
inline val <S, T> BoundSetter<S, Option<T>>.some: BoundSetter<S, T>
  get() = this.compose(somePrism())

/**
 * Focus into the optional [arrow.core.None].
 */
inline val <S, T> BoundSetter<S, Option<T>>.none: BoundSetter<S, Unit>
  get() = this.compose(nonePrism())