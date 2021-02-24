package arrow.core.extensions.either.show

import arrow.core.Either.Companion
import arrow.core.extensions.EitherShow
import arrow.typeclasses.Show
import arrow.typeclasses.ShowDeprecation
import kotlin.Suppress

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(ShowDeprecation)
inline fun <L, R> Companion.show(SL: Show<L>, SR: Show<R>): EitherShow<L, R> = object :
  arrow.core.extensions.EitherShow<L, R> {
  override fun SL(): arrow.typeclasses.Show<L> = SL

  override fun SR(): arrow.typeclasses.Show<R> = SR
}
