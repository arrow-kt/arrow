package arrow.instances

import arrow.Kind
import arrow.Kind2
import arrow.core.*
import arrow.deprecation.ExtensionsDSLDeprecated
import arrow.extension
import arrow.typeclasses.*

@extension
interface Function1FunctorInstance<I> : Functor<Function1PartialOf<I>> {
  override fun <A, B> Kind<Function1PartialOf<I>, A>.map(f: (A) -> B): Function1<I, B> =
    fix().map(f)
}

@extension
interface Function1ContravariantInstance<O> : Contravariant<Conested<ForFunction1, O>> {
  override fun <A, B> Kind<Conested<ForFunction1, O>, A>.contramap(f: (B) -> A): Kind<Conested<ForFunction1, O>, B> =
    counnest().fix().contramap(f).conest()

  fun <A, B> Function1Of<A, O>.contramapC(f: (B) -> A): Function1Of<B, O> =
    conest().contramap(f).counnest()
}

@extension
interface Function1DivideInstance<O> : Divide<Conested<ForFunction1, O>>, Function1ContravariantInstance<O> {
  fun MO(): Monoid<O>

  override fun <A, B, Z> divide(fa: Kind<Conested<ForFunction1, O>, A>, fb: Kind<Conested<ForFunction1, O>, B>, f: (Z) -> Tuple2<A, B>): Kind<Conested<ForFunction1, O>, Z> =
    Function1<Z, O> { z ->
      val (a, b) = f(z)
      MO().run {
        fa.counnest()(a) +
          fb.counnest()(b)
      }
    }.conest()

  fun <A, B, Z> divideC(fa: Kind<Function1PartialOf<A>, O>, fb: Kind<Function1PartialOf<B>, O>, f: (Z) -> Tuple2<A, B>): Kind<Function1PartialOf<Z>, O> =
    divide(fa.conest(), fb.conest(), f).counnest()
}

@extension
interface Function1DivisibleInstance<O> : Divisible<Conested<ForFunction1, O>>, Function1DivideInstance<O> {

  fun MOO(): Monoid<O>
  override fun MO(): Monoid<O> = MOO()

  override fun <A> conquer(): Kind<Conested<ForFunction1, O>, A> =
    Function1<A, O> { MO().empty() }.conest()

  fun <A> conquerC(): Kind<Function1PartialOf<A>, O> =
    conquer<A>().counnest()
}

@extension
interface Function1DecidableInstance<O> : Decidable<Conested<ForFunction1, O>>, Function1DivisibleInstance<O> {

  fun MOOO(): Monoid<O>
  override fun MOO(): Monoid<O> = MOOO()

  override fun <A, B, Z> choose(fa: Kind<Conested<ForFunction1, O>, A>, fb: Kind<Conested<ForFunction1, O>, B>, f: (Z) -> Either<A, B>): Kind<Conested<ForFunction1, O>, Z> =
    Function1<Z, O> { z ->
      f(z).fold({ a ->
        fa.counnest()(a)
      }, { b ->
        fb.counnest()(b)
      })
    }.conest()

  fun <A, B, Z> chooseC(fa: Kind<Function1PartialOf<A>, O>, fb: Kind<Function1PartialOf<B>, O>, f: (Z) -> Either<A, B>): Kind<Function1PartialOf<Z>, O> =
    choose(fa.conest(), fb.conest(), f).counnest().fix()
}

@extension
interface Function1ProfunctorInstance : Profunctor<ForFunction1> {
  override fun <A, B, C, D> Kind<Function1PartialOf<A>, B>.dimap(fl: (C) -> A, fr: (B) -> D): Function1<C, D> =
    (fr compose fix().f compose fl).k()
}

@extension
interface Function1ApplicativeInstance<I> : Applicative<Function1PartialOf<I>>, Function1FunctorInstance<I> {

  override fun <A> just(a: A): Function1<I, A> =
    Function1.just(a)

  override fun <A, B> Kind<Function1PartialOf<I>, A>.map(f: (A) -> B): Function1<I, B> =
    fix().map(f)

  override fun <A, B> Kind<Function1PartialOf<I>, A>.ap(ff: Kind<Function1PartialOf<I>, (A) -> B>): Function1<I, B> =
    fix().ap(ff)
}

@extension
interface Function1MonadInstance<I> : Monad<Function1PartialOf<I>>, Function1ApplicativeInstance<I> {

  override fun <A, B> Kind<Function1PartialOf<I>, A>.map(f: (A) -> B): Function1<I, B> =
    fix().map(f)

  override fun <A, B> Kind<Function1PartialOf<I>, A>.ap(ff: Kind<Function1PartialOf<I>, (A) -> B>): Function1<I, B> =
    fix().ap(ff)

  override fun <A, B> Kind<Function1PartialOf<I>, A>.flatMap(f: (A) -> Kind<Function1PartialOf<I>, B>): Function1<I, B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> Function1Of<I, Either<A, B>>): Function1<I, B> =
    Function1.tailRecM(a, f)
}

@extension
interface Function1CategoryInstance : Category<ForFunction1> {
  override fun <A> id(): Kind2<ForFunction1, A, A> = Function1.id()

  override fun <A, B, C> Kind2<ForFunction1, B, C>.compose(arr: Kind2<ForFunction1, A, B>): Kind2<ForFunction1, A, C> = fix().compose(arr.fix())
}

class Function1Context<A> : Function1MonadInstance<A>

class Function1ContextPartiallyApplied<L> {
  infix fun <A> extensions(f: Function1Context<L>.() -> A): A =
    f(Function1Context())
}

@Deprecated(ExtensionsDSLDeprecated)
fun <L> ForFunction1(): Function1ContextPartiallyApplied<L> =
  Function1ContextPartiallyApplied()

