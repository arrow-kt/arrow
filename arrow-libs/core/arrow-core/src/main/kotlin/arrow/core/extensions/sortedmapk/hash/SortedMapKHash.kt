package arrow.core.extensions.sortedmapk.hash

import arrow.core.SortedMapK
import arrow.core.SortedMapK.Companion
import arrow.core.extensions.SortedMapKHash
import arrow.typeclasses.Hash
import kotlin.Comparable
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
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "hash(HK, HA)",
  "arrow.core.hash"
  ),
  DeprecationLevel.WARNING
)
fun <K : Comparable<K>, A> SortedMapK<K, A>.hash(HK: Hash<K>, HA: Hash<A>): Int =
    arrow.core.SortedMapK.hash<K, A>(HK, HA).run {
  this@hash.hash() as kotlin.Int
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <K : Comparable<K>, A> Companion.hash(HK: Hash<K>, HA: Hash<A>): SortedMapKHash<K, A> =
    object : arrow.core.extensions.SortedMapKHash<K, A> { override fun HK():
    arrow.typeclasses.Hash<K> = HK

  override fun HA(): arrow.typeclasses.Hash<A> = HA }
