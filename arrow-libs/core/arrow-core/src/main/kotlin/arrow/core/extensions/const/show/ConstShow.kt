package arrow.core.extensions.const.show

import arrow.core.Const.Companion
import arrow.core.extensions.ConstShow
import arrow.typeclasses.Show
import kotlin.Suppress

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A, T> Companion.show(SA: Show<A>): ConstShow<A, T> = object :
    arrow.core.extensions.ConstShow<A, T> { override fun SA(): arrow.typeclasses.Show<A> = SA }
