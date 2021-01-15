package arrow.core.extensions.tuple2.semigroup

import arrow.core.Tuple2
import arrow.core.Tuple2.Companion
import arrow.core.extensions.Tuple2Semigroup
import arrow.typeclasses.Semigroup
import kotlin.Deprecated
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("plus")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "plus(SA, SB, arg1)",
  "arrow.core.plus"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Tuple2<A, B>.plus(
  SA: Semigroup<A>,
  SB: Semigroup<B>,
  arg1: Tuple2<A, B>
): Tuple2<A, B> = arrow.core.Tuple2.semigroup<A, B>(SA, SB).run {
  this@plus.plus(arg1) as arrow.core.Tuple2<A, B>
}

@JvmName("maybeCombine")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "maybeCombine(SA, SB, arg1)",
  "arrow.core.maybeCombine"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Tuple2<A, B>.maybeCombine(
  SA: Semigroup<A>,
  SB: Semigroup<B>,
  arg1: Tuple2<A, B>
): Tuple2<A, B> = arrow.core.Tuple2.semigroup<A, B>(SA, SB).run {
  this@maybeCombine.maybeCombine(arg1) as arrow.core.Tuple2<A, B>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A, B> Companion.semigroup(SA: Semigroup<A>, SB: Semigroup<B>): Tuple2Semigroup<A, B> =
    object : arrow.core.extensions.Tuple2Semigroup<A, B> { override fun SA():
    arrow.typeclasses.Semigroup<A> = SA

  override fun SB(): arrow.typeclasses.Semigroup<B> = SB }
