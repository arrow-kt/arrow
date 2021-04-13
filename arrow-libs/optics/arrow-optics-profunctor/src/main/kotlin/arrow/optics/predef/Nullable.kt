package arrow.optics.predef

import arrow.core.Either
import arrow.optics.AffineFoldK
import arrow.optics.AffineTraversalK
import arrow.optics.FoldK
import arrow.optics.Optic
import arrow.optics.PPrism
import arrow.optics.Prism
import arrow.optics.PrismK
import arrow.optics.TraversalK
import arrow.optics.compose
import arrow.optics.prism

fun <A, B> Optic.Companion.notNull(): PPrism<A?, B?, A, B> =
  prism({ a ->
    a?.let { Either.Right(it) } ?: Either.Left(null)
  }, { b -> b })

@JvmName("notNull_prism")
fun <K : PrismK, S, T, A, B> Optic<K, S, T, A?, B?>.notNull(): Optic<PrismK, S, T, A, B> =
  compose(Optic.notNull())

@JvmName("notNull_affineTraversal")
fun <K : AffineTraversalK, S, T, A, B> Optic<K, S, T, A?, B?>.notNull(): Optic<AffineTraversalK, S, T, A, B> =
  compose(Optic.notNull())

@JvmName("notNull_traversal")
fun <K : TraversalK, S, T, A, B> Optic<K, S, T, A?, B?>.notNull(): Optic<TraversalK, S, T, A, B> =
  compose(Optic.notNull())

@JvmName("notNull_affineFold")
fun <K : AffineFoldK, S, T, A, B> Optic<K, S, T, A?, B?>.notNull(): Optic<AffineFoldK, S, T, A, B> =
  compose(Optic.notNull())

@JvmName("notNull_fold")
fun <K : FoldK, S, T, A, B> Optic<K, S, T, A?, B?>.notNull(): Optic<FoldK, S, T, A, B> =
  compose(Optic.notNull())

fun <A> Optic.Companion.isNull(): Prism<A?, Unit> =
  prism({ a ->
    a?.let { Either.Left(a) } ?: Either.Right(Unit)
  }, { null })

@JvmName("isNull_prism")
fun <K : PrismK, S, A> Optic<K, S, S, A?, A?>.isNull(): Optic<PrismK, S, S, Unit, Unit> =
  compose(Optic.isNull<A>())

@JvmName("isNull_affineTraversal")
fun <K : AffineTraversalK, S, A> Optic<K, S, S, A?, A?>.isNull(): Optic<AffineTraversalK, S, S, Unit, Unit> =
  compose(Optic.isNull())

@JvmName("isNull_traversal")
fun <K : TraversalK, S, A> Optic<K, S, S, A?, A?>.isNull(): Optic<TraversalK, S, S, Unit, Unit> =
  compose(Optic.isNull())

@JvmName("isNull_affineFold")
fun <K : AffineFoldK, S, A> Optic<K, S, S, A?, A?>.isNull(): Optic<AffineFoldK, S, S, Unit, Unit> =
  compose(Optic.isNull())

@JvmName("isNull_fold")
fun <K : FoldK, S, A> Optic<K, S, S, A?, A?>.isNull(): Optic<FoldK, S, S, Unit, Unit> =
  compose(Optic.isNull())
