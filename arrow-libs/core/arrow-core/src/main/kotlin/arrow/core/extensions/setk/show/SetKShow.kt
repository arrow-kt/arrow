package arrow.core.extensions.setk.show

import arrow.core.SetK.Companion
import arrow.core.extensions.SetKShow
import arrow.typeclasses.Show
import arrow.typeclasses.ShowDeprecation
import kotlin.Suppress

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(ShowDeprecation)
inline fun <A> Companion.show(SA: Show<A>): SetKShow<A> = object : arrow.core.extensions.SetKShow<A> {
  override fun SA(): arrow.typeclasses.Show<A> = SA
}
