package arrow.core.extensions.tuple9.hash

import arrow.core.Tuple9
import arrow.core.Tuple9.Companion
import arrow.core.extensions.Tuple9Hash
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
  "hash(HA, HB, HC, HD, HE, HF, HG, HH, HI)",
  "arrow.core.hash"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F, G, H, I> Tuple9<A, B, C, D, E, F, G, H, I>.hash(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>,
  HF: Hash<F>,
  HG: Hash<G>,
  HH: Hash<H>,
  HI: Hash<I>
): Int = arrow.core.Tuple9.hash<A, B, C, D, E, F, G, H, I>(HA, HB, HC, HD, HE, HF, HG, HH, HI).run {
  this@hash.hash() as kotlin.Int
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A, B, C, D, E, F, G, H, I> Companion.hash(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>,
  HF: Hash<F>,
  HG: Hash<G>,
  HH: Hash<H>,
  HI: Hash<I>
): Tuple9Hash<A, B, C, D, E, F, G, H, I> = object : arrow.core.extensions.Tuple9Hash<A, B, C, D, E,
    F, G, H, I> { override fun HA(): arrow.typeclasses.Hash<A> = HA

  override fun HB(): arrow.typeclasses.Hash<B> = HB

  override fun HC(): arrow.typeclasses.Hash<C> = HC

  override fun HD(): arrow.typeclasses.Hash<D> = HD

  override fun HE(): arrow.typeclasses.Hash<E> = HE

  override fun HF(): arrow.typeclasses.Hash<F> = HF

  override fun HG(): arrow.typeclasses.Hash<G> = HG

  override fun HH(): arrow.typeclasses.Hash<H> = HH

  override fun HI(): arrow.typeclasses.Hash<I> = HI }
