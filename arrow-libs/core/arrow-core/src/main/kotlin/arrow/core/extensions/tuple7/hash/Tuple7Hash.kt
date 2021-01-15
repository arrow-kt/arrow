package arrow.core.extensions.tuple7.hash

import arrow.core.Tuple7
import arrow.core.Tuple7.Companion
import arrow.core.extensions.Tuple7Hash
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
  "hash(HA, HB, HC, HD, HE, HF, HG)",
  "arrow.core.hash"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F, G> Tuple7<A, B, C, D, E, F, G>.hash(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>,
  HF: Hash<F>,
  HG: Hash<G>
): Int = arrow.core.Tuple7.hash<A, B, C, D, E, F, G>(HA, HB, HC, HD, HE, HF, HG).run {
  this@hash.hash() as kotlin.Int
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A, B, C, D, E, F, G> Companion.hash(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>,
  HF: Hash<F>,
  HG: Hash<G>
): Tuple7Hash<A, B, C, D, E, F, G> = object : arrow.core.extensions.Tuple7Hash<A, B, C, D, E, F, G>
    { override fun HA(): arrow.typeclasses.Hash<A> = HA

  override fun HB(): arrow.typeclasses.Hash<B> = HB

  override fun HC(): arrow.typeclasses.Hash<C> = HC

  override fun HD(): arrow.typeclasses.Hash<D> = HD

  override fun HE(): arrow.typeclasses.Hash<E> = HE

  override fun HF(): arrow.typeclasses.Hash<F> = HF

  override fun HG(): arrow.typeclasses.Hash<G> = HG }
