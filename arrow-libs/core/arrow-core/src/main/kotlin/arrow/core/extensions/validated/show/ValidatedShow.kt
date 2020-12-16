package arrow.core.extensions.validated.show

import arrow.core.Validated.Companion
import arrow.core.extensions.ValidatedShow
import arrow.typeclasses.Show
import kotlin.Suppress

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Validated.show(SL, SR)", "arrow.core.show"))
inline fun <L, R> Companion.show(SL: Show<L>, SR: Show<R>): ValidatedShow<L, R> = object :
    arrow.core.extensions.ValidatedShow<L, R> { override fun SL(): arrow.typeclasses.Show<L> = SL

  override fun SR(): arrow.typeclasses.Show<R> = SR }
