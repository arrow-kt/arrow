package arrow.core.extensions.id.show

import arrow.core.Id.Companion
import arrow.core.extensions.IdShow
import arrow.typeclasses.Show
import kotlin.Suppress

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A> Companion.show(SA: Show<A>): IdShow<A> = object : arrow.core.extensions.IdShow<A> {
    override fun SA(): arrow.typeclasses.Show<A> = SA }
