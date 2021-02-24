package arrow.core.extensions.map.eqK

import arrow.Kind
import arrow.core.ForMapK
import arrow.core.extensions.MapKEqK
import arrow.typeclasses.Eq
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Suppress
import kotlin.collections.Map
import kotlin.jvm.JvmName

@JvmName("eqK")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0")
fun <K, A> Map<K, A>.eqK(
  EQK: Eq<K>,
  arg1: Map<K, A>,
  arg2: Eq<A>
): Boolean = arrow.core.extensions.map.eqK.Map.eqK<K>(EQK).run {
  arrow.core.MapK(this@eqK).eqK<A>(arrow.core.MapK(arg1), arg2) as kotlin.Boolean
}

@JvmName("liftEq")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0")
fun <K, A> liftEq(EQK: Eq<K>, arg0: Eq<A>): Eq<Kind<Kind<ForMapK, K>, A>> =
  arrow.core.extensions.map.eqK.Map
    .eqK<K>(EQK)
    .liftEq<A>(arg0) as arrow.typeclasses.Eq<arrow.Kind<arrow.Kind<arrow.core.ForMapK, K>, A>>

object Map {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated("Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0")
  inline fun <K> eqK(EQK: Eq<K>): MapKEqK<K> = object : arrow.core.extensions.MapKEqK<K> {
    override
    fun EQK(): arrow.typeclasses.Eq<K> = EQK
  }
}
