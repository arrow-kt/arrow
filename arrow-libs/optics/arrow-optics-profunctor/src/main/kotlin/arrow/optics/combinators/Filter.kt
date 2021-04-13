package arrow.optics.combinators

import arrow.core.Either
import arrow.core.identity
import arrow.optics.AffineFoldK
import arrow.optics.AffineTraversal
import arrow.optics.AffineTraversalK
import arrow.optics.FoldK
import arrow.optics.Optic
import arrow.optics.PrismK
import arrow.optics.TraversalK
import arrow.optics.aTraversing
import arrow.optics.compose
import arrow.optics.has
import arrow.optics.internal.Applicative
import arrow.optics.internal.Kind
import arrow.optics.internal.Pro
import arrow.optics.internal.Profunctor
import arrow.optics.internal.Traversing
import arrow.optics.internal.WanderF
import arrow.optics.prism

fun <S> Optic.Companion.filter(pred: (S) -> Boolean): Optic<PrismK, S, S, S, S> =
  prism({ s -> if (pred(s)) Either.Right(s) else Either.Left(s) }, ::identity)

@JvmName("filter_prism")
fun <K : PrismK, S, A> Optic<K, S, S, A, A>.filter(
  pred: (A) -> Boolean
): Optic<PrismK, S, S, A, A> = compose(Optic.filter(pred))

@JvmName("filter_affineTraversal")
fun <K : AffineTraversalK, S, A> Optic<K, S, S, A, A>.filter(
  pred: (A) -> Boolean
): Optic<AffineTraversalK, S, S, A, A> = compose(Optic.filter(pred))

@JvmName("filter_traversal")
fun <K : TraversalK, S, A> Optic<K, S, S, A, A>.filter(
  pred: (A) -> Boolean
): Optic<TraversalK, S, S, A, A> = compose(Optic.filter(pred))

@JvmName("filter_affineFold")
fun <K : AffineFoldK, S, A> Optic<K, S, S, A, A>.filter(
  pred: (A) -> Boolean
): Optic<AffineFoldK, S, S, A, A> = compose(Optic.filter(pred))

@JvmName("filter_fold")
fun <K : FoldK, S, A> Optic<K, S, S, A, A>.filter(
  pred: (A) -> Boolean
): Optic<FoldK, S, S, A, A> = compose(Optic.filter(pred))

fun <K : FoldK, S> Optic.Companion.filteredBy(filter: Optic<K, S, S, Any?, Any?>): Optic<PrismK, S, S, S, S> =
  filter { s -> s.has(filter) }

@JvmName("filteredBy_prism")
fun <K1 : PrismK, K2 : FoldK, S, A> Optic<K1, S, S, A, A>.filteredBy(
  filter: Optic<K2, A, A, Any?, Any?>
): Optic<PrismK, S, S, A, A> =
  compose(Optic.filteredBy(filter))

@JvmName("filteredBy_affineTraversal")
fun <K1 : AffineTraversalK, K2 : FoldK, S, A> Optic<K1, S, S, A, A>.filteredBy(
  filter: Optic<K2, A, A, Any?, Any?>
): Optic<AffineTraversalK, S, S, A, A> =
  compose(Optic.filteredBy(filter))

@JvmName("filteredBy_traversal")
fun <K1 : TraversalK, K2 : FoldK, S, A> Optic<K1, S, S, A, A>.filteredBy(
  filter: Optic<K2, A, A, Any?, Any?>
): Optic<TraversalK, S, S, A, A> =
  compose(Optic.filteredBy(filter))

@JvmName("filteredBy_affineFold")
fun <K1 : AffineFoldK, K2 : FoldK, S, A> Optic<K1, S, S, A, A>.filteredBy(
  filter: Optic<K2, A, A, Any?, Any?>
): Optic<AffineFoldK, S, S, A, A> =
  compose(Optic.filteredBy(filter))

@JvmName("filteredBy_fold")
fun <K1 : FoldK, K2 : FoldK, S, A> Optic<K1, S, S, A, A>.filteredBy(
  filter: Optic<K2, A, A, Any?, Any?>
): Optic<FoldK, S, S, A, A> =
  compose(Optic.filteredBy(filter))

