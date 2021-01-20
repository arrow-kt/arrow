package arrow.core.extensions.nonemptylist.show

import arrow.core.NonEmptyList.Companion
import arrow.core.extensions.NonEmptyListShow
import arrow.typeclasses.Show
import kotlin.Suppress

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "@extension projected functions are deprecated",
  ReplaceWith(
    "Show.nonEmptyList(SA)",
    "arrow.core.nonEmptyList", "arrow.core.Show"
  ),
  DeprecationLevel.WARNING
)
inline fun <A> Companion.show(SA: Show<A>): NonEmptyListShow<A> = object :
    arrow.core.extensions.NonEmptyListShow<A> { override fun SA(): arrow.typeclasses.Show<A> = SA }
