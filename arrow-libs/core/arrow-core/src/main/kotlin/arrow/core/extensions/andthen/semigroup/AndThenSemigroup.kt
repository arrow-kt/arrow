package arrow.core.extensions.andthen.semigroup

import arrow.core.AndThen
import arrow.core.AndThen.Companion
import arrow.core.extensions.AndThenSemigroup
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
fun <A, B> AndThen<A, B>.plus(SB: Semigroup<B>, arg1: AndThen<A, B>): AndThen<A, B> =
  arrow.core.AndThen.semigroup<A, B>(SB).run {
    this@plus.plus(arg1) as arrow.core.AndThen<A, B>
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
fun <A, B> AndThen<A, B>.maybeCombine(SB: Semigroup<B>, arg1: AndThen<A, B>): AndThen<A, B> =
  arrow.core.AndThen.semigroup<A, B>(SB).run {
    this@maybeCombine.maybeCombine(arg1) as arrow.core.AndThen<A, B>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A, B> Companion.semigroup(SB: Semigroup<B>): AndThenSemigroup<A, B> = object :
  arrow.core.extensions.AndThenSemigroup<A, B> { override fun SB(): arrow.typeclasses.Semigroup<B> = SB }
