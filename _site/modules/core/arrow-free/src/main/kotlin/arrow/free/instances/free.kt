package arrow.free.instances

import arrow.Kind
import arrow.core.Either
import arrow.core.FunctionK
import arrow.free.*
import arrow.instance
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.free.ap as freeAp
import arrow.free.flatMap as freeFlatMap
import arrow.free.map as freeMap

@instance(Free::class)
interface FreeFunctorInstance<S> : Functor<FreePartialOf<S>> {

  override fun <A, B> Kind<FreePartialOf<S>, A>.map(f: (A) -> B): Free<S, B> =
    fix().freeMap(f)
}

@instance(Free::class)
interface FreeApplicativeInstance<S> : FreeFunctorInstance<S>, Applicative<FreePartialOf<S>> {

  override fun <A> just(a: A): Free<S, A> = Free.just(a)

  override fun <A, B> Kind<FreePartialOf<S>, A>.map(f: (A) -> B): Free<S, B> =
    fix().freeMap(f)

  override fun <A, B> Kind<FreePartialOf<S>, A>.ap(ff: Kind<FreePartialOf<S>, (A) -> B>): Free<S, B> =
    fix().freeAp(ff)
}

@instance(Free::class)
interface FreeMonadInstance<S> : FreeApplicativeInstance<S>, Monad<FreePartialOf<S>> {

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

interface FreeEq<F, G, A> : Eq<Kind<FreePartialOf<F>, A>> {

  fun MG(): Monad<G>

  fun FK(): FunctionK<F, G>

  override fun Kind<FreePartialOf<F>, A>.eqv(b: Kind<FreePartialOf<F>, A>): Boolean =
    fix().foldMap(FK(), MG()) == b.fix().foldMap(FK(), MG())
}

@Suppress("UNUSED_PARAMETER")
fun <F, G, A> Free.Companion.eq(FK: FunctionK<F, G>, MG: Monad<G>, dummy: Unit = Unit): FreeEq<F, G, A> =
  object : FreeEq<F, G, A> {
    override fun FK(): FunctionK<F, G> = FK

    override fun MG(): arrow.typeclasses.Monad<G> = MG
  }
