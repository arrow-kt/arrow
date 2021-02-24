package arrow.core.extensions.sortedmapk.eq

import arrow.core.SortedMapK
import arrow.core.SortedMapK.Companion
import arrow.core.extensions.SortedMapKEq
import arrow.typeclasses.Eq
import arrow.typeclasses.EqDeprecation
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Deprecated
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("neqv")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  EqDeprecation,
  ReplaceWith("this != arg1"),
  DeprecationLevel.WARNING
)
fun <K : Comparable<K>, A> SortedMapK<K, A>.neqv(
  EQK: Eq<K>,
  EQA: Eq<A>,
  arg1: SortedMapK<K, A>
): Boolean = arrow.core.SortedMapK.eq<K, A>(EQK, EQA).run {
  this@neqv.neqv(arg1) as kotlin.Boolean
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(EqDeprecation)
inline fun <K : Comparable<K>, A> Companion.eq(EQK: Eq<K>, EQA: Eq<A>): SortedMapKEq<K, A> =
  object : arrow.core.extensions.SortedMapKEq<K, A> {
    override fun EQK(): arrow.typeclasses.Eq<K> = EQK
    override fun EQA(): arrow.typeclasses.Eq<A> = EQA
  }
