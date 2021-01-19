package arrow.core.extensions.tuple5.show

import arrow.core.Tuple5.Companion
import arrow.core.extensions.Tuple5Show
import arrow.typeclasses.Show
import kotlin.Suppress

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "Show.tuple5(SA, SB, SC, SD, SE)",
    "arrow.core.Show",
    "arrow.core.tuple5"
  ),
  DeprecationLevel.WARNING
)
inline fun <A, B, C, D, E> Companion.show(
  SA: Show<A>,
  SB: Show<B>,
  SC: Show<C>,
  SD: Show<D>,
  SE: Show<E>
): Tuple5Show<A, B, C, D, E> = object : arrow.core.extensions.Tuple5Show<A, B, C, D, E> { override
    fun SA(): arrow.typeclasses.Show<A> = SA

  override fun SB(): arrow.typeclasses.Show<B> = SB

  override fun SC(): arrow.typeclasses.Show<C> = SC

  override fun SD(): arrow.typeclasses.Show<D> = SD

  override fun SE(): arrow.typeclasses.Show<E> = SE }
