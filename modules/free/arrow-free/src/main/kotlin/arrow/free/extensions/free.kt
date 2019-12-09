package arrow.free.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.FunctionK
import arrow.extension
import arrow.free.Free
import arrow.free.FreeOf
import arrow.free.FreePartialOf
import arrow.free.extensions.free.foldable.foldLeft
import arrow.free.extensions.free.foldable.foldRight
import arrow.free.fix
import arrow.free.foldMap
import arrow.typeclasses.Applicative
import arrow.typeclasses.Apply
import arrow.typeclasses.Eq
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.Traverse
import arrow.undocumented
import arrow.free.ap as freeAp
import arrow.free.flatMap as freeFlatMap
import arrow.free.map as freeMap

@extension
@undocumented
interface FreeFunctor<S> : Functor<FreePartialOf<S>> {

  override fun <A, B> Kind<FreePartialOf<S>, A>.map(f: (A) -> B): Free<S, B> =
    fix().freeMap(f)
}

@extension
@undocumented
interface FreeApply<S> : Apply<FreePartialOf<S>>, FreeFunctor<S> {

  override fun <A, B> Kind<FreePartialOf<S>, A>.map(f: (A) -> B): Free<S, B> =
    fix().freeMap(f)

  override fun <A, B> Kind<FreePartialOf<S>, A>.ap(ff: Kind<FreePartialOf<S>, (A) -> B>): Free<S, B> =
    fix().freeAp(ff)
}

@extension
@undocumented
interface FreeApplicative<S> : Applicative<FreePartialOf<S>>, FreeFunctor<S> {

  override fun <A> just(a: A): Free<S, A> = Free.just(a)

  override fun <A, B> Kind<FreePartialOf<S>, A>.map(f: (A) -> B): Free<S, B> =
    fix().freeMap(f)

  override fun <A, B> Kind<FreePartialOf<S>, A>.ap(ff: Kind<FreePartialOf<S>, (A) -> B>): Free<S, B> =
    fix().freeAp(ff)
}

@extension
@undocumented
interface FreeMonad<S> : Monad<FreePartialOf<S>>, FreeApplicative<S> {

  override fun <A, B> Kind<FreePartialOf<S>, A>.map(f: (A) -> B): Free<S, B> =
    fix().freeMap(f)

  override fun <A, B> Kind<FreePartialOf<S>, A>.ap(ff: Kind<FreePartialOf<S>, (A) -> B>): Free<S, B> =
    fix().freeAp(ff)

  override fun <A, B> Kind<FreePartialOf<S>, A>.flatMap(f: (A) -> Kind<FreePartialOf<S>, B>): Free<S, B> =
    fix().freeFlatMap { f(it).fix() }

  override fun <A, B> tailRecM(a: A, f: (A) -> FreeOf<S, Either<A, B>>): Free<S, B> = f(a).fix().flatMap {
    when (it) {
      is Either.Left -> tailRecM(it.a, f)
      is Either.Right -> just(it.b)
    }
  }
}

@extension
@undocumented
interface FreeEq<F, G, A> : Eq<Kind<FreePartialOf<F>, A>> {

  fun MG(): Monad<G>

  fun FK(): FunctionK<F, G>

  override fun Kind<FreePartialOf<F>, A>.eqv(b: Kind<FreePartialOf<F>, A>): Boolean =
    fix().foldMap(FK(), MG()) == b.fix().foldMap(FK(), MG())
}

@extension
@undocumented
interface FreeFoldable<F> : Foldable<FreePartialOf<F>> {
  fun FF(): Foldable<F>
  override fun <A, B> Kind<FreePartialOf<F>, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldStep(
      onPure = { a -> f(b, a) },
      onSuspend = { fa: Kind<F, A> -> FF().run { fa.foldLeft(b, f) } },
      onFlatMapped = { fa: Kind<F, A>, g: (A) -> Free<F, A> -> FF().run { fa.foldLeft(b) { bb: B, a: A -> g(a).foldLeft(bb, f) } } }
    )

  override fun <A, B> Kind<FreePartialOf<F>, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldStep(
      onPure = { a -> f(a, lb) },
      onSuspend = { fa: Kind<F, A> -> FF().run { fa.foldRight(lb, f) } },
      onFlatMapped = { fa: Kind<F, A>, g: (A) -> Free<F, A> -> FF().run { fa.foldRight(lb) { a: A, lbb: Eval<B> -> g(a).foldRight(lbb, f) } } }
    )
}

@extension
@undocumented
interface FreeTraverse<F> : Traverse<FreePartialOf<F>> {
  fun FT(): Traverse<F>
  fun FF(): Foldable<F> = FT()

  override fun <G, A, B> Kind<FreePartialOf<F>, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Kind<FreePartialOf<F>, B>> =
    when (val x: Either<Kind<F, Free<F, A>>, A> = fix().resume(FT())) {
      is Either.Right -> AP.run { f(x.b).map { Free.Pure<F, B>(it) } }
      is Either.Left -> AP.run { FT().run { x.a.traverse(AP) { free -> free.traverse(AP, f) }.map { s -> Free.roll(s) } } }
    }

  override fun <A, B> Kind<FreePartialOf<F>, A>.foldLeft(b: B, f: (B, A) -> B): B =
    foldLeft(FF(), b, f)

  override fun <A, B> Kind<FreePartialOf<F>, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    foldRight(FF(), lb, f)

  override fun <A, B> Kind<FreePartialOf<F>, A>.map(f: (A) -> B): Kind<FreePartialOf<F>, B> =
    fix().freeMap(f)
}
