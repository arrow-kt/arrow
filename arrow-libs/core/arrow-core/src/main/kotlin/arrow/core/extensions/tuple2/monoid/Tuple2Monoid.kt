package arrow.core.extensions.tuple2.monoid

import arrow.core.Tuple2
import arrow.core.Tuple2.Companion
import arrow.core.combineAll
import arrow.core.extensions.Tuple2Monoid
import arrow.core.pair
import arrow.typeclasses.Monoid
import kotlin.Deprecated
import kotlin.Suppress
import kotlin.collections.Collection
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("combineAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "map { (a, b) -> Pair(a, b) } .combineAll(Monoid.pair(MA, MB)).let { (a, b) -> Tuple2(a, b) }",
    "arrow.core.combineAll",
    "arrow.core.Monoid",
    "arrow.core.pair",
    "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Collection<Tuple2<A, B>>.combineAll(MA: Monoid<A>, MB: Monoid<B>): Tuple2<A, B> =
  map { (a, b) -> Pair(a, b) }.combineAll(Monoid.pair(MA, MB)).let { (a, b) -> Tuple2(a, b) }

@JvmName("combineAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "arg0.map { (a, b) -> Pair(a, b) }.combineAll(Monoid.pair(MA, MB)).let { (a, b) -> Tuple2(a, b) }",
    "arrow.core.combineAll",
    "arrow.core.Monoid",
    "arrow.core.pair",
    "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> combineAll(
  MA: Monoid<A>,
  MB: Monoid<B>,
  arg0: List<Tuple2<A, B>>
): Tuple2<A, B> =
  arg0.map { (a, b) -> Pair(a, b) }.combineAll(Monoid.pair(MA, MB)).let { (a, b) -> Tuple2(a, b) }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Tuple2 is deprecated in favor of Kotlin's Pair. ReplaceWith Pair and use Pair instance of Show",
  ReplaceWith(
    "Monoid.pair(MA, MB)",
    "arrow.core.Monoid",
    "arrow.core.pair"
  ),
  DeprecationLevel.WARNING
)
inline fun <A, B> Companion.monoid(MA: Monoid<A>, MB: Monoid<B>): Tuple2Monoid<A, B> = object :
  arrow.core.extensions.Tuple2Monoid<A, B> {
  override fun MA(): arrow.typeclasses.Monoid<A> = MA

  override fun MB(): arrow.typeclasses.Monoid<B> = MB
}
