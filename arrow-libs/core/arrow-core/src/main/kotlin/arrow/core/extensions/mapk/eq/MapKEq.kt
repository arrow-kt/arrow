package arrow.core.extensions.mapk.eq

import arrow.core.MapK
import arrow.core.MapK.Companion
import arrow.core.extensions.MapKEq
import arrow.typeclasses.Eq
import arrow.typeclasses.EqDeprecation
import kotlin.Boolean
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
  ReplaceWith("this == arg1"),
  DeprecationLevel.WARNING
)
fun <K, A> MapK<K, A>.neqv(
  EQK: Eq<K>,
  EQA: Eq<A>,
  arg1: MapK<K, A>
): Boolean = arrow.core.MapK.eq<K, A>(EQK, EQA).run {
  this@neqv.neqv(arg1) as kotlin.Boolean
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(EqDeprecation)
inline fun <K, A> Companion.eq(EQK: Eq<K>, EQA: Eq<A>): MapKEq<K, A> = object :
    arrow.core.extensions.MapKEq<K, A> { override fun EQK(): arrow.typeclasses.Eq<K> = EQK

  override fun EQA(): arrow.typeclasses.Eq<A> = EQA }
