package arrow.effects

import arrow.HK
import arrow.core.Either
import arrow.instance
import arrow.typeclasses.MonadError

@instance(DeferredKW::class)
interface DeferredKWMonadErrorInstance :
        DeferredKWMonadInstance,
        MonadError<DeferredKWHK, Throwable> {
    override fun <A> raiseError(e: Throwable): DeferredKW<A> =
            DeferredKW.raiseError(e)

    override fun <A> handleErrorWith(fa: DeferredKWKind<A>, f: (Throwable) -> DeferredKWKind<A>): DeferredKW<A> =
            fa.handleErrorWith { f(it).ev() }
}

@instance(DeferredKW::class)
interface DeferredKWSyncInstance : DeferredKWMonadErrorInstance, Sync<DeferredKWHK> {
    override fun <A> suspend(fa: () -> DeferredKWKind<A>): DeferredKW<A> =
            DeferredKW.suspend(fa = fa)
}

@instance(DeferredKW::class)
interface DeferredKWAsyncInstance : DeferredKWSyncInstance, Async<DeferredKWHK> {
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
