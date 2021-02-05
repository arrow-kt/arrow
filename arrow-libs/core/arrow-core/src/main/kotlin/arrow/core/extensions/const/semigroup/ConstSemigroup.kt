package arrow.core.extensions.const.semigroup

import arrow.Kind
import arrow.core.Const
import arrow.core.Const.Companion
import arrow.core.ForConst
import arrow.core.extensions.ConstSemigroup
import arrow.typeclasses.Semigroup
import kotlin.Deprecated
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("combine")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "combine(SA, arg1)",
    "arrow.core.combine"
  ),
  DeprecationLevel.WARNING
)
fun <A, T> Kind<Kind<ForConst, A>, T>.combine(SA: Semigroup<A>, arg1: Kind<Kind<ForConst, A>, T>): Const<A, T> =
  arrow.core.Const.semigroup<A, T>(SA).run {
    this@combine.combine(arg1) as arrow.core.Const<A, T>
  }

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
    "combine(SA, arg1)",
    "arrow.core.combine"
  ),
  DeprecationLevel.WARNING
)
fun <A, T> Kind<Kind<ForConst, A>, T>.plus(SA: Semigroup<A>, arg1: Kind<Kind<ForConst, A>, T>): Const<A, T> =
  arrow.core.Const.semigroup<A, T>(SA).run {
    this@plus.plus(arg1) as arrow.core.Const<A, T>
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
    "Option.fromNullable(arg1).fold({ this }, { combine(it) })",
    "arrow.core.combine"
  ),
  DeprecationLevel.WARNING
)
fun <A, T> Kind<Kind<ForConst, A>, T>.maybeCombine(
  SA: Semigroup<A>,
  arg1: Kind<Kind<ForConst, A>, T>
): Const<A, T> =
  arrow.core.Const.semigroup<A, T>(SA).run {
    this@maybeCombine.maybeCombine(arg1) as arrow.core.Const<A, T>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Semigroup typeclass is deprecated. Use concrete methods on Const",
  level = DeprecationLevel.WARNING
)
inline fun <A, T> Companion.semigroup(SA: Semigroup<A>): ConstSemigroup<A, T> =
  object : arrow.core.extensions.ConstSemigroup<A, T> {
    override fun SA(): arrow.typeclasses.Semigroup<A> =
      SA
  }
