package arrow.effects.extensions

import arrow.Kind
import arrow.core.Either
import arrow.data.fix
import arrow.effects.ManagedT
import arrow.effects.ManagedTPartialOf
import arrow.effects.fix
import arrow.effects.typeclasses.Bracket
import arrow.extension
import arrow.typeclasses.*

@extension
interface ManagedTFunctor<F, E> : Functor<ManagedTPartialOf<F, E>> {
  override fun <A, B> Kind<ManagedTPartialOf<F, E>, A>.map(f: (A) -> B): Kind<ManagedTPartialOf<F, E>, B> = fix().map(f)
}

@extension
interface ManagedTApplicative<F, E> : Applicative<ManagedTPartialOf<F, E>> {
  fun BR(): Bracket<F, E>
  override fun <A, B> Kind<ManagedTPartialOf<F, E>, A>.ap(ff: Kind<ManagedTPartialOf<F, E>, (A) -> B>): Kind<ManagedTPartialOf<F, E>, B> =
    fix().ap(ff.fix())

  override fun <A> just(a: A): Kind<ManagedTPartialOf<F, E>, A> = ManagedT.just(a, BR())
}

@extension
interface ManagedTSelective<F, E> : Selective<ManagedTPartialOf<F, E>> {
  fun BR(): Bracket<F, E>
  override fun <A> just(a: A): Kind<ManagedTPartialOf<F, E>, A> = ManagedT.just(a, BR())
  override fun <A, B> Kind<ManagedTPartialOf<F, E>, A>.ap(ff: Kind<ManagedTPartialOf<F, E>, (A) -> B>): Kind<ManagedTPartialOf<F, E>, B> =
    fix().ap(ff.fix())

  override fun <A, B> Kind<ManagedTPartialOf<F, E>, Either<A, B>>.select(f: Kind<ManagedTPartialOf<F, E>, (A) -> B>): Kind<ManagedTPartialOf<F, E>, B> =
    fix().flatMap { it.fold({ ManagedT.just(it, BR()).ap(f.fix()) }, { ManagedT.just(it, BR()) }) }
}

@extension
interface ManagedTMonad<F, E> : Monad<ManagedTPartialOf<F, E>> {
  fun BR(): Bracket<F, E>
  override fun <A> just(a: A): Kind<ManagedTPartialOf<F, E>, A> = ManagedT.just(a, BR())
  override fun <A, B> Kind<ManagedTPartialOf<F, E>, A>.flatMap(f: (A) -> Kind<ManagedTPartialOf<F, E>, B>): Kind<ManagedTPartialOf<F, E>, B> =
    fix().flatMap { f(it).fix() }

  override fun <A, B> tailRecM(a: A, f: (A) -> Kind<ManagedTPartialOf<F, E>, Either<A, B>>): Kind<ManagedTPartialOf<F, E>, B> =
    f(a).flatMap {
      it.fold({
        tailRecM(it, f).fix()
      }, {
        ManagedT.just(it, BR())
      })
    }
}

@extension
interface ManagedTSemigroup<F, E, R> : Semigroup<ManagedT<F, E, R>> {
  fun SR(): Semigroup<R>
  override fun ManagedT<F, E, R>.combine(b: ManagedT<F, E, R>): ManagedT<F, E, R> = combine(b, SR())
}

@extension
interface ManagedTMonoid<F, E, R> : Monoid<ManagedT<F, E, R>>, ManagedTSemigroup<F, E, R> {
  fun MR(): Monoid<R>
  fun BR(): Bracket<F, E>
  override fun SR(): Semigroup<R> = MR()
  override fun empty(): ManagedT<F, E, R> = ManagedT.empty(MR(), BR())
}