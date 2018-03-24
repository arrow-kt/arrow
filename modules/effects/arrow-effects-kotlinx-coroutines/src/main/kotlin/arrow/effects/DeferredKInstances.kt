package arrow.effects

import arrow.Kind
import arrow.core.Either
import arrow.effects.typeclasses.Async
import arrow.effects.typeclasses.Effect
import arrow.effects.typeclasses.MonadSuspend
import arrow.effects.typeclasses.Proc
import arrow.instance
import arrow.typeclasses.*

@instance(DeferredK::class)
interface DeferredKFunctorInstance : Functor<ForDeferredK> {
    override fun <A, B> map(fa: DeferredKOf<A>, f: (A) -> B): DeferredK<B> =
            fa.fix().map(f)
}

@instance(DeferredK::class)
interface DeferredKApplicativeInstance : Applicative<ForDeferredK> {
    override fun <A, B> map(fa: DeferredKOf<A>, f: (A) -> B): DeferredK<B> =
            fa.fix().map(f)

    override fun <A> pure(a: A): DeferredK<A> =
            DeferredK.pure(a)

    override fun <A, B> DeferredKOf<A>.ap(ff: DeferredKOf<(A) -> B>): DeferredK<B> =
            fix().ap(ff)
}

@instance(DeferredK::class)
interface DeferredKMonadInstance : Monad<ForDeferredK> {
    override fun <A, B> flatMap(fa: DeferredKOf<A>, f: kotlin.Function1<A, DeferredKOf<B>>): DeferredK<B> =
            fa.fix().flatMap(f)

    override fun <A, B> map(fa: DeferredKOf<A>, f: (A) -> B): DeferredK<B> =
            fa.fix().map(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, DeferredKOf<arrow.core.Either<A, B>>>): DeferredK<B> =
            DeferredK.tailRecM(a, f)

    override fun <A, B> DeferredKOf<A>.ap(ff: DeferredKOf<(A) -> B>): DeferredK<B> =
            fix().ap(ff)

    override fun <A> pure(a: A): DeferredK<A> =
            DeferredK.pure(a)
}

@instance(DeferredK::class)
interface DeferredKApplicativeErrorInstance : DeferredKApplicativeInstance, ApplicativeError<ForDeferredK, Throwable> {
    override fun <A> raiseError(e: Throwable): DeferredK<A> =
            DeferredK.raiseError(e)

    override fun <A> DeferredKOf<A>.handleErrorWith(f: (Throwable) -> DeferredKOf<A>): DeferredK<A> =
            handleErrorWith(null) { f(it).fix() }
}

@instance(DeferredK::class)
interface DeferredKMonadErrorInstance : DeferredKMonadInstance, MonadError<ForDeferredK, Throwable> {
    override fun <A> raiseError(e: Throwable): DeferredK<A> =
            DeferredK.raiseError(e)

    override fun <A> DeferredKOf<A>.handleErrorWith(f: (Throwable) -> DeferredKOf<A>): DeferredK<A> =
            handleErrorWith(null) { f(it).fix() }
}

@instance(DeferredK::class)
interface DeferredKMonadSuspendInstance : DeferredKMonadErrorInstance, MonadSuspend<ForDeferredK> {
    override fun <A> suspend(fa: () -> DeferredKOf<A>): DeferredK<A> =
            DeferredK.suspend(fa = fa)
}

@instance(DeferredK::class)
interface DeferredKAsyncInstance : DeferredKMonadSuspendInstance, Async<ForDeferredK> {
    override fun <A> async(fa: Proc<A>): DeferredK<A> =
            DeferredK.async(fa = fa)

    override fun <A> invoke(fa: () -> A): DeferredK<A> =
            DeferredK.invoke(f = fa)
}

@instance(DeferredK::class)
interface DeferredKEffectInstance : DeferredKAsyncInstance, Effect<ForDeferredK> {
    override fun <A> Kind<ForDeferredK, A>.runAsync(cb: (Either<Throwable, A>) -> DeferredKOf<Unit>): DeferredK<Unit> =
            fix().runAsync(cb)
}
