package arrow

import arrow.core.nonFatalOrThrow

@PublishedApi
@Deprecated(
  message = "Moved to common arrow-exception-utils module",
  replaceWith = ReplaceWith("this.nonFatalOrThrow()", "arrow.core.nonFatalOrThrow"),
  level = DeprecationLevel.HIDDEN
)
internal fun Throwable.throwIfFatal(): Throwable = this.nonFatalOrThrow()
