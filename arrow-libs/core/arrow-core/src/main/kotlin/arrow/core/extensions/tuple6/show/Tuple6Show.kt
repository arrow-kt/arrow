package arrow.core.extensions.tuple6.show

import arrow.core.Tuple6.Companion
import arrow.core.extensions.Tuple6Show
import arrow.typeclasses.Show
import kotlin.Suppress

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A, B, C, D, E, F> Companion.show(
  SA: Show<A>,
  SB: Show<B>,
  SC: Show<C>,
  SD: Show<D>,
  SE: Show<E>,
  SF: Show<F>
): Tuple6Show<A, B, C, D, E, F> = object : arrow.core.extensions.Tuple6Show<A, B, C, D, E, F> {
    override fun SA(): arrow.typeclasses.Show<A> = SA

  override fun SB(): arrow.typeclasses.Show<B> = SB

  override fun SC(): arrow.typeclasses.Show<C> = SC

  override fun SD(): arrow.typeclasses.Show<D> = SD

  override fun SE(): arrow.typeclasses.Show<E> = SE

  override fun SF(): arrow.typeclasses.Show<F> = SF }
