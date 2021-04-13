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
fun <LK1, LK2, I, I2, S, T1, T2 : T1, A1 : A2, A2, B1, B2 : B1, C, D> Optic<LK1, I, S, T1, A1, B1>.compose(
  other: Optic<LK2, I2, A2, B2, C, D>
): Optic<LK1, I, S, T2, C, D> where LK2 : LK1 = object : Optic<LK1, I, S, T2, C, D> {
  override fun <P, J> Profunctor<P>.transform(focus: Pro<P, J, C, D>): Pro<P, (I) -> J, S, T2> {
    // Both casts are safe because of variance but the Pro type does not allow variance :/
    val pab = other.run { transform(focus) }.ixMap<J, (I2) -> J, A2, B2> { j -> { j } }
      as Pro<P, J, A1, B1>
    return this@compose.run { transform(pab) as Pro<P, (I) -> J, S, T2> }
  }
}

fun <LK1, LK2, I, J, Z, S, T, A1 : A2, A2, B1, B2 : B1, C, D> Optic<LK1, I, S, T, A1, B1>.icompose(
  other: Optic<LK2, J, A2, B2, C, D>,
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

fun <K, I, J, S, T, A, B> Optic<K, I, S, T, A, B>.reindexed(
  f: (I) -> J
): Optic<K, J, S, T, A, B> =
  object : Optic<K, J, S, T, A, B> {
    override fun <P, K> Profunctor<P>.transform(focus: Pro<P, K, A, B>): Pro<P, (J) -> K, S, T> =
      this@reindexed.run {
        transform(focus).ixMap { jk -> { i: I -> jk((f(i))) } }
      }
  }
