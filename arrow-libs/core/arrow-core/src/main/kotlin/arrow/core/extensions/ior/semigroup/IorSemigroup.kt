package arrow.core.extensions.ior.semigroup

import arrow.core.Ior
import arrow.core.Ior.Companion
import arrow.core.extensions.IorSemigroup
import arrow.typeclasses.Semigroup

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
  "this.combine(SGL, SGR, arg1)",
  "arrow.core.combine"
  ),
  DeprecationLevel.WARNING
)
fun <L, R> Ior<L, R>.plus(
  SGL: Semigroup<L>,
  SGR: Semigroup<R>,
  arg1: Ior<L, R>
): Ior<L, R> = arrow.core.Ior.semigroup<L, R>(SGL, SGR).run {
  this@plus.plus(arg1) as arrow.core.Ior<L, R>
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
    "this.combine(SGL, SGR, arg1)",
    "arrow.core.combine"
  ),
  DeprecationLevel.WARNING
)
fun <L, R> Ior<L, R>.maybeCombine(
  SGL: Semigroup<L>,
  SGR: Semigroup<R>,
  arg1: Ior<L, R>
): Ior<L, R> = arrow.core.Ior.semigroup<L, R>(SGL, SGR).run {
  this@maybeCombine.maybeCombine(arg1) as arrow.core.Ior<L, R>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "Semigroup.ior(SGL, SGR)",
    "arrow.core.ior", "arrow.typeclasses.Semigroup"
  ),
  DeprecationLevel.WARNING
)
inline fun <L, R> Companion.semigroup(SGL: Semigroup<L>, SGR: Semigroup<R>): IorSemigroup<L, R> =
    object : arrow.core.extensions.IorSemigroup<L, R> { override fun SGL():
    arrow.typeclasses.Semigroup<L> = SGL

  override fun SGR(): arrow.typeclasses.Semigroup<R> = SGR }
