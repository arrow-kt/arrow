package arrow.core.extensions.const.show

import arrow.core.Const.Companion
import arrow.core.extensions.ConstShow
import arrow.typeclasses.Show
import arrow.typeclasses.ShowDeprecation
import kotlin.Suppress

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(ShowDeprecation)
inline fun <A, T> Companion.show(SA: Show<A>): ConstShow<A, T> = object :
  arrow.core.extensions.ConstShow<A, T> { override fun SA(): arrow.typeclasses.Show<A> = SA }
