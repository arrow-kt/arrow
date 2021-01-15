package arrow.core.extensions.set.hash

import arrow.core.extensions.SetKHash
import arrow.typeclasses.Hash
import kotlin.Deprecated
import kotlin.Int
import kotlin.Suppress
import kotlin.collections.Set
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
  "hash(HA)",
  "arrow.core.hash"
  ),
  DeprecationLevel.WARNING
)
fun <A> Set<A>.hash(HA: Hash<A>): Int = arrow.core.extensions.set.hash.Set.hash<A>(HA).run {
  arrow.core.SetK(this@hash).hash() as kotlin.Int
}

object Set {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun <A> hash(HA: Hash<A>): SetKHash<A> = object : arrow.core.extensions.SetKHash<A> {
      override fun HA(): arrow.typeclasses.Hash<A> = HA }}
