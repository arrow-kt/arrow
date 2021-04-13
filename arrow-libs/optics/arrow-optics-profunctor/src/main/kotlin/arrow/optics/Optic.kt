package arrow.optics

import arrow.optics.internal.Pro
import arrow.optics.internal.Profunctor

typealias Optic_<K, I, S, A> = Optic<K, I, S, S, A, A>

interface Optic<out K, I, S, T, A, B> {
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
interface PrismK : AffineTraversalK
interface LensK : AffineTraversalK, GetterK
interface IsoK : LensK

// Type tetris! Although this isn't that bad ^-^
fun <LK1, LK2, I, I2, S, T, A, B, C, D> Optic<LK1, I, S, T, A, B>.compose(
  other: Optic<LK2, I2, A, B, C, D>
): Optic<LK1, I, S, T, C, D> where LK2 : LK1 = object : Optic<LK1, I, S, T, C, D> {
  override fun <P, J> Profunctor<P>.transform(focus: Pro<P, J, C, D>): Pro<P, (I) -> J, S, T> {
    val pab = other.run { transform(focus) }.ixMap<J, (I2) -> J, A, B> { j -> { j } }
    return this@compose.run { transform(pab) }
  }
}

fun <LK1, LK2, I, J, Z, S, T, A, B, C, D> Optic<LK1, I, S, T, A, B>.icompose(
  other: Optic<LK2, J, A, B, C, D>,
  f: (I, J) -> Z
): Optic<LK1, Z, S, T, C, D> where LK2 : LK1 = object : Optic<LK1, Z, S, T, C, D> {
  override fun <P, K> Profunctor<P>.transform(focus: Pro<P, K, C, D>): Pro<P, (Z) -> K, S, T> {
    val pab = other.run { transform(focus) }
    return this@icompose.run { transform(pab).ixMap { zk -> { i -> { j -> zk(f(i, j)) } } } }
  }
}

fun <LK1, LK2, I, J, S, T, A, B, C, D> Optic<LK1, I, S, T, A, B>.icompose(
  other: Optic<LK2, J, A, B, C, D>
): Optic<LK1, Pair<I, J>, S, T, C, D> where LK2 : LK1 =
  icompose(other) { i, j -> i to j }

// Included for symmetry
fun <LK1, LK2, I, J, S, T, A, B, C, D> Optic<LK1, I, S, T, A, B>.icomposeLeft(
  other: Optic<LK2, J, A, B, C, D>
): Optic<LK1, I, S, T, C, D> where LK2 : LK1 =
  compose(other)

fun <LK1, LK2, I, J, S, T, A, B, C, D> Optic<LK1, I, S, T, A, B>.icomposeRight(
  other: Optic<LK2, J, A, B, C, D>
): Optic<LK1, J, S, T, C, D> where LK2 : LK1 =
  icompose(other) { _, j -> j }
