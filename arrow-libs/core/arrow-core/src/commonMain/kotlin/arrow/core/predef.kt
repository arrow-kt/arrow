package arrow.core

@Suppress("NOTHING_TO_INLINE")
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
  inline fun <A> unbox(value: Any?): A =
    if (value === this) null as A else value as A

  @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
  public inline fun <T> combine(first: Any?, second: T, combine: (T, T) -> T): T =
    if (first === this) second else combine(first as T, second)

  @Suppress("UNCHECKED_CAST")
  inline fun <T, R> fold(value: Any?, ifEmpty: () -> R, ifNotEmpty: (T) -> R): R =
    if (value === this) ifEmpty() else ifNotEmpty(value as T)
}
