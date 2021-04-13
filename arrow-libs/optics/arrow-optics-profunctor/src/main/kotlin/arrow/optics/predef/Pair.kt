package arrow.optics.predef

import arrow.optics.AffineFoldK
import arrow.optics.AffineTraversalK
import arrow.optics.Fold
import arrow.optics.FoldF
import arrow.optics.FoldK
import arrow.optics.GetterK
import arrow.optics.Lens
import arrow.optics.LensK
import arrow.optics.Optic
import arrow.optics.PLens
import arrow.optics.PTraversal
import arrow.optics.TraversalK
import arrow.optics.compose
import arrow.optics.foldOf
import arrow.optics.folding
import arrow.optics.internal.Applicative
import arrow.optics.internal.Kind
import arrow.optics.internal.Pro
import arrow.optics.internal.Profunctor
import arrow.optics.internal.Traversing
import arrow.optics.internal.WanderF
import arrow.optics.lens
import arrow.optics.traverseOf
import arrow.optics.traverseOf_
import arrow.optics.traversing

fun <A, B, C> Optic.Companion.pairFirst(): PLens<Pair<A, C>, Pair<B, C>, A, B> =
  Optic.lens({ (a, _) -> a }, { (_, c), b -> b to c })

// Ugly^^
@JvmName("first_pair_lens")
fun <K : LensK, S, T, A, B, C> Optic<K, S, T, Pair<A, C>, Pair<B, C>>.first(): Optic<LensK, S, T, A, B> =
  compose(Optic.pairFirst())

@JvmName("first_affine_traversal")
fun <K : AffineTraversalK, S, T, A, B, C> Optic<K, S, T, Pair<A, C>, Pair<B, C>>.first(): Optic<AffineTraversalK, S, T, A, B> =
  compose(Optic.pairFirst())

@JvmName("first_pair_traversal")
fun <K : TraversalK, S, T, A, B, C> Optic<K, S, T, Pair<A, C>, Pair<B, C>>.first(): Optic<TraversalK, S, T, A, B> =
  compose(Optic.pairFirst())

@JvmName("first_pair_getter")
fun <K : GetterK, S, T, A, B, C> Optic<K, S, T, Pair<A, C>, Pair<B, C>>.first(): Optic<GetterK, S, T, A, B> =
  compose(Optic.pairFirst())

@JvmName("first_pair_affine_fold")
fun <K : AffineFoldK, S, T, A, B, C> Optic<K, S, T, Pair<A, C>, Pair<B, C>>.first(): Optic<AffineFoldK, S, T, A, B> =
  compose(Optic.pairFirst())

@JvmName("first_pair_fold")
fun <K : FoldK, S, T, A, B, C> Optic<K, S, T, Pair<A, C>, Pair<B, C>>.first(): Optic<FoldK, S, T, A, B> =
  compose(Optic.pairFirst())

fun <A, B, C> Optic.Companion.pairSecond(): PLens<Pair<A, B>, Pair<A, C>, B, C> =
  Optic.lens({ (_, b) -> b }, { (a, _), c -> a to c })

// Ugly^^
@JvmName("second_pair_lens")
fun <K : LensK, S, T, A, B, C> Optic<K, S, T, Pair<A, B>, Pair<A, C>>.second(): Optic<LensK, S, T, B, C> =
  compose(Optic.pairSecond())

@JvmName("second_affine_traversal")
fun <K : AffineTraversalK, S, T, A, B, C> Optic<K, S, T, Pair<A, B>, Pair<A, C>>.second(): Optic<AffineTraversalK, S, T, B, C> =
  compose(Optic.pairSecond())

@JvmName("second_pair_traversal")
fun <K : TraversalK, S, T, A, B, C> Optic<K, S, T, Pair<A, B>, Pair<A, C>>.second(): Optic<TraversalK, S, T, B, C> =
  compose(Optic.pairSecond())

@JvmName("second_pair_getter")
fun <K : GetterK, S, T, A, B, C> Optic<K, S, T, Pair<A, B>, Pair<A, C>>.second(): Optic<GetterK, S, T, B, C> =
  compose(Optic.pairSecond())

@JvmName("second_pair_affine_fold")
fun <K : AffineFoldK, S, T, A, B, C> Optic<K, S, T, Pair<A, B>, Pair<A, C>>.second(): Optic<AffineFoldK, S, T, B, C> =
  compose(Optic.pairSecond())

@JvmName("second_pair_fold")
fun <K : FoldK, S, T, A, B, C> Optic<K, S, T, Pair<A, B>, Pair<A, C>>.second(): Optic<FoldK, S, T, B, C> =
  compose(Optic.pairSecond())

fun <A, B> Optic.Companion.pairEvery(): Optic<TraversalK, Pair<A, A>, Pair<B, B>, A, B> =
  Optic.traversing(object : WanderF<Pair<A, A>, Pair<B, B>, A, B> {
    override fun <F> invoke(AF: Applicative<F>, source: Pair<A, A>, f: (A) -> Kind<F, B>): Kind<F, Pair<B, B>> =
      AF.ap(AF.map(f(source.first)) { fst -> { snd: B -> fst to snd } }, f(source.second))
  })

@JvmName("pair_every_traversal")
fun <K : TraversalK, S, T, A, B> Optic<K, S, T, Pair<A, A>, Pair<B, B>>.every(): Optic<TraversalK, S, T, A, B> =
  compose(Optic.pairEvery())

@JvmName("pair_every_fold")
fun <K : FoldK, S, T, A, B> Optic<K, S, T, Pair<A, A>, Pair<B, B>>.every(): Optic<FoldK, S, T, A, B> =
  compose(Optic.pairEvery())

@JvmName("pair_beside_traversal")
fun <K1 : TraversalK, K2 : TraversalK, A, B, C, D, E, F> Optic.Companion.pairBeside(
  l: Optic<K1, A, B, E, F>,
  r: Optic<K2, C, D, E, F>
): PTraversal<Pair<A, C>, Pair<B, D>, E, F> =
  traversing(object : WanderF<Pair<A, C>, Pair<B, D>, E, F> {
    override fun <G> invoke(AF: Applicative<G>, source: Pair<A, C>, f: (E) -> Kind<G, F>): Kind<G, Pair<B, D>> =
      AF.ap(
        AF.map(source.first.traverseOf(l, AF, f)) { b -> { d: D -> b to d } },
        source.second.traverseOf(r, AF, f)
      )
  })

@JvmName("pair_beside_fold")
fun <K1 : FoldK, K2 : FoldK, A, C, E> Optic.Companion.pairBeside(
  l: Optic<K1, A, A, E, E>,
  r: Optic<K2, C, C, E, E>
): Fold<Pair<A, C>, E> =
  folding(object : FoldF<Pair<A, C>, E> {
    override fun <F> invoke(AF: Applicative<F>, s: Pair<A, C>, f: (E) -> Kind<F, Unit>): Kind<F, Unit> =
      AF.ap(
        AF.map(s.first.traverseOf_(l, AF, f)) { { } },
        s.second.traverseOf_(r, AF, f)
      )
  })

@JvmName("pair_beside_traversal")
fun <K1 : TraversalK, K2 : TraversalK, K3 : TraversalK, S, T, A, B, C, D, E, F> Optic<K1, S, T, Pair<A, B>, Pair<C, D>>.beside(
  l: Optic<K2, A, C, E, F>,
  r: Optic<K3, B, D, E, F>
): PTraversal<S, T, E, F> =
  compose(Optic.pairBeside(l, r))

@JvmName("pair_beside_fold")
fun <K1 : FoldK, K2 : FoldK, K3 : FoldK, S, A, B, C> Optic<K1, S, S, Pair<A, B>, Pair<A, B>>.beside(
  l: Optic<K2, A, A, C, C>,
  r: Optic<K3, B, B, C, C>
): Fold<S, C> =
  compose(Optic.pairBeside(l, r))
