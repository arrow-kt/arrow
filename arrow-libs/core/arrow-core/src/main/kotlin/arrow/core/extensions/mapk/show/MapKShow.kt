package arrow.core.extensions.mapk.show

import arrow.core.MapK.Companion
import arrow.core.extensions.MapKShow
import arrow.typeclasses.Show
import arrow.typeclasses.ShowDeprecation
import kotlin.Suppress

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(ShowDeprecation)
inline fun <K, A> Companion.show(SK: Show<K>, SA: Show<A>): MapKShow<K, A> = object :
    arrow.core.extensions.MapKShow<K, A> { override fun SK(): arrow.typeclasses.Show<K> = SK

  override fun SA(): arrow.typeclasses.Show<A> = SA }
