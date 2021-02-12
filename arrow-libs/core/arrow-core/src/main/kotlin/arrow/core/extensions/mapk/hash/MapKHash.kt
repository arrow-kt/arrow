package arrow.core.extensions.mapk.hash

import arrow.core.MapK
import arrow.core.MapK.Companion
import arrow.core.extensions.MapKHash
import arrow.typeclasses.Hash
import arrow.typeclasses.HashDeprecation
import kotlin.Deprecated
import kotlin.Int
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("hash")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(HashDeprecation, ReplaceWith("hashCode()"))
fun <K, A> MapK<K, A>.hash(HK: Hash<K>, HA: Hash<A>): Int = arrow.core.MapK.hash<K, A>(HK, HA).run {
  this@hash.hash() as kotlin.Int
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(HashDeprecation)
inline fun <K, A> Companion.hash(HK: Hash<K>, HA: Hash<A>): MapKHash<K, A> = object :
    arrow.core.extensions.MapKHash<K, A> { override fun HK(): arrow.typeclasses.Hash<K> = HK

  override fun HA(): arrow.typeclasses.Hash<A> = HA }
