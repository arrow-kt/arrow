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

typealias Fold<S, A> = Optic_<FoldK, Any?, S, A>

fun <S, A> Optic.Companion.folding(
  ff: FoldF<S, A>
): Fold<S, A> =
  object : Fold<S, A> {
    override fun <P, J> Profunctor<P>.transform(focus: Pro<P, J, A, A>): Pro<P, (Any?) -> J, S, S> =
      (this as Traversing<P>).run {
        // Casts are safe because P is guaranteed to be Forget here
        (focus as Pro<P, J, A, Unit>).wander(object : WanderF<S, Unit, A, Unit> {
          override fun <F> invoke(AF: Applicative<F>, source: S, f: (A) -> Kind<F, Unit>): Kind<F, Unit> =
            ff.invoke(AF, source, f)
        }) as Pro<P, J, S, S>
      }.ixMap { it(Unit) }
  }

interface FoldF<S, A> {
  operator fun <F> invoke(AF: Applicative<F>, s: S, f: (A) -> Kind<F, Unit>): Kind<F, Unit>
}

typealias IxFold<I, S, A> = Optic_<FoldK, I, S, A>

fun <I, S, A> Optic.Companion.ixFolding(
  ff: IxFoldF<I, S, A>
): IxFold<I, S, A> =
  object : IxFold<I, S, A> {
    override fun <P, J> Profunctor<P>.transform(focus: Pro<P, J, A, A>): Pro<P, (I) -> J, S, S> =
      (this as Traversing<P>).run {
        // Casts are safe because P is guaranteed to be Forget here
        (focus as Pro<P, J, A, Unit>).iwander(object : IxWanderF<I, S, Unit, A, Unit> {
          override fun <F> invoke(AF: Applicative<F>, source: S, f: (I, A) -> Kind<F, Unit>): Kind<F, Unit> =
            ff.invoke(AF, source, f)
        }) as Pro<P, J, S, S>
      }.ixMap { f -> { i: I -> f(i) } }
  }

interface IxFoldF<I, S, A> {
  operator fun <F> invoke(AF: Applicative<F>, s: S, f: (I, A) -> Kind<F, Unit>): Kind<F, Unit>
}

fun <K : FoldK, I, S, A, R> S.foldOf(optic: Optic_<K, I, S, A>, MR: Monoid<R>, f: (A) -> R): R =
  Forget.traversing(MR).run { optic.run { transform(Forget<R, Nothing, A, A>(f)) } }
    .fix().f(this)

fun <K : FoldK, I, S, A> S.foldOf(optic: Optic_<K, I, S, A>, MA: Monoid<A>): A =
  foldOf(optic, MA, ::identity)

fun <K : FoldK, I, S, A> S.collectOf(optic: Optic_<K, I, S, A>): List<A> =
  foldOf(optic, Monoid.list()) { listOf(it) }

fun <K : FoldK, I, S> S.sumOf(optic: Optic_<K, I, S, Int>): Int =
  foldOf(optic, Monoid.int())

fun <K : FoldK, I, S, A> S.lengthOf(optic: Optic_<K, I, S, A>): Int =
  sumOf(optic.get { 1 })

fun <K : FoldK, I, S, A> S.has(optic: Optic_<K, I, S, A>): Boolean =
  foldOf(optic, object : Monoid<Boolean> {
    override fun empty(): Boolean = false
    override fun Boolean.combine(b: Boolean): Boolean = this || b
  }) { true }

fun <K : FoldK, I, S, A, F> S.traverseOf_(optic: Optic_<K, I, S, A>, AF: Applicative<F>, f: (A) -> Kind<F, Unit>): Kind<F, Unit> =
  // This is suboptimal as it creates const elements needlessly, maybe create a new type that goes to Kind<F, Unit> directly?
  Forget.traversing(object : Monoid<Kind<F, Unit>> {
    override fun empty(): Kind<F, Unit> = AF.pure(Unit)
    override fun Kind<F, Unit>.combine(b: Kind<F, Unit>): Kind<F, Unit> =
      AF.ap(AF.map(this) { _ -> { _: Unit -> Unit } }, b)
  }).run { optic.run { transform(Forget<Kind<F, Unit>, Nothing, A, A>(f)) } }.fix().f(this)

fun <K : FoldK, I, S, A, R> S.ixFoldOf(optic: Optic_<K, I, S, A>, MR: Monoid<R>, f: (I, A) -> R): R =
  IxForget.traversing(MR).run { optic.run { transform(IxForget(f)) } }
    .fix().f(::identity, this)

fun <K : FoldK, I, S, A> S.ixCollectOf(optic: Optic_<K, I, S, A>): List<Pair<I, A>> =
  ixFoldOf(optic, Monoid.list()) { i, a -> listOf(i to a) }

fun <K : FoldK, I, S, A, F> S.ixTraverseOf_(optic: Optic_<K, I, S, A>, AF: Applicative<F>, f: (I, A) -> Kind<F, Unit>): Kind<F, Unit> =
  // This is suboptimal as it creates const elements needlessly, maybe create a new type that goes to Kind<F, Unit> directly?
  IxForget.traversing(object : Monoid<Kind<F, Unit>> {
    override fun empty(): Kind<F, Unit> = AF.pure(Unit)
    override fun Kind<F, Unit>.combine(b: Kind<F, Unit>): Kind<F, Unit> =
      AF.ap(AF.map(this) { _ -> { _: Unit -> Unit } }, b)
  }).run { optic.run { transform(IxForget(f)) } }.fix().f(::identity, this)
