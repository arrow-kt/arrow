package arrow.core.extensions.tuple5.hash

import arrow.core.Tuple5
import arrow.core.Tuple5.Companion
import arrow.core.extensions.Tuple5Hash
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
  "hash(HA, HB, HC, HD, HE)",
  "arrow.core.hash"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E> Tuple5<A, B, C, D, E>.hash(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>
): Int = arrow.core.Tuple5.hash<A, B, C, D, E>(HA, HB, HC, HD, HE).run {
  this@hash.hash() as kotlin.Int
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "Hash.tuple5(HA, HB, HC, HD, HE)",
    "arrow.core.Hash",
    "arrow.core.tuple5"
  ),
  DeprecationLevel.WARNING
)
inline fun <A, B, C, D, E> Companion.hash(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>
): Tuple5Hash<A, B, C, D, E> = object : arrow.core.extensions.Tuple5Hash<A, B, C, D, E> { override
    fun HA(): arrow.typeclasses.Hash<A> = HA

  override fun HB(): arrow.typeclasses.Hash<B> = HB

  override fun HC(): arrow.typeclasses.Hash<C> = HC

  override fun HD(): arrow.typeclasses.Hash<D> = HD

  override fun HE(): arrow.typeclasses.Hash<E> = HE }
