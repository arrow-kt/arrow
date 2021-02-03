package arrow.core.extensions.sequencek.show

import arrow.core.SequenceK.Companion
import arrow.core.extensions.SequenceKShow
import arrow.typeclasses.Show
import arrow.typeclasses.ShowDeprecation
import kotlin.Suppress

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(ShowDeprecation)
inline fun <A> Companion.show(SA: Show<A>): SequenceKShow<A> = object :
    arrow.core.extensions.SequenceKShow<A> { override fun SA(): arrow.typeclasses.Show<A> = SA }
