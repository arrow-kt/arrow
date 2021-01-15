package arrow.core.extensions.id.hash

import arrow.core.Id
import arrow.core.Id.Companion
import arrow.core.extensions.IdHash
import arrow.typeclasses.Hash
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
  "hash(HA)",
  "arrow.core.hash"
  ),
  DeprecationLevel.WARNING
)
fun <A> Id<A>.hash(HA: Hash<A>): Int = arrow.core.Id.hash<A>(HA).run {
  this@hash.hash() as kotlin.Int
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A> Companion.hash(HA: Hash<A>): IdHash<A> = object : arrow.core.extensions.IdHash<A> {
    override fun HA(): arrow.typeclasses.Hash<A> = HA }
