package arrow.instances

import arrow.Kind
import arrow.core.Either
import arrow.data.ForFunction0
import arrow.data.Function0
import arrow.data.Function0Of
import arrow.data.fix
import arrow.instance
import arrow.typeclasses.*
import arrow.typeclasses.continuations.BindingContinuation
import arrow.typeclasses.continuations.MonadNonBlockingContinuation
import kotlin.coroutines.experimental.CoroutineContext

@instance(Function0::class)
interface Function0FunctorInstance : Functor<ForFunction0> {
    override fun <A, B> map(fa: Function0Of<A>, f: kotlin.Function1<A, B>): Function0<B> =
            fa.fix().map(f)
}

@instance(Function0::class)
interface Function0ApplicativeInstance : Applicative<ForFunction0> {
    override fun <A, B> ap(fa: Function0Of<A>, ff: Function0Of<kotlin.Function1<A, B>>): Function0<B> =
            fa.fix().ap(ff)

    override fun <A, B> map(fa: Function0Of<A>, f: kotlin.Function1<A, B>): Function0<B> =
            fa.fix().map(f)

    override fun <A> pure(a: A): Function0<A> =
            Function0.pure(a)
}

@instance(Function0::class)
interface Function0MonadInstance : Monad<ForFunction0> {
    override fun <A, B> ap(fa: Function0Of<A>, ff: Function0Of<kotlin.Function1<A, B>>): Function0<B> =
            fa.fix().ap(ff)

    override fun <A, B> flatMap(fa: Function0Of<A>, f: kotlin.Function1<A, Function0Of<B>>): Function0<B> =
            fa.fix().flatMap(f)

    override fun <A, B> flatMapIn(fa: Kind<ForFunction0, A>, context: CoroutineContext, f: (A) -> Kind<ForFunction0, B>): Function0<B> =
            fa.fix().flatMapIn(context, f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, Function0Of<Either<A, B>>>): Function0<B> =
            Function0.tailRecM(a, f)

    override fun <A, B> map(fa: Function0Of<A>, f: kotlin.Function1<A, B>): Function0<B> =
            fa.fix().map(f)

    override fun <A> pure(a: A): Function0<A> =
            Function0.pure(a)

    override fun <B> binding(context: CoroutineContext, c: suspend BindingContinuation<ForFunction0, *>.() -> B): Kind<ForFunction0, B> =
            MonadNonBlockingContinuation.binding(this, context, c)
}

@instance(Function0::class)
interface Function0ComonadInstance : Comonad<ForFunction0> {
    override fun <A, B> coflatMap(fa: Function0Of<A>, f: kotlin.Function1<Function0Of<A>, B>): Function0<B> =
            fa.fix().coflatMap(f)

    override fun <A> extract(fa: Function0Of<A>): A =
            fa.fix().extract()

    override fun <A, B> map(fa: Function0Of<A>, f: kotlin.Function1<A, B>): Function0<B> =
            fa.fix().map(f)
}

@instance(Function0::class)
interface Function0BimonadInstance : Bimonad<ForFunction0> {
    override fun <A, B> ap(fa: Function0Of<A>, ff: Function0Of<kotlin.Function1<A, B>>): Function0<B> =
            fa.fix().ap(ff)

    override fun <A, B> flatMap(fa: Function0Of<A>, f: kotlin.Function1<A, Function0Of<B>>): Function0<B> =
            fa.fix().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, Function0Of<Either<A, B>>>): Function0<B> =
            Function0.tailRecM(a, f)

    override fun <A, B> map(fa: Function0Of<A>, f: kotlin.Function1<A, B>): Function0<B> =
            fa.fix().map(f)

    override fun <A> pure(a: A): Function0<A> =
            Function0.pure(a)

    override fun <A, B> coflatMap(fa: Function0Of<A>, f: kotlin.Function1<Function0Of<A>, B>): Function0<B> =
            fa.fix().coflatMap(f)

    override fun <A> extract(fa: Function0Of<A>): A =
            fa.fix().extract()
}
