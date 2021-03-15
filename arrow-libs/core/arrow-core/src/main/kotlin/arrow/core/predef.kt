package arrow.core

inline fun <A> identity(a: A): A = a

internal object ArrowCoreInternalException : RuntimeException(
  "Arrow-Core internal error. Please let us know and create a ticket at https://github.com/arrow-kt/arrow-core/issues/new/choose",
  null
) {
  override fun fillInStackTrace(): Throwable = this
}
