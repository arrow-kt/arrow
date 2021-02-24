package arrow.core.extensions.tuple10.hash

import arrow.core.Tuple10
import arrow.core.Tuple10.Companion
import arrow.core.extensions.Tuple10Hash
import arrow.typeclasses.Hash
import arrow.typeclasses.HashDeprecation
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
@Deprecated(HashDeprecation, ReplaceWith("hashCode()"))
fun <A, B, C, D, E, F, G, H, I, J> Tuple10<A, B, C, D, E, F, G, H, I, J>.hash(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>,
  HF: Hash<F>,
  HG: Hash<G>,
  HH: Hash<H>,
  HI: Hash<I>,
  HJ: Hash<J>
): Int = arrow.core.Tuple10.hash<A, B, C, D, E, F, G, H, I,
  J>(HA, HB, HC, HD, HE, HF, HG, HH, HI, HJ).run {
  this@hash.hash() as kotlin.Int
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(HashDeprecation)
inline fun <A, B, C, D, E, F, G, H, I, J> Companion.hash(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>,
  HF: Hash<F>,
  HG: Hash<G>,
  HH: Hash<H>,
  HI: Hash<I>,
  HJ: Hash<J>
): Tuple10Hash<A, B, C, D, E, F, G, H, I, J> = object : arrow.core.extensions.Tuple10Hash<A, B, C,
    D, E, F, G, H, I, J> {
  override fun HA(): arrow.typeclasses.Hash<A> = HA

  override fun HB(): arrow.typeclasses.Hash<B> = HB

  override fun HC(): arrow.typeclasses.Hash<C> = HC

  override fun HD(): arrow.typeclasses.Hash<D> = HD

  override fun HE(): arrow.typeclasses.Hash<E> = HE

  override fun HF(): arrow.typeclasses.Hash<F> = HF

  override fun HG(): arrow.typeclasses.Hash<G> = HG

  override fun HH(): arrow.typeclasses.Hash<H> = HH

  override fun HI(): arrow.typeclasses.Hash<I> = HI

  override fun HJ(): arrow.typeclasses.Hash<J> = HJ
}
