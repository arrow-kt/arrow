package arrow.core

import arrow.syntax.function.curried
import arrow.syntax.function.andThen as AndThen
import arrow.syntax.function.compose as Compose

inline fun <A> identity(a: A): A = a

@Deprecated(
  "Use curried from the arrow.syntax.* package",
  ReplaceWith(
    "this.curried()",
    "arrow.syntax.function.curried"
  )
)
inline fun <A, B, Z> ((A, B) -> Z).curry(): (A) -> (B) -> Z =
  this.curried()

@Deprecated(
  "Use compose from the arrow.syntax.* package",
  ReplaceWith(
    "this.compose(f)",
    "arrow.syntax.function.compose"
  )
)
infix fun <A, B, C> ((B) -> C).compose(f: (A) -> B): (A) -> C =
  this Compose f

@Deprecated(
  "Use andThen from the arrow.syntax.* package",
  ReplaceWith(
    "this.andThen(g)",
    "arrow.syntax.function.andThen"
  )
)
infix fun <A, B, C> ((A) -> B).andThen(g: (B) -> C): (A) -> C =
  this AndThen g

internal object ArrowCoreInternalException : RuntimeException(
  "Arrow-Core internal error. Please let us know and create a ticket at https://github.com/arrow-kt/arrow-core/issues/new/choose",
  null
) {
  override fun fillInStackTrace(): Throwable = this
}
