package arrow.core.extensions.tuple7.show

import arrow.core.Tuple7.Companion
import arrow.core.extensions.Tuple7Show
import arrow.typeclasses.Show
import kotlin.Suppress

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "Show.tuple7(SA, SB, SC, SD, SE, SF, SG)",
    "arrow.core.Show",
    "arrow.core.tuple7"
  ),
  DeprecationLevel.WARNING
)
inline fun <A, B, C, D, E, F, G> Companion.show(
  SA: Show<A>,
  SB: Show<B>,
  SC: Show<C>,
  SD: Show<D>,
  SE: Show<E>,
  SF: Show<F>,
  SG: Show<G>
): Tuple7Show<A, B, C, D, E, F, G> = object : arrow.core.extensions.Tuple7Show<A, B, C, D, E, F, G>
    { override fun SA(): arrow.typeclasses.Show<A> = SA

  override fun SB(): arrow.typeclasses.Show<B> = SB

  override fun SC(): arrow.typeclasses.Show<C> = SC

  override fun SD(): arrow.typeclasses.Show<D> = SD

  override fun SE(): arrow.typeclasses.Show<E> = SE

  override fun SF(): arrow.typeclasses.Show<F> = SF

  override fun SG(): arrow.typeclasses.Show<G> = SG }
