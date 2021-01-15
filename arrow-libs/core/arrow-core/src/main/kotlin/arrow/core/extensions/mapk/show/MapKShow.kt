package arrow.core.extensions.mapk.show

import arrow.core.MapK.Companion
import arrow.core.extensions.MapKShow
import arrow.typeclasses.Show
import kotlin.Suppress

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("Show.map(SK, SA)", "arrow.core.Show", "arrow.core.map"))
inline fun <K, A> Companion.show(SK: Show<K>, SA: Show<A>): MapKShow<K, A> = object :
    arrow.core.extensions.MapKShow<K, A> { override fun SK(): arrow.typeclasses.Show<K> = SK

  override fun SA(): arrow.typeclasses.Show<A> = SA }
