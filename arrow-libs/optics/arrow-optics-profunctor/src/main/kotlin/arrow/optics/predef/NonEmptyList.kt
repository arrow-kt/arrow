package arrow.optics.predef

import arrow.core.Nel
import arrow.optics.Optic
import arrow.optics.PTraversal
import arrow.optics.TraversalK
import arrow.optics.compose
import arrow.optics.iso

fun <A, B> Optic.Companion.traversedNel(): PTraversal<Nel<A>, Nel<B>, A, B> =
  Optic.iso({ xs: Nel<A> -> xs.all }, { b: List<B> -> Nel.fromListUnsafe(b) }).traversed()

@JvmName("nel_traversed")
fun <K : TraversalK, S, T, A, B> Optic<K, S, T, Nel<A>, Nel<B>>.traversed(): Optic<TraversalK, S, T, A, B> =
  compose(Optic.traversedNel())
