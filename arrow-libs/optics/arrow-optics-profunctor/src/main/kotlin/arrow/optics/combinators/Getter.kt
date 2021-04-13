package arrow.optics.combinators

import arrow.optics.AffineFoldK
import arrow.optics.FoldK
import arrow.optics.GetterK
import arrow.optics.Optic_
import arrow.optics.compose
import arrow.optics.get

// This is a bit ugly
// The compiler can do this on its own if K is fixed, but not in polymorphic settings...
// The compiler could figure this out, if it knew that the Marker interfaces are final and thus
//  have a fixed hierarchy...
@JvmName("getFold")
fun <K : FoldK, I, S, A, B> Optic_<K, I, S, A>.get(f: (A) -> B): Optic_<FoldK, I, S, B> =
  this.compose(arrow.optics.Optic.get(f))
@JvmName("getAffineFold")
fun <K : AffineFoldK, I, S, A, B> Optic_<K, I, S, A>.get(f: (A) -> B): Optic_<AffineFoldK, I, S, B> =
  this.compose(arrow.optics.Optic.get(f))
@JvmName("getGetter")
fun <K : GetterK, I, S, A, B> Optic_<K, I, S, A>.get(f: (A) -> B): Optic_<GetterK, I, S, B> =
  this.compose(arrow.optics.Optic.get(f))
