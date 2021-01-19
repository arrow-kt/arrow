package arrow.core.extensions.tuple10.show

import arrow.core.Tuple10.Companion
import arrow.core.extensions.Tuple10Show
import arrow.typeclasses.Show
import kotlin.Suppress

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "Show.tuple10(SA, SB, SC, SD, SE, SF, SG, SH, SI, SJ)",
    "arrow.core.Show",
    "arrow.core.tuple10"
  ),
  DeprecationLevel.WARNING
)
inline fun <A, B, C, D, E, F, G, H, I, J> Companion.show(
  SA: Show<A>,
  SB: Show<B>,
  SC: Show<C>,
  SD: Show<D>,
  SE: Show<E>,
  SF: Show<F>,
  SG: Show<G>,
  SH: Show<H>,
  SI: Show<I>,
  SJ: Show<J>
): Tuple10Show<A, B, C, D, E, F, G, H, I, J> = object : arrow.core.extensions.Tuple10Show<A, B, C,
    D, E, F, G, H, I, J> { override fun SA(): arrow.typeclasses.Show<A> = SA

  override fun SB(): arrow.typeclasses.Show<B> = SB

  override fun SC(): arrow.typeclasses.Show<C> = SC

  override fun SD(): arrow.typeclasses.Show<D> = SD

  override fun SE(): arrow.typeclasses.Show<E> = SE

  override fun SF(): arrow.typeclasses.Show<F> = SF

  override fun SG(): arrow.typeclasses.Show<G> = SG

  override fun SH(): arrow.typeclasses.Show<H> = SH

  override fun SI(): arrow.typeclasses.Show<I> = SI

  override fun SJ(): arrow.typeclasses.Show<J> = SJ }
