package arrow.optics

import arrow.optics.internal.Pro
import arrow.optics.internal.Profunctor

typealias Optic_<K, S, A> = Optic<K, S, S, A, A>

interface Optic<out K, S, T, A, B> {
  fun <P> Profunctor<P>.transform(focus: Pro<P, A, B>): Pro<P, S, T>

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
fun <LK1, LK2, S, T, A, B, C, D> Optic<LK1,S, T, A, B>.compose(
  other: Optic<LK2, A, B, C, D>
): Optic<LK1, S, T, C, D> where LK2 : LK1 = object : Optic<LK1, S, T, C, D> {
  override fun <P> Profunctor<P>.transform(focus: Pro<P, C, D>): Pro<P, S, T> {
    val pab = other.run { transform(focus) }
    return this@compose.run { transform(pab) }
  }
}
