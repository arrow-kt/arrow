package arrow.core.extensions

import arrow.Kind
import arrow.core.extensions.function1.monad.monad
import arrow.core.Either
import arrow.core.Function1
import arrow.core.ForFunction1
import arrow.core.Function1Of
import arrow.core.Function1PartialOf
import arrow.core.Tuple2
import arrow.core.compose
import arrow.extension
import arrow.core.fix
import arrow.core.invoke
import arrow.core.k
import arrow.typeclasses.Applicative
import arrow.typeclasses.Apply
import arrow.typeclasses.Category
import arrow.typeclasses.Conested
import arrow.typeclasses.Contravariant
import arrow.typeclasses.Decidable
import arrow.typeclasses.Divide
import arrow.typeclasses.Divisible
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadSyntax
import arrow.typeclasses.Monoid
import arrow.typeclasses.Profunctor
import arrow.typeclasses.Semigroup
import arrow.typeclasses.conest
import arrow.typeclasses.counnest

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
interface Function1Divide<O> : Divide<Conested<ForFunction1, O>>, Function1Contravariant<O> {
  fun MO(): Monoid<O>

  override fun <A, B, Z> divide(fa: Kind<Conested<ForFunction1, O>, A>, fb: Kind<Conested<ForFunction1, O>, B>, f: (Z) -> Tuple2<A, B>): Kind<Conested<ForFunction1, O>, Z> =
    Function1<Z, O> {
      val (a, b) = f(it)

      MO().run {
        fa.counnest().invoke(a) +
          fb.counnest().invoke(b)
      }
    }.conest()

  fun <A, B, Z> divideC(fa: Function1Of<A, O>, fb: Function1Of<A, O>, f: (Z) -> Tuple2<A, B>): Function1Of<Z, O> =
    divide(fa.conest(), fb.conest(), f).counnest()
}

@extension
interface Function1Divisible<O> : Divisible<Conested<ForFunction1, O>>, Function1Divide<O> {
  override fun MO(): Monoid<O> = MOO()
  fun MOO(): Monoid<O>

  override fun <A> conquer(): Kind<Conested<ForFunction1, O>, A> =
    Function1<A, O> {
      MOO().empty()
    }.conest()

  fun <A> conquerC(): Function1Of<A, O> =
    conquer<A>().counnest()
}

@extension
interface Function1Decidable<O> : Decidable<Conested<ForFunction1, O>>, Function1Divisible<O> {
  override fun MOO(): Monoid<O> = MOOO()
  fun MOOO(): Monoid<O>

  override fun <A, B, Z> choose(fa: Kind<Conested<ForFunction1, O>, A>, fb: Kind<Conested<ForFunction1, O>, B>, f: (Z) -> Either<A, B>): Kind<Conested<ForFunction1, O>, Z> =
    Function1<Z, O> {
      f(it).fold({
        fa.counnest().invoke(it)
      }, {
        fb.counnest().invoke(it)
      })
    }.conest()

  fun <A, B, Z> chooseC(fa: Function1Of<A, O>, fb: Function1Of<B, O>, f: (Z) -> Either<A, B>): Function1Of<Z, O> =
    choose(fa.conest(), fb.conest(), f).counnest()
}

@extension
interface Function1Profunctor : Profunctor<ForFunction1> {
  override fun <A, B, C, D> Function1Of<A, B>.dimap(fl: (C) -> A, fr: (B) -> D): Function1<C, D> =
    (fr compose fix().f compose fl).k()
}

@extension
interface Function1Apply<I> : Apply<Function1PartialOf<I>>, Function1Functor<I> {

  override fun <A, B> Function1Of<I, A>.map(f: (A) -> B): Function1<I, B> =
    fix().map(f)

  override fun <A, B> Function1Of<I, A>.ap(ff: Function1Of<I, (A) -> B>): Function1<I, B> =
    fix().ap(ff)
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

fun <A, B> Function1.Companion.fx(c: suspend MonadSyntax<Function1PartialOf<A>>.() -> B): Function1<A, B> =
  Function1.monad<A>().fx.monad(c).fix()

@extension
interface Function1Category : Category<ForFunction1> {
  override fun <A> id(): Function1<A, A> = Function1.id()

  override fun <A, B, C> Function1Of<B, C>.compose(arr: Function1Of<A, B>): Function1Of<A, C> = fix().compose(arr.fix())
}
