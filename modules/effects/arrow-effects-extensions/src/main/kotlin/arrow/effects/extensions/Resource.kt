package arrow.effects.extensions

import arrow.Kind
import arrow.core.Either
import arrow.effects.Resource
import arrow.effects.ResourcePartialOf
import arrow.effects.fix
import arrow.effects.typeclasses.Bracket
import arrow.extension
import arrow.typeclasses.*
import arrow.undocumented

@extension
@undocumented
interface ResourceFunctor<F, E> : Functor<ResourcePartialOf<F, E>> {
  fun BR(): Bracket<F, E>
  override fun <A, B> Kind<ResourcePartialOf<F, E>, A>.map(f: (A) -> B): Kind<ResourcePartialOf<F, E>, B> =
    fix().map(BR(), f)
}

@extension
interface ResourceApplicative<F, E> : Applicative<ResourcePartialOf<F, E>> {
  fun BR(): Bracket<F, E>
  override fun <A, B> Kind<ResourcePartialOf<F, E>, A>.ap(ff: Kind<ResourcePartialOf<F, E>, (A) -> B>): Kind<ResourcePartialOf<F, E>, B> =
    fix().ap(BR(), ff.fix())

  override fun <A> just(a: A): Kind<ResourcePartialOf<F, E>, A> = Resource.just(a, BR())
}

@extension
interface ResourceSelective<F, E> : Selective<ResourcePartialOf<F, E>> {
  fun BR(): Bracket<F, E>
  override fun <A> just(a: A): Kind<ResourcePartialOf<F, E>, A> = Resource.just(a, BR())
  override fun <A, B> Kind<ResourcePartialOf<F, E>, A>.ap(ff: Kind<ResourcePartialOf<F, E>, (A) -> B>): Kind<ResourcePartialOf<F, E>, B> =
    fix().ap(ff.fix())

  override fun <A, B> Kind<ResourcePartialOf<F, E>, Either<A, B>>.select(f: Kind<ResourcePartialOf<F, E>, (A) -> B>): Kind<ResourcePartialOf<F, E>, B> =
    fix().flatMap { it.fold({ Resource.just(it, BR()).ap(BR(), f.fix()) }, { Resource.just(it, BR()) }) }
}

@extension
interface ResourceMonad<F, E> : Monad<ResourcePartialOf<F, E>> {
  fun BR(): Bracket<F, E>
  override fun <A> just(a: A): Kind<ResourcePartialOf<F, E>, A> = Resource.just(a, BR())
  override fun <A, B> Kind<ResourcePartialOf<F, E>, A>.flatMap(f: (A) -> Kind<ResourcePartialOf<F, E>, B>): Kind<ResourcePartialOf<F, E>, B> =
    fix().flatMap { f(it).fix() }

  override fun <A, B> tailRecM(a: A, f: (A) -> Kind<ResourcePartialOf<F, E>, Either<A, B>>): Kind<ResourcePartialOf<F, E>, B> =
    f(a).flatMap {
      it.fold({
        tailRecM(it, f).fix()
      }, {
        Resource.just(it, BR())
      })
    }
}

@extension
interface ResourceSemigroup<F, E, A> : Semigroup<Resource<F, E, A>> {
  fun SR(): Semigroup<A>
  fun BR(): Bracket<F, E>
  override fun Resource<F, E, A>.combine(b: Resource<F, E, A>): Resource<F, E, A> = combine(b, SR(), BR())
}

@extension
interface ResourceMonoid<F, E, A> : Monoid<Resource<F, E, A>>, ResourceSemigroup<F, E, A> {
  fun MR(): Monoid<A>
  override fun BR(): Bracket<F, E>
  override fun SR(): Semigroup<A> = MR()
  override fun empty(): Resource<F, E, A> = Resource.empty(MR(), BR())
}