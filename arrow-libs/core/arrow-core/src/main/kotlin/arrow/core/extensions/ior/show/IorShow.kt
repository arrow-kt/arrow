package arrow.core.extensions.ior.show

import arrow.core.Ior.Companion
import arrow.core.extensions.IorShow
import arrow.typeclasses.Show
import arrow.typeclasses.ShowDeprecation

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(ShowDeprecation)
inline fun <L, R> Companion.show(SL: Show<L>, SR: Show<R>): IorShow<L, R> = object :
    arrow.core.extensions.IorShow<L, R> { override fun SL(): arrow.typeclasses.Show<L> = SL

  override fun SR(): arrow.typeclasses.Show<R> = SR }
