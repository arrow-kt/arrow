package arrow.core.extensions.tuple4.show

import arrow.core.Tuple4.Companion
import arrow.core.extensions.Tuple4Show
import arrow.typeclasses.Show
import kotlin.Suppress

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "Show.tuple4(SA, SB, SC, SD)",
    "arrow.core.Show",
    "arrow.core.tuple4"
  ),
  DeprecationLevel.WARNING
)
inline fun <A, B, C, D> Companion.show(
  SA: Show<A>,
  SB: Show<B>,
  SC: Show<C>,
  SD: Show<D>
): Tuple4Show<A, B, C, D> = object : arrow.core.extensions.Tuple4Show<A, B, C, D> { override fun
    SA(): arrow.typeclasses.Show<A> = SA

  override fun SB(): arrow.typeclasses.Show<B> = SB

  override fun SC(): arrow.typeclasses.Show<C> = SC

  override fun SD(): arrow.typeclasses.Show<D> = SD }
