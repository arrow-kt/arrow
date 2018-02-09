package arrow.instances

import arrow.typeclasses.Applicative
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.core.*
import arrow.data.*
import arrow.instance

@instance(Function1::class)
interface Function1FunctorInstance<I> : Functor<Function1PartialOf<I>> {
    override fun <A, B> map(fa: Function1Of<I, A>, f: (A) -> B): Function1<I, B> =
            fa.extract().map(f)
}

@instance(Function1::class)
interface Function1ApplicativeInstance<I> : Function1FunctorInstance<I>, Applicative<Function1PartialOf<I>> {

    override fun <A> pure(a: A): Function1<I, A> =
            Function1.pure(a)

    override fun <A, B> map(fa: Function1Of<I, A>, f: (A) -> B): Function1<I, B> =
            fa.extract().map(f)

    override fun <A, B> ap(fa: Function1Of<I, A>, ff: Function1Of<I, (A) -> B>): Function1<I, B> =
            fa.extract().ap(ff)
}

@instance(Function1::class)
interface Function1MonadInstance<I> : Function1ApplicativeInstance<I>, Monad<Function1PartialOf<I>> {

    override fun <A, B> map(fa: Function1Of<I, A>, f: (A) -> B): Function1<I, B> =
            fa.extract().map(f)

    override fun <A, B> ap(fa: Function1Of<I, A>, ff: Function1Of<I, (A) -> B>): Function1<I, B> =
            fa.extract().ap(ff)

    override fun <A, B> flatMap(fa: Function1Of<I, A>, f: (A) -> Function1Of<I, B>): Function1<I, B> =
            fa.extract().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: (A) -> Function1Of<I, Either<A, B>>): Function1<I, B> =
            Function1.tailRecM(a, f)
}

