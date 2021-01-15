package arrow.core.extensions.tuple3.hash

import arrow.core.Tuple3
import arrow.core.Tuple3.Companion
import arrow.core.extensions.Tuple3Hash
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
  "hash(HA, HB, HC)",
  "arrow.core.hash"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> Tuple3<A, B, C>.hash(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>
): Int = arrow.core.Tuple3.hash<A, B, C>(HA, HB, HC).run {
  this@hash.hash() as kotlin.Int
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A, B, C> Companion.hash(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>
): Tuple3Hash<A, B, C> = object : arrow.core.extensions.Tuple3Hash<A, B, C> { override fun HA():
    arrow.typeclasses.Hash<A> = HA

  override fun HB(): arrow.typeclasses.Hash<B> = HB

  override fun HC(): arrow.typeclasses.Hash<C> = HC }
