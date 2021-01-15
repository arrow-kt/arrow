package arrow.core.extensions.tuple9.show

import arrow.core.Tuple9.Companion
import arrow.core.extensions.Tuple9Show
import arrow.typeclasses.Show
import kotlin.Suppress

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A, B, C, D, E, F, G, H, I> Companion.show(
  SA: Show<A>,
  SB: Show<B>,
  SC: Show<C>,
  SD: Show<D>,
  SE: Show<E>,
  SF: Show<F>,
  SG: Show<G>,
  SH: Show<H>,
  SI: Show<I>
): Tuple9Show<A, B, C, D, E, F, G, H, I> = object : arrow.core.extensions.Tuple9Show<A, B, C, D, E,
    F, G, H, I> { override fun SA(): arrow.typeclasses.Show<A> = SA

  override fun SB(): arrow.typeclasses.Show<B> = SB

  override fun SC(): arrow.typeclasses.Show<C> = SC

  override fun SD(): arrow.typeclasses.Show<D> = SD

  override fun SE(): arrow.typeclasses.Show<E> = SE

  override fun SF(): arrow.typeclasses.Show<F> = SF

  override fun SG(): arrow.typeclasses.Show<G> = SG

  override fun SH(): arrow.typeclasses.Show<H> = SH

  override fun SI(): arrow.typeclasses.Show<I> = SI }
