package arrow.effects

import arrow.MonadError
import arrow.instance

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
interface DeferredKWAsyncContextInstance : AsyncContext<DeferredKWHK> {
    override fun <A> runAsync(fa: Proc<A>): DeferredKW<A> = DeferredKW.runAsync(fa = fa)
}
