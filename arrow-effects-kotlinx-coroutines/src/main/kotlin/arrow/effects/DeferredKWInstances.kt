package arrow.effects

import arrow.HK
import arrow.core.Either
import arrow.instance
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.MonadError

@instance(DeferredKW::class)
interface DeferredKWApplicativeErrorInstance :
        DeferredKWApplicativeInstance,
        ApplicativeError<DeferredKWHK, Throwable> {
    override fun <A> raiseError(e: Throwable): DeferredKW<A> =
            DeferredKW.raiseError(e)

    override fun <A> handleErrorWith(fa: DeferredKWKind<A>, f: (Throwable) -> DeferredKWKind<A>): DeferredKW<A> =
            fa.handleErrorWith { f(it).ev() }
}

@instance(DeferredKW::class)
interface DeferredKWMonadErrorInstance :
        DeferredKWApplicativeErrorInstance,
        DeferredKWMonadInstance,
        MonadError<DeferredKWHK, Throwable> {
    override fun <A, B> ap(fa: DeferredKWKind<A>, ff: DeferredKWKind<(A) -> B>): DeferredKW<B> =
            super<DeferredKWMonadInstance>.ap(fa, ff)

    override fun <A, B> map(fa: DeferredKWKind<A>, f: (A) -> B): DeferredKW<B> =
            super<DeferredKWMonadInstance>.map(fa, f)

    override fun <A> pure(a: A): DeferredKW<A> =
            super<DeferredKWMonadInstance>.pure(a)
}

@instance(DeferredKW::class)
interface DeferredKWMonadSuspendInstance : DeferredKWMonadErrorInstance, MonadSuspend<DeferredKWHK> {
    override fun <A> suspend(fa: () -> DeferredKWKind<A>): DeferredKW<A> =
            DeferredKW.suspend(fa = fa)
}

@instance(DeferredKW::class)
interface DeferredKWAsyncInstance : DeferredKWMonadSuspendInstance, Async<DeferredKWHK> {
    override fun <A> async(fa: Proc<A>): DeferredKW<A> =
            DeferredKW.async(fa = fa)

    override fun <A> invoke(fa: () -> A): DeferredKW<A> =
            DeferredKW.invoke(f = fa)
}

@instance(DeferredKW::class)
interface DeferredKWEffectInstance : DeferredKWAsyncInstance, Effect<DeferredKWHK> {
    override fun <A> runAsync(fa: HK<DeferredKWHK, A>, cb: (Either<Throwable, A>) -> DeferredKWKind<Unit>): DeferredKW<Unit> =
            fa.ev().runAsync(cb)
}
