package arrow.core.extensions.mapk.semigroup

import arrow.core.MapK
import arrow.core.MapK.Companion
import arrow.core.extensions.MapKSemigroup
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
    "combine(SG, arg1)",
    "arrow.core.combine"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> MapK<K, A>.plus(SG: Semigroup<A>, arg1: MapK<K, A>): MapK<K, A> =
  arrow.core.MapK.semigroup<K, A>(SG).run {
    this@plus.plus(arg1) as arrow.core.MapK<K, A>
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
    "arg2?.let { it.combine(SG, this) }",
    "arrow.core.combine"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> MapK<K, A>.maybeCombine(SG: Semigroup<A>, arg1: MapK<K, A>): MapK<K, A> =
  arrow.core.MapK.semigroup<K, A>(SG).run {
    this@maybeCombine.maybeCombine(arg1) as arrow.core.MapK<K, A>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("Semigroup.map(SG)", "arrow.core.Semigroup", "arrow.core.map"))
inline fun <K, A> Companion.semigroup(SG: Semigroup<A>): MapKSemigroup<K, A> = object :
  arrow.core.extensions.MapKSemigroup<K, A> {
  override fun SG(): arrow.typeclasses.Semigroup<A> =
    SG
}
