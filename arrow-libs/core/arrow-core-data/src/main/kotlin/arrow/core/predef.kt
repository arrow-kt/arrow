package arrow.core

inline fun <A> identity(a: A): A = a

inline fun <A, B, Z> ((A, B) -> Z).curry(): (A) -> (B) -> Z = { p1: A -> { p2: B -> this(p1, p2) } }

infix fun <A, B, C> ((B) -> C).compose(f: (A) -> B): (A) -> C =
  AndThen(this).compose(f)

infix fun <A, B, C> ((A) -> B).andThen(g: (B) -> C): (A) -> C =
  AndThen(this).andThen(g)

internal object ArrowCoreInternalException : RuntimeException(
  "Arrow-Core internal error. Please let us know and create a ticket at https://github.com/arrow-kt/arrow-core/issues/new/choose",
  null
) {
  override fun fillInStackTrace(): Throwable = this
}
