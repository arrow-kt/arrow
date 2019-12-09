package arrow.fx.extensions

import arrow.Kind
import arrow.core.Either
import arrow.fx.Resource
import arrow.fx.ResourceOf
import arrow.fx.ResourcePartialOf
import arrow.fx.fix
import arrow.fx.typeclasses.Bracket
import arrow.extension
import arrow.typeclasses.Applicative
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.Monoid
import arrow.typeclasses.Selective
import arrow.typeclasses.Semigroup
import arrow.undocumented

@extension
@undocumented
interface ResourceFunctor<F, E> : Functor<ResourcePartialOf<F, E>> {
  fun BR(): Bracket<F, E>
  override fun <A, B> ResourceOf<F, E, A>.map(f: (A) -> B): Resource<F, E, B> =
    fix().map(BR(), f)
}

@extension
interface ResourceApplicative<F, E> : Applicative<ResourcePartialOf<F, E>>, ResourceFunctor<F, E> {
  override fun BR(): Bracket<F, E>

  override fun <A> just(a: A): Resource<F, E, A> = Resource.just(a, BR())
  override fun <A, B> ResourceOf<F, E, A>.ap(ff: Kind<ResourcePartialOf<F, E>, (A) -> B>): Resource<F, E, B> =
    fix().ap(BR(), ff.fix())

  override fun <A, B> Kind<ResourcePartialOf<F, E>, A>.map(f: (A) -> B): Resource<F, E, B> =
    fix().map(BR(), f)
}

@extension
interface ResourceSelective<F, E> : Selective<ResourcePartialOf<F, E>>, ResourceApplicative<F, E> {
  override fun BR(): Bracket<F, E>
  override fun <A, B> ResourceOf<F, E, Either<A, B>>.select(f: Kind<ResourcePartialOf<F, E>, (A) -> B>): Resource<F, E, B> =
    fix().flatMap { it.fold({ a -> Resource.just(a, BR()).ap(BR(), f.fix()) }, { b -> Resource.just(b, BR()) }) }
}

@extension
interface ResourceMonad<F, E> : Monad<ResourcePartialOf<F, E>>, ResourceSelective<F, E> {
  override fun BR(): Bracket<F, E>
  override fun <A, B> ResourceOf<F, E, A>.flatMap(f: (A) -> ResourceOf<F, E, B>): Resource<F, E, B> =
    fix().flatMap { f(it).fix() }

  override fun <A, B> tailRecM(a: A, f: (A) -> Kind<ResourcePartialOf<F, E>, Either<A, B>>): Resource<F, E, B> =
    Resource.tailRecM(BR(), a, f)

  override fun <A, B> Kind<ResourcePartialOf<F, E>, A>.map(f: (A) -> B): Resource<F, E, B> =
    fix().map(BR(), f)

  override fun <A, B> ResourceOf<F, E, A>.ap(ff: Kind<ResourcePartialOf<F, E>, (A) -> B>): Resource<F, E, B> =
    fix().ap(BR(), ff)

  override fun <A, B> ResourceOf<F, E, Either<A, B>>.select(f: Kind<ResourcePartialOf<F, E>, (A) -> B>): Resource<F, E, B> =
    fix().flatMap { it.fold({ a -> Resource.just(a, BR()).ap(BR(), f.fix()) }, { b -> Resource.just(b, BR()) }) }
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
