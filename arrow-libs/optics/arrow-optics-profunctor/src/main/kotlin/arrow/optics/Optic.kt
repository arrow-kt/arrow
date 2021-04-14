package arrow.optics

import arrow.optics.internal.Pro
import arrow.optics.internal.Profunctor

typealias Optic_<K, I, S, A> = Optic<K, I, S, S, A, A>

interface Optic<out K, I, in S, out T, A, B> {
  fun <P, J> Profunctor<P>.transform(focus: Pro<P, J, A, B>): Pro<P, (I) -> J, S, T>

  companion object
}

// Markers to use for K
interface FoldK
interface AffineFoldK : FoldK
interface GetterK : AffineFoldK

interface SetterK
interface TraversalK : SetterK, FoldK
interface AffineTraversalK : TraversalK, AffineFoldK
interface PrismK : AffineTraversalK, ReviewK
interface LensK : AffineTraversalK, GetterK
interface ReviewK
interface ReversedLensK : ReviewK
interface ReversedPrismK : GetterK
interface IsoK : LensK, ReversedLensK, ReversedPrismK

// Type tetris! Although this isn't that bad ^-^
fun <K1, K2 : K1, I1, I2, S, T, A1 : A2, A2, B1, B2 : B1, C, D> Optic<K1, I1, S, T, A1, B1>.compose(
  other: Optic<K2, I2, A2, B2, C, D>
): Optic<K1, I1, S, T, C, D> = object : Optic<K1, I1, S, T, C, D> {
  override fun <P, J> Profunctor<P>.transform(focus: Pro<P, J, C, D>): Pro<P, (I1) -> J, S, T> {
    val pab = other.run { transform(focus) }.ixMap<J, (I2) -> J, A2, B2> { j -> { j } }
    return this@compose.run { transform(pab) }
  }
}

fun <K1, K2 : K1, I1, I2, I3, S, T, A1 : A2, A2, B1, B2 : B1, C, D> Optic<K1, I1, S, T, A1, B1>.ixCompose(
  other: Optic<K2, I2, A2, B2, C, D>,
  f: (I1, I2) -> I3
): Optic<K1, I3, S, T, C, D> = object : Optic<K1, I3, S, T, C, D> {
  override fun <P, J> Profunctor<P>.transform(focus: Pro<P, J, C, D>): Pro<P, (I3) -> J, S, T> {
    val pab = other.run { transform(focus) }
    return this@ixCompose.run { transform(pab) }
      .ixMap { i3j -> { i1: I1 -> { i2: I2 -> i3j(f(i1, i2)) } } }
  }
}

fun <K1, K2 : K1, I1, I2, S, T, A1 : A2, A2, B1, B2 : B1, C, D> Optic<K1, I1, S, T, A1, B1>.ixCompose(
  other: Optic<K2, I2, A2, B2, C, D>
): Optic<K1, Pair<I1, I2>, S, T, C, D> =
  ixCompose(other) { i1, i2 -> i1 to i2 }

// Included for symmetry, but this is just compose
fun <K1, K2 : K1, I1, I2, S, T, A1 : A2, A2, B1, B2 : B1, C, D> Optic<K1, I1, S, T, A1, B1>.ixComposeLeft(
  other: Optic<K2, I2, A2, B2, C, D>
): Optic<K1, I1, S, T, C, D> =
  compose(other)

fun <K1, K2 : K1, I1, I2, S, T, A1 : A2, A2, B1, B2 : B1, C, D> Optic<K1, I1, S, T, A1, B1>.ixComposeRight(
  other: Optic<K2, I2, A2, B2, C, D>
): Optic<K1, I2, S, T, C, D> =
  ixCompose(other) { _, i2 -> i2 }

fun <K, I, J, S, T, A, B> Optic<K, I, S, T, A, B>.reindexed(
  f: (I) -> J
): Optic<K, J, S, T, A, B> =
  object : Optic<K, J, S, T, A, B> {
    override fun <P, K> Profunctor<P>.transform(focus: Pro<P, K, A, B>): Pro<P, (J) -> K, S, T> =
      this@reindexed.run {
        transform(focus).ixMap { jk -> { i: I -> jk((f(i))) } }
      }
  }
