package arrow.optics

import arrow.core.identity
import arrow.optics.combinators.get
import arrow.optics.internal.Applicative
import arrow.optics.internal.Forget
import arrow.optics.internal.IxForget
import arrow.optics.internal.IxWanderF
import arrow.optics.internal.Kind
import arrow.optics.internal.Pro
import arrow.optics.internal.Profunctor
import arrow.optics.internal.Traversing
import arrow.optics.internal.WanderF
import arrow.optics.internal.fix
import arrow.typeclasses.Monoid

typealias Fold<S, A> = Optic<FoldK, Any?, S, Nothing, A, Nothing>

fun <S, A> Optic.Companion.folding(
  ff: FoldF<S, A>
): Fold<S, A> =
  object : Fold<S, A> {
    override fun <P, J> Profunctor<P>.transform(focus: Pro<P, J, A, Nothing>): Pro<P, (Any?) -> J, S, Nothing> =
      (this as Traversing<P>).run {
        // Casts are safe because P is guaranteed to be Forget here
        focus.wander(object : WanderF<S, Nothing, A, Nothing> {
          override fun <F> invoke(AF: Applicative<F>, source: S, f: (A) -> Kind<F, Nothing>): Kind<F, Nothing> =
            // These casts are somewhat safe
            ff.invoke(AF, source, f as (A) -> Kind<F, Unit>) as Kind<F, Nothing>
        })
      }.ixMap { it(Unit) }
  }

interface FoldF<S, A> {
  operator fun <F> invoke(AF: Applicative<F>, s: S, f: (A) -> Kind<F, Unit>): Kind<F, Unit>
}

typealias IxFold<I, S, A> = Optic<FoldK, I, S, Nothing, A, Nothing>

fun <I, S, A> Optic.Companion.ixFolding(
  ff: IxFoldF<I, S, A>
): IxFold<I, S, A> =
  object : IxFold<I, S, A> {
    override fun <P, J> Profunctor<P>.transform(focus: Pro<P, J, A, Nothing>): Pro<P, (I) -> J, S, Nothing> =
      (this as Traversing<P>).run {
        // Casts are safe because P is guaranteed to be Forget here
        (focus as Pro<P, J, A, Nothing>).iwander(object : IxWanderF<I, S, Nothing, A, Nothing> {
          override fun <F> invoke(AF: Applicative<F>, source: S, f: (I, A) -> Kind<F, Nothing>): Kind<F, Nothing> =
            ff.invoke(AF, source, f as (I, A) -> Kind<F, Unit>) as Kind<F, Nothing>
        })
      }.ixMap { f -> { i: I -> f(i) } }
  }

interface IxFoldF<I, S, A> {
  operator fun <F> invoke(AF: Applicative<F>, s: S, f: (I, A) -> Kind<F, Unit>): Kind<F, Unit>
}

fun <K : FoldK, I, S, T, A, B, R> S.foldOf(optic: Optic<K, I, S, T, A, B>, MR: Monoid<R>, f: (A) -> R): R =
  Forget.traversing(MR).run { optic.run { transform(Forget<R, Nothing, A, B>(f)) } }
    .fix().f(this)

fun <K : FoldK, I, S, T, A, B> S.foldOf(optic: Optic<K, I, S, T, A, B>, MA: Monoid<A>): A =
  foldOf(optic, MA, ::identity)

fun <K : FoldK, I, S, T, A, B> S.collectOf(optic: Optic<K, I, S, T, A, B>): List<A> =
  foldOf(optic, Monoid.list()) { listOf(it) }

fun <K : FoldK, I, S, T, B> S.sumOf(optic: Optic<K, I, S, T, Int, B>): Int =
  foldOf(optic, Monoid.int())

fun <K : FoldK, I, S, T, A, B> S.lengthOf(optic: Optic<K, I, S, T, A, B>): Int =
  sumOf(optic.get { 1 })

fun <K : FoldK, I, S, T, A, B> S.has(optic: Optic<K, I, S, T, A, B>): Boolean =
  foldOf(optic, object : Monoid<Boolean> {
    override fun empty(): Boolean = false
    override fun Boolean.combine(b: Boolean): Boolean = this || b
  }) { true }

fun <K : FoldK, I, S, T, A, B, F> S.traverseOf_(optic: Optic<K, I, S, T, A, B>, AF: Applicative<F>, f: (A) -> Kind<F, Unit>): Kind<F, Unit> =
  // This is suboptimal as it creates const elements needlessly, maybe create a new type that goes to Kind<F, Unit> directly?
  Forget.traversing(object : Monoid<Kind<F, Unit>> {
    override fun empty(): Kind<F, Unit> = AF.pure(Unit)
    override fun Kind<F, Unit>.combine(b: Kind<F, Unit>): Kind<F, Unit> =
      AF.ap(AF.map(this) { _ -> { _: Unit -> Unit } }, b)
  }).run { optic.run { transform(Forget<Kind<F, Unit>, Nothing, A, B>(f)) } }.fix().f(this)

fun <K : FoldK, I, S, T, A, B, R> S.ixFoldOf(optic: Optic<K, I, S, T, A, B>, MR: Monoid<R>, f: (I, A) -> R): R =
  IxForget.traversing(MR).run { optic.run { transform(IxForget(f)) } }
    .fix().f(::identity, this)

fun <K : FoldK, I, S, T, A, B> S.ixCollectOf(optic: Optic<K, I, S, T, A, B>): List<Pair<I, A>> =
  ixFoldOf(optic, Monoid.list()) { i, a -> listOf(i to a) }

fun <K : FoldK, I, S, T, A, B, F> S.ixTraverseOf_(optic: Optic<K, I, S, T, A, B>, AF: Applicative<F>, f: (I, A) -> Kind<F, Unit>): Kind<F, Unit> =
  // This is suboptimal as it creates const elements needlessly, maybe create a new type that goes to Kind<F, Unit> directly?
  IxForget.traversing(object : Monoid<Kind<F, Unit>> {
    override fun empty(): Kind<F, Unit> = AF.pure(Unit)
    override fun Kind<F, Unit>.combine(b: Kind<F, Unit>): Kind<F, Unit> =
      AF.ap(AF.map(this) { _ -> { _: Unit -> Unit } }, b)
  }).run { optic.run { transform(IxForget(f)) } }.fix().f(::identity, this)
