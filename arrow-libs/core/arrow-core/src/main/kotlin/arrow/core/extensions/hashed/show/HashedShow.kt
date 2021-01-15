package arrow.core.extensions.hashed.show

import arrow.core.Hashed.Companion
import arrow.core.extensions.HashedShow
import arrow.typeclasses.Show
import kotlin.Suppress

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A> Companion.show(SA: Show<A>): HashedShow<A> = object :
    arrow.core.extensions.HashedShow<A> { override fun SA(): arrow.typeclasses.Show<A> = SA }
