package arrow.core

inline fun <A> identity(a: A): A = a

inline fun <A, B, Z> ((A, B) -> Z).curry(): (A) -> (B) -> Z = { p1: A -> { p2: B -> this(p1, p2) } }

inline infix fun <A, B, C> ((B) -> C).compose(crossinline f: (A) -> B): (A) -> C = { a: A -> this(f(a)) }

inline infix fun <A, B, C> ((A) -> B).andThen(crossinline g: (B) -> C): (A) -> C = { a: A -> g(this(a)) }

internal object ArrowCoreInternalException : RuntimeException(
  "Arrow-Core internal error. Please let us know and create a ticket at https://github.com/arrow-kt/arrow-core/issues/new/choose",
  null
) {
  override fun fillInStackTrace(): Throwable = this
}
