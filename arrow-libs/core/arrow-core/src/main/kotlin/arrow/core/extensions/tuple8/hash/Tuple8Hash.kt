package arrow.core.extensions.tuple8.hash

import arrow.core.Tuple8
import arrow.core.Tuple8.Companion
import arrow.core.extensions.Tuple8Hash
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
  "hash(HA, HB, HC, HD, HE, HF, HG, HH)",
  "arrow.core.hash"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F, G, H> Tuple8<A, B, C, D, E, F, G, H>.hash(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>,
  HF: Hash<F>,
  HG: Hash<G>,
  HH: Hash<H>
): Int = arrow.core.Tuple8.hash<A, B, C, D, E, F, G, H>(HA, HB, HC, HD, HE, HF, HG, HH).run {
  this@hash.hash() as kotlin.Int
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "Hash.tuple8(HA, HB, HC, HD, HE, HF, HG, HH)",
    "arrow.core.Hash",
    "arrow.core.tuple8"
  ),
  DeprecationLevel.WARNING
)
inline fun <A, B, C, D, E, F, G, H> Companion.hash(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>,
  HF: Hash<F>,
  HG: Hash<G>,
  HH: Hash<H>
): Tuple8Hash<A, B, C, D, E, F, G, H> = object : arrow.core.extensions.Tuple8Hash<A, B, C, D, E, F,
    G, H> { override fun HA(): arrow.typeclasses.Hash<A> = HA

  override fun HB(): arrow.typeclasses.Hash<B> = HB

  override fun HC(): arrow.typeclasses.Hash<C> = HC

  override fun HD(): arrow.typeclasses.Hash<D> = HD

  override fun HE(): arrow.typeclasses.Hash<E> = HE

  override fun HF(): arrow.typeclasses.Hash<F> = HF

  override fun HG(): arrow.typeclasses.Hash<G> = HG

  override fun HH(): arrow.typeclasses.Hash<H> = HH }
