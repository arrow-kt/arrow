package arrow.core.extensions

import arrow.Kind
import arrow.core.*
import arrow.core.extensions.function0.monad.monad
import arrow.core.extensions.function1.monad.monad
import arrow.extension
import arrow.typeclasses.*
import arrow.typeclasses.suspended.monad.Fx

@extension
interface Function1Semigroup<A, B> : Semigroup<Function1<A, B>> {
  fun SB(): Semigroup<B>

  override fun Function1<A, B>.combine(b: Function1<A, B>): Function1<A, B> = { a: A -> SB().run { invoke(a).combine(b(a)) } }.k()
}

@extension
interface Function1Monoid<A, B> : Monoid<Function1<A, B>>, Function1Semigroup<A, B> {
  fun MB(): Monoid<B>

  override fun SB() = MB()

  override fun empty(): Function1<A, B> = Function1 { MB().run { empty() } }
}

@extension
interface Function1Functor<I> : Functor<Function1PartialOf<I>> {
  override fun <A, B> Function1Of<I, A>.map(f: (A) -> B): Function1<I, B> =
    fix().map(f)
}

@extension
interface Function1Contravariant<O> : Contravariant<Conested<ForFunction1, O>> {
  override fun <A, B> Kind<Conested<ForFunction1, O>, A>.contramap(f: (B) -> A): Kind<Conested<ForFunction1, O>, B> =
    counnest().fix().contramap(f).conest()

  fun <A, B> Function1Of<A, O>.contramapC(f: (B) -> A): Function1Of<B, O> =
    conest().contramap(f).counnest()
}

@extension
interface Function1Profunctor : Profunctor<ForFunction1> {
  override fun <A, B, C, D> Function1Of<A, B>.dimap(fl: (C) -> A, fr: (B) -> D): Function1<C, D> =
    (fr compose fix().f compose fl).k()
}

@extension
interface Function1Applicative<I> : Applicative<Function1PartialOf<I>>, Function1Functor<I> {

  override fun <A> just(a: A): Function1<I, A> =
    Function1.just(a)

  override fun <A, B> Function1Of<I, A>.map(f: (A) -> B): Function1<I, B> =
    fix().map(f)

  override fun <A, B> Function1Of<I, A>.ap(ff: Function1Of<I, (A) -> B>): Function1<I, B> =
    fix().ap(ff)
}

@extension
interface Function1Monad<I> : Monad<Function1PartialOf<I>>, Function1Applicative<I> {

  override fun <A, B> Function1Of<I, A>.map(f: (A) -> B): Function1<I, B> =
    fix().map(f)

  override fun <A, B> Function1Of<I, A>.ap(ff: Function1Of<I, (A) -> B>): Function1<I, B> =
    fix().ap(ff)

  override fun <A, B> Function1Of<I, A>.flatMap(f: (A) -> Function1Of<I, B>): Function1<I, B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> Function1Of<I, Either<A, B>>): Function1<I, B> =
    Function1.tailRecM(a, f)
}

@extension
interface Function1Fx<A> : Fx<Function1PartialOf<A>> {
  override fun monad(): Monad<Function1PartialOf<A>> = Function1.monad()
}

@extension
interface Function1Category : Category<ForFunction1> {
  override fun <A> id(): Function1<A, A> = Function1.id()

  override fun <A, B, C> Function1Of<B, C>.compose(arr: Function1Of<A, B>): Function1Of<A, C> = fix().compose(arr.fix())
}
