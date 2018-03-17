package arrow.instances

import arrow.Kind
import arrow.core.Either
import arrow.data.Function1
import arrow.data.Function1Of
import arrow.data.Function1PartialOf
import arrow.data.fix
import arrow.instance
import arrow.typeclasses.Applicative
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.continuations.BindingContinuation
import arrow.typeclasses.continuations.MonadNonBlockingContinuation
import kotlin.coroutines.experimental.CoroutineContext

@instance(Function1::class)
interface Function1FunctorInstance<I> : Functor<Function1PartialOf<I>> {
    override fun <A, B> map(fa: Function1Of<I, A>, f: (A) -> B): Function1<I, B> =
            fa.fix().map(f)
}

@instance(Function1::class)
interface Function1ApplicativeInstance<I> : Function1FunctorInstance<I>, Applicative<Function1PartialOf<I>> {

    override fun <A> pure(a: A): Function1<I, A> =
            Function1.pure(a)

    override fun <A, B> map(fa: Function1Of<I, A>, f: (A) -> B): Function1<I, B> =
            fa.fix().map(f)

    override fun <A, B> ap(fa: Function1Of<I, A>, ff: Function1Of<I, (A) -> B>): Function1<I, B> =
            fa.fix().ap(ff)
}

@instance(Function1::class)
interface Function1MonadInstance<I> : Function1ApplicativeInstance<I>, Monad<Function1PartialOf<I>> {

    override fun <A, B> map(fa: Function1Of<I, A>, f: (A) -> B): Function1<I, B> =
            fa.fix().map(f)

    override fun <A, B> ap(fa: Function1Of<I, A>, ff: Function1Of<I, (A) -> B>): Function1<I, B> =
            fa.fix().ap(ff)

    override fun <A, B> flatMap(fa: Function1Of<I, A>, f: (A) -> Function1Of<I, B>): Function1<I, B> =
            fa.fix().flatMap(f)

    override fun <A, B> flatMapIn(fa: Kind<Function1PartialOf<I>, A>, context: CoroutineContext, f: (A) -> Kind<Function1PartialOf<I>, B>): Function1<I, B> =
            fa.fix().flatMapIn(context, f)

    override fun <A, B> tailRecM(a: A, f: (A) -> Function1Of<I, Either<A, B>>): Function1<I, B> =
            Function1.tailRecM(a, f)

    override fun <B> binding(context: CoroutineContext, c: suspend BindingContinuation<Function1PartialOf<I>, *>.() -> B): Kind<Function1PartialOf<I>, B> =
            MonadNonBlockingContinuation.binding(this, context, c)
}

