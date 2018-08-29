package arrow.instances

import arrow.Kind
import arrow.Kind2
import arrow.core.*
import arrow.instance
import arrow.typeclasses.Applicative
import arrow.typeclasses.Category
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.Profunctor

@instance(Function1::class)
interface Function1FunctorInstance<I> : Functor<Function1PartialOf<I>> {
  override fun <A, B> Kind<Function1PartialOf<I>, A>.map(f: (A) -> B): Function1<I, B> =
    fix().map(f)
}

@instance(Function1::class)
interface Function1ProfunctorInstance : Profunctor<ForFunction1> {
  override fun <A, B, C, D> Kind<Function1PartialOf<A>, B>.dimap(fl: (C) -> A, fr: (B) -> D): Function1<C, D> =
    (fr compose fix().f compose fl).k()
}

@instance(Function1::class)
interface Function1ApplicativeInstance<I> : Function1FunctorInstance<I>, Applicative<Function1PartialOf<I>> {

  override fun <A> just(a: A): Function1<I, A> =
    Function1.just(a)

  override fun <A, B> Kind<Function1PartialOf<I>, A>.map(f: (A) -> B): Function1<I, B> =
    fix().map(f)

  override fun <A, B> Kind<Function1PartialOf<I>, A>.ap(ff: Kind<Function1PartialOf<I>, (A) -> B>): Function1<I, B> =
    fix().ap(ff)
}

@instance(Function1::class)
interface Function1MonadInstance<I> : Function1ApplicativeInstance<I>, Monad<Function1PartialOf<I>> {

  override fun <A, B> Kind<Function1PartialOf<I>, A>.map(f: (A) -> B): Function1<I, B> =
    fix().map(f)

  override fun <A, B> Kind<Function1PartialOf<I>, A>.ap(ff: Kind<Function1PartialOf<I>, (A) -> B>): Function1<I, B> =
    fix().ap(ff)

  override fun <A, B> Kind<Function1PartialOf<I>, A>.flatMap(f: (A) -> Kind<Function1PartialOf<I>, B>): Function1<I, B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> Function1Of<I, Either<A, B>>): Function1<I, B> =
    Function1.tailRecM(a, f)
}

class Function1Context<A> : Function1MonadInstance<A>

class Function1ContextPartiallyApplied<L> {
  infix fun <A> extensions(f: Function1Context<L>.() -> A): A =
    f(Function1Context())
}

fun <L> ForFunction1(): Function1ContextPartiallyApplied<L> =
  Function1ContextPartiallyApplied()

@instance(Function1::class)
interface Function1CategoryInstance : Category<ForFunction1> {
  override fun <A> id(): Kind2<ForFunction1, A, A> = Function1.id()

  override fun <A, B, C> Kind2<ForFunction1, B, C>.compose(arr: Kind2<ForFunction1, A, B>): Kind2<ForFunction1, A, C> = fix().compose(arr.fix())
}
