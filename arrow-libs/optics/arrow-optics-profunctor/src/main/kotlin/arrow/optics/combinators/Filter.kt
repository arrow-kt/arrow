package arrow.optics.combinators

import arrow.core.Either
import arrow.core.identity
import arrow.optics.AffineFoldK
import arrow.optics.AffineTraversal
import arrow.optics.AffineTraversalK
import arrow.optics.FoldK
import arrow.optics.IxAffineFold
import arrow.optics.IxAffineTraversal
import arrow.optics.IxFold
import arrow.optics.IxFoldF
import arrow.optics.IxTraversal
import arrow.optics.LensK
import arrow.optics.Optic
import arrow.optics.PrismK
import arrow.optics.TraversalK
import arrow.optics.aTraversing
import arrow.optics.compose
import arrow.optics.has
import arrow.optics.internal.Applicative
import arrow.optics.internal.IxWanderF
import arrow.optics.internal.Kind
import arrow.optics.internal.Pro
import arrow.optics.internal.Profunctor
import arrow.optics.internal.Traversing
import arrow.optics.internal.WanderF
import arrow.optics.ixATraversing
import arrow.optics.ixFolding
import arrow.optics.ixTraverseOf
import arrow.optics.ixTraverseOf_
import arrow.optics.ixTraversing
import arrow.optics.prism
import arrow.optics.traverseOf_
import arrow.optics.traversing

fun <S> Optic.Companion.filter(pred: (S) -> Boolean): Optic<PrismK, Any?, S, S, S, S> =
  prism({ s -> if (pred(s)) Either.Right(s) else Either.Left(s) }, ::identity)

@JvmName("filter_prism")
fun <K : PrismK, I, S, A> Optic<K, I, S, S, A, A>.filter(
  pred: (A) -> Boolean
): Optic<PrismK, I, S, S, A, A> = compose(Optic.filter(pred))

@JvmName("filter_affineTraversal")
fun <K : AffineTraversalK, I, S, A> Optic<K, I, S, S, A, A>.filter(
  pred: (A) -> Boolean
): Optic<AffineTraversalK, I, S, S, A, A> = compose(Optic.filter(pred))

@JvmName("filter_traversal")
fun <K : TraversalK, I, S, A> Optic<K, I, S, S, A, A>.filter(
  pred: (A) -> Boolean
): Optic<TraversalK, I, S, S, A, A> = compose(Optic.filter(pred))

@JvmName("filter_affineFold")
fun <K : AffineFoldK, I, S, A> Optic<K, I, S, S, A, A>.filter(
  pred: (A) -> Boolean
): Optic<AffineFoldK, I, S, S, A, A> = compose(Optic.filter(pred))

@JvmName("filter_fold")
fun <K : FoldK, I, S, A> Optic<K, I, S, S, A, A>.filter(
  pred: (A) -> Boolean
): Optic<FoldK, I, S, S, A, A> = compose(Optic.filter(pred))

fun <K : FoldK, I, S> Optic.Companion.filteredBy(filter: Optic<K, I, S, S, Any?, Any?>): Optic<PrismK, Any?, S, S, S, S> =
  filter { s -> s.has(filter) }

@JvmName("filteredBy_prism")
fun <K1 : PrismK, K2 : FoldK, I, J, S, A> Optic<K1, I, S, S, A, A>.filteredBy(
  filter: Optic<K2, J, A, A, Any?, Any?>
): Optic<PrismK, I, S, S, A, A> =
  compose(Optic.filteredBy(filter))

@JvmName("filteredBy_affineTraversal")
fun <K1 : AffineTraversalK, K2 : FoldK, I, J, S, A> Optic<K1, I, S, S, A, A>.filteredBy(
  filter: Optic<K2, J, A, A, Any?, Any?>
): Optic<AffineTraversalK, I, S, S, A, A> =
  compose(Optic.filteredBy(filter))

@JvmName("filteredBy_traversal")
fun <K1 : TraversalK, K2 : FoldK, I, J, S, A> Optic<K1, I, S, S, A, A>.filteredBy(
  filter: Optic<K2, J, A, A, Any?, Any?>
): Optic<TraversalK, I, S, S, A, A> =
  compose(Optic.filteredBy(filter))

@JvmName("filteredBy_affineFold")
fun <K1 : AffineFoldK, K2 : FoldK, I, J, S, A> Optic<K1, I, S, S, A, A>.filteredBy(
  filter: Optic<K2, J, A, A, Any?, Any?>
): Optic<AffineFoldK, I, S, S, A, A> =
  compose(Optic.filteredBy(filter))

@JvmName("filteredBy_fold")
fun <K1 : FoldK, K2 : FoldK, S, I, J, A> Optic<K1, I, S, S, A, A>.filteredBy(
  filter: Optic<K2, J, A, A, Any?, Any?>
): Optic<FoldK, I, S, S, A, A> =
  compose(Optic.filteredBy(filter))
