package arrow.core.extensions.tuple3.show

import arrow.core.Tuple3.Companion
import arrow.core.extensions.Tuple3Show
import arrow.typeclasses.Show
import kotlin.Suppress

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A, B, C> Companion.show(
  SA: Show<A>,
  SB: Show<B>,
  SC: Show<C>
): Tuple3Show<A, B, C> = object : arrow.core.extensions.Tuple3Show<A, B, C> { override fun SA():
    arrow.typeclasses.Show<A> = SA

  override fun SB(): arrow.typeclasses.Show<B> = SB

  override fun SC(): arrow.typeclasses.Show<C> = SC }
