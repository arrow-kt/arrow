package arrow.effects

import arrow.Kind
import arrow.core.Either
import arrow.instance
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.MonadError

@instance(DeferredKW::class)
interface DeferredKWApplicativeErrorInstance :
        DeferredKWApplicativeInstance,
        ApplicativeError<ForDeferredKW, Throwable> {
    override fun <A> raiseError(e: Throwable): DeferredKW<A> =
            DeferredKW.raiseError(e)

    override fun <A> handleErrorWith(fa: DeferredKWOf<A>, f: (Throwable) -> DeferredKWOf<A>): DeferredKW<A> =
            fa.handleErrorWith { f(it).reify() }
}

@instance(DeferredKW::class)
interface DeferredKWMonadErrorInstance :
        DeferredKWApplicativeErrorInstance,
        DeferredKWMonadInstance,
        MonadError<ForDeferredKW, Throwable> {
    override fun <A, B> ap(fa: DeferredKWOf<A>, ff: DeferredKWOf<(A) -> B>): DeferredKW<B> =
            super<DeferredKWMonadInstance>.ap(fa, ff)

    override fun <A, B> map(fa: DeferredKWOf<A>, f: (A) -> B): DeferredKW<B> =
            super<DeferredKWMonadInstance>.map(fa, f)

    override fun <A> pure(a: A): DeferredKW<A> =
            super<DeferredKWMonadInstance>.pure(a)
}

@instance(DeferredKW::class)
interface DeferredKWMonadSuspendInstance : DeferredKWMonadErrorInstance, MonadSuspend<ForDeferredKW> {
    override fun <A> suspend(fa: () -> DeferredKWOf<A>): DeferredKW<A> =
            DeferredKW.suspend(fa = fa)
}

@instance(DeferredKW::class)
interface DeferredKWAsyncInstance : DeferredKWMonadSuspendInstance, Async<ForDeferredKW> {
    override fun <A> async(fa: Proc<A>): DeferredKW<A> =
            DeferredKW.async(fa = fa)

    override fun <A> invoke(fa: () -> A): DeferredKW<A> =
            DeferredKW.invoke(f = fa)
}

@instance(DeferredKW::class)
interface DeferredKWEffectInstance : DeferredKWAsyncInstance, Effect<ForDeferredKW> {
    override fun <A> runAsync(fa: Kind<ForDeferredKW, A>, cb: (Either<Throwable, A>) -> DeferredKWOf<Unit>): DeferredKW<Unit> =
            fa.reify().runAsync(cb)
}
