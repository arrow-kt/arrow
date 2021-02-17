package arrow.core.extensions.sortedmapk.eqK

import arrow.Kind
import arrow.core.ForSortedMapK
import arrow.core.SortedMapK.Companion
import arrow.core.extensions.SortedMapKEqK
import arrow.typeclasses.Eq
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Deprecated
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("eqK")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "eqK(EQK, arg1, arg2)",
    "arrow.core.eqK"
  ),
  DeprecationLevel.WARNING
)
fun <K : Comparable<K>, A> Kind<Kind<ForSortedMapK, K>, A>.eqK(
  EQK: Eq<K>,
  arg1: Kind<Kind<ForSortedMapK, K>, A>,
  arg2: Eq<A>
): Boolean = arrow.core.SortedMapK.eqK<K>(EQK).run {
  this@eqK.eqK<A>(arg1, arg2) as kotlin.Boolean
}

@JvmName("liftEq")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "liftEq(EQK, arg0)",
    "arrow.core.SortedMapK.liftEq"
  ),
  DeprecationLevel.WARNING
)
fun <K : Comparable<K>, A> liftEq(EQK: Eq<K>, arg0: Eq<A>): Eq<Kind<Kind<ForSortedMapK, K>, A>> =
  arrow.core.SortedMapK
    .eqK<K>(EQK)
    .liftEq<A>(arg0) as arrow.typeclasses.Eq<arrow.Kind<arrow.Kind<arrow.core.ForSortedMapK, K>, A>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <K : Comparable<K>> Companion.eqK(EQK: Eq<K>): SortedMapKEqK<K> = object :
  arrow.core.extensions.SortedMapKEqK<K> { override fun EQK(): arrow.typeclasses.Eq<K> = EQK }
