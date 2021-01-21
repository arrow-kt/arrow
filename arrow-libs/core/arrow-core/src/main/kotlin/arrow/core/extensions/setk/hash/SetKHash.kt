package arrow.core.extensions.setk.hash

import arrow.core.SetK
import arrow.core.SetK.Companion
import arrow.core.extensions.SetKHash
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
    "Hash.set(HA).run { this.hash() }",
    "arrow.core.set",
    "arrow.typeclasses.Hash"
  ),
  DeprecationLevel.WARNING
)
fun <A> SetK<A>.hash(HA: Hash<A>): Int = arrow.core.SetK.hash<A>(HA).run {
  this@hash.hash() as kotlin.Int
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "Hash.set<A>(HA)",
    "arrow.core.set",
    "arrow.typeclasses.Hash"
  ),
  DeprecationLevel.WARNING
)
inline fun <A> Companion.hash(HA: Hash<A>): SetKHash<A> = object : arrow.core.extensions.SetKHash<A> {
  override fun HA(): arrow.typeclasses.Hash<A> = HA
}
