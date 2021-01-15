package arrow.core.extensions.tuple2.monoid

import arrow.core.Tuple2
import arrow.core.Tuple2.Companion
import arrow.core.extensions.Tuple2Monoid
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
  "combineAll(MA, MB)",
  "arrow.core.combineAll"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Collection<Tuple2<A, B>>.combineAll(MA: Monoid<A>, MB: Monoid<B>): Tuple2<A, B> =
    arrow.core.Tuple2.monoid<A, B>(MA, MB).run {
  this@combineAll.combineAll() as arrow.core.Tuple2<A, B>
}

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
  "combineAll(MA, MB, arg0)",
  "arrow.core.Tuple2.combineAll"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> combineAll(
  MA: Monoid<A>,
  MB: Monoid<B>,
  arg0: List<Tuple2<A, B>>
): Tuple2<A, B> = arrow.core.Tuple2
   .monoid<A, B>(MA, MB)
   .combineAll(arg0) as arrow.core.Tuple2<A, B>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A, B> Companion.monoid(MA: Monoid<A>, MB: Monoid<B>): Tuple2Monoid<A, B> = object :
    arrow.core.extensions.Tuple2Monoid<A, B> { override fun MA(): arrow.typeclasses.Monoid<A> = MA

  override fun MB(): arrow.typeclasses.Monoid<B> = MB }
