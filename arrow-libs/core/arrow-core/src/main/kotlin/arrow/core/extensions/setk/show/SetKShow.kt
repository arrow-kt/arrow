package arrow.core.extensions.setk.show

import arrow.core.SetK.Companion
import arrow.core.extensions.SetKShow
import arrow.typeclasses.Show
import kotlin.Suppress

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "Show.set<A>(SA)",
    "arrow.core.set",
    "arrow.typeclasses.Show"
  ),
  DeprecationLevel.WARNING
)
inline fun <A> Companion.show(SA: Show<A>): SetKShow<A> = object : arrow.core.extensions.SetKShow<A> {
  override fun SA(): arrow.typeclasses.Show<A> = SA
}
