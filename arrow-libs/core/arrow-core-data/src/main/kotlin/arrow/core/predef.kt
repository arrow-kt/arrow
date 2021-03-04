package arrow.core

inline fun <A> identity(a: A): A = a

@Deprecated(
  "Use curried from the arrow.syntax.* package",
  ReplaceWith(
    "this.curried()",
    "arrow.syntax.curried"
  )
)
inline fun <A, B, Z> ((A, B) -> Z).curry(): (A) -> (B) -> Z = { p1: A -> { p2: B -> this(p1, p2) } }

@Deprecated(
  "Use compose from the arrow.syntax.* package",
  ReplaceWith(
    "this.compose(f)",
    "arrow.syntax.compose"
  )
)
infix fun <A, B, C> ((B) -> C).compose(f: (A) -> B): (A) -> C =
  AndThen(this).compose(f)

@Deprecated(
  "Use andThen from the arrow.syntax.* package",
  ReplaceWith(
    "this.andThen(g)",
    "arrow.syntax.andThen"
  )
)
infix fun <A, B, C> ((A) -> B).andThen(g: (B) -> C): (A) -> C =
  AndThen(this).andThen(g)

internal object ArrowCoreInternalException : RuntimeException(
  "Arrow-Core internal error. Please let us know and create a ticket at https://github.com/arrow-kt/arrow-core/issues/new/choose",
  null
) {
  override fun fillInStackTrace(): Throwable = this
}
