package arrow.core.extensions.nonemptylist.show

import arrow.core.NonEmptyList.Companion
import arrow.core.extensions.NonEmptyListShow
import arrow.typeclasses.Show
import arrow.typeclasses.ShowDeprecation
import kotlin.Suppress

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(ShowDeprecation)
inline fun <A> Companion.show(SA: Show<A>): NonEmptyListShow<A> = object :
  arrow.core.extensions.NonEmptyListShow<A> { override fun SA(): arrow.typeclasses.Show<A> = SA }
