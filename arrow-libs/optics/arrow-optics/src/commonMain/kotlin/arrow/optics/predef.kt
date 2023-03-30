package arrow.optics

@Suppress("ClassName")
internal object EMPTY_VALUE {
  @Suppress("UNCHECKED_CAST")
  inline fun <T> unbox(value: Any?): T =
    if (value === this) null as T else value as T
}
