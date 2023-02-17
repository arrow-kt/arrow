package arrow.core

import arrow.typeclasses.Semigroup
import arrow.typeclasses.SemigroupDeprecation

public inline fun <A> identity(a: A): A = a

/**
 * This is a work-around for having nested nulls in generic code.
 * This allows for writing faster generic code instead of using `Option`.
 * This is only used as an optimisation technique in low-level code,
 * always prefer to use `Option` in actual business code when needed in generic code.
 */
@PublishedApi
internal object EmptyValue {
  @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
  public inline fun <A> unbox(value: Any?): A =
    if (value === this) null as A else value as A

  public inline fun <T> combine(first: Any?, second: T, combine: (T, T) -> T): T =
    if (first === EmptyValue) second else combine(first as T, second)
}

/**
 * Like [Semigroup.maybeCombine] but for using with [EmptyValue]
 */
@PublishedApi
@Deprecated(SemigroupDeprecation, ReplaceWith("emptyCombine(first, second) { x, y -> x.combine(y) }"))
internal fun <T> Semigroup<T>.emptyCombine(first: Any?, second: T): T =
  emptyCombine(first, second) { x, y -> x.combine(y) }

/**
 * Apply the [combine] function if [first] is not empty, otherwise return [second].
 */
@PublishedApi
internal fun <T> emptyCombine(first: Any?, second: T, combine: (T, T) -> T): T =
  if (first == EmptyValue) second else combine(first as T, second)
