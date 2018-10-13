package arrow.free.instances

import arrow.Kind
import arrow.core.Either
import arrow.core.FunctionK
import arrow.deprecation.ExtensionsDSLDeprecated
import arrow.free.*
import arrow.extension
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.free.ap as freeAp
import arrow.free.flatMap as freeFlatMap
import arrow.free.map as freeMap

@extension
interface FreeFunctorInstance<S> : Functor<FreePartialOf<S>> {

  override fun <A, B> Kind<FreePartialOf<S>, A>.map(f: (A) -> B): Free<S, B> =
    fix().freeMap(f)
}

@extension
interface FreeApplicativeInstance<S> : Applicative<FreePartialOf<S>>, FreeFunctorInstance<S> {

  override fun <A> just(a: A): Free<S, A> = Free.just(a)

  override fun <A, B> Kind<FreePartialOf<S>, A>.map(f: (A) -> B): Free<S, B> =
    fix().freeMap(f)

  override fun <A, B> Kind<FreePartialOf<S>, A>.ap(ff: Kind<FreePartialOf<S>, (A) -> B>): Free<S, B> =
    fix().freeAp(ff)
}

@extension
interface FreeMonadInstance<S> : Monad<FreePartialOf<S>>, FreeApplicativeInstance<S> {

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
interface FreeEq<F, G, A> : Eq<Kind<FreePartialOf<F>, A>> {

  fun MG(): Monad<G>

  fun FK(): FunctionK<F, G>

  override fun Kind<FreePartialOf<F>, A>.eqv(b: Kind<FreePartialOf<F>, A>): Boolean =
    fix().foldMap(FK(), MG()) == b.fix().foldMap(FK(), MG())
}

class FreeContext<S> : FreeMonadInstance<S>

class FreeContextPartiallyApplied<S> {
  @Deprecated(ExtensionsDSLDeprecated)
  infix fun <A> extensions(f: FreeContext<S>.() -> A): A =
    f(FreeContext())
}

fun <S> ForFree(): FreeContextPartiallyApplied<S> =
  FreeContextPartiallyApplied()