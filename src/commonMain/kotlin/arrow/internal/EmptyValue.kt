package arrow.internal

@PublishedApi
internal object EmptyValue {
  @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
  public inline fun <A> unbox(value: Any?): A = if (value === this) null as A else value as A
}
