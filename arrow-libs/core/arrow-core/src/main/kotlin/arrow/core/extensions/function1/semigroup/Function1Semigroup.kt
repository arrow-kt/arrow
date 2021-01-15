package arrow.core.extensions.function1.semigroup

import arrow.core.Function1
import arrow.core.Function1.Companion
import arrow.core.extensions.Function1Semigroup
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
  "plus(SB, arg1)",
  "arrow.core.plus"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Function1<A, B>.plus(SB: Semigroup<B>, arg1: Function1<A, B>): Function1<A, B> =
    arrow.core.Function1.semigroup<A, B>(SB).run {
  this@plus.plus(arg1) as arrow.core.Function1<A, B>
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
  "maybeCombine(SB, arg1)",
  "arrow.core.maybeCombine"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Function1<A, B>.maybeCombine(SB: Semigroup<B>, arg1: Function1<A, B>): Function1<A, B> =
    arrow.core.Function1.semigroup<A, B>(SB).run {
  this@maybeCombine.maybeCombine(arg1) as arrow.core.Function1<A, B>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A, B> Companion.semigroup(SB: Semigroup<B>): Function1Semigroup<A, B> = object :
    arrow.core.extensions.Function1Semigroup<A, B> { override fun SB():
    arrow.typeclasses.Semigroup<B> = SB }
