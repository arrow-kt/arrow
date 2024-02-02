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
public object EmptyValue {
  @Suppress("UNCHECKED_CAST")
  public inline fun <A> unbox(value: Any?): A =
    fold(value, { null as A }, ::identity)

  public inline fun <T> combine(first: Any?, second: T, combine: (T, T) -> T): T =
    fold(first, { second }, { t: T -> combine(t, second) })

  @Suppress("UNCHECKED_CAST")
  public inline fun <T, R> fold(value: Any?, ifEmpty: () -> R, ifNotEmpty: (T) -> R): R =
    if (value === EmptyValue) ifEmpty() else ifNotEmpty(value as T)
}

/**
 * Like [Semigroup.maybeCombine] but for using with [EmptyValue]
 */
@PublishedApi
@Deprecated(SemigroupDeprecation, ReplaceWith("EmptyValue.combine(first, second) { x, y -> x.combine(y) }", "arrow.core.EmptyValue"))
internal fun <T> Semigroup<T>.emptyCombine(first: Any?, second: T): T =
  EmptyValue.combine(first, second) { x, y -> x.combine(y) }
