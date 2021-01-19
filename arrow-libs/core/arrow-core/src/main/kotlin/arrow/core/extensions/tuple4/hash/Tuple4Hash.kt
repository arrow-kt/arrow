package arrow.core.extensions.tuple4.hash

import arrow.core.Tuple4
import arrow.core.Tuple4.Companion
import arrow.core.extensions.Tuple4Hash
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
  "hash(HA, HB, HC, HD)",
  "arrow.core.hash"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D> Tuple4<A, B, C, D>.hash(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>
): Int = arrow.core.Tuple4.hash<A, B, C, D>(HA, HB, HC, HD).run {
  this@hash.hash() as kotlin.Int
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "Hash.tuple4(HA, HB, HC, HD)",
    "arrow.core.Hash",
    "arrow.core.tuple4"
  ),
  DeprecationLevel.WARNING
)
inline fun <A, B, C, D> Companion.hash(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>
): Tuple4Hash<A, B, C, D> = object : arrow.core.extensions.Tuple4Hash<A, B, C, D> { override fun
    HA(): arrow.typeclasses.Hash<A> = HA

  override fun HB(): arrow.typeclasses.Hash<B> = HB

  override fun HC(): arrow.typeclasses.Hash<C> = HC

  override fun HD(): arrow.typeclasses.Hash<D> = HD }
