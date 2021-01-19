package arrow.core.extensions.tuple2.show

import arrow.core.Tuple2.Companion
import arrow.core.extensions.Tuple2Show
import arrow.typeclasses.Show
import kotlin.Suppress

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Tuple2 is deprecated in favor of Kotlin's Pair. ReplaceWith Pair and use Pair instance of Show",
  ReplaceWith(
    "Show.pair(SA, SB, SC)",
    "arrow.core.Show",
    "arrow.core.pair"
  ),
  DeprecationLevel.WARNING
)
inline fun <A, B> Companion.show(SA: Show<A>, SB: Show<B>): Tuple2Show<A, B> = object :
    arrow.core.extensions.Tuple2Show<A, B> { override fun SA(): arrow.typeclasses.Show<A> = SA

  override fun SB(): arrow.typeclasses.Show<B> = SB }
