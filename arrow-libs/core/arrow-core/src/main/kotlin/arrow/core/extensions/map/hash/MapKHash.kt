package arrow.core.extensions.map.hash

import arrow.core.extensions.MapKHash
import arrow.typeclasses.Hash
import arrow.typeclasses.HashDeprecation
import kotlin.Deprecated
import kotlin.Int
import kotlin.Suppress
import kotlin.collections.Map
import kotlin.jvm.JvmName

@JvmName("hash")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(HashDeprecation, ReplaceWith("hashCode()"))
fun <K, A> Map<K, A>.hash(HK: Hash<K>, HA: Hash<A>): Int =
    arrow.core.extensions.map.hash.Map.hash<K, A>(HK, HA).run {
  arrow.core.MapK(this@hash).hash() as kotlin.Int
}

@Deprecated("Receiver Map object is deprecated, prefer to turn Map functions into top-level functions")
object Map {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated(HashDeprecation)
  inline fun <K, A> hash(HK: Hash<K>, HA: Hash<A>): MapKHash<K, A> = object :
      arrow.core.extensions.MapKHash<K, A> { override fun HK(): arrow.typeclasses.Hash<K> = HK

    override fun HA(): arrow.typeclasses.Hash<A> = HA }}
