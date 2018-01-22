package arrow.effects

import arrow.core.Either
import arrow.instance
import arrow.typeclasses.MonadError
import io.reactivex.BackpressureStrategy

@instance(FlowableKW::class)
interface FlowableKWMonadErrorInstance :
        FlowableKWMonadInstance,
        MonadError<FlowableKWHK, Throwable> {
    override fun <A> raiseError(e: Throwable): FlowableKW<A> =
            FlowableKW.raiseError(e)

    override fun <A> handleErrorWith(fa: FlowableKWKind<A>, f: (Throwable) -> FlowableKWKind<A>): FlowableKW<A> =
            fa.handleErrorWith { f(it).ev() }
}

@instance(FlowableKW::class)
interface FlowableKWMonadSuspendInstance :
        FlowableKWMonadErrorInstance,
        MonadSuspend<FlowableKWHK> {
    override fun <A> suspend(fa: () -> FlowableKWKind<A>): FlowableKW<A> =
            FlowableKW.suspend(fa)

    fun BS(): BackpressureStrategy = BackpressureStrategy.BUFFER
}

@instance(FlowableKW::class)
interface FlowableKWAsyncInstance :
        FlowableKWMonadSuspendInstance,
        Async<FlowableKWHK> {
    override fun <A> async(fa: Proc<A>): FlowableKW<A> =
            FlowableKW.async(fa, BS())
}

@instance(FlowableKW::class)
interface FlowableKWEffectInstance :
        FlowableKWAsyncInstance,
        Effect<FlowableKWHK> {
    override fun <A> runAsync(fa: FlowableKWKind<A>, cb: (Either<Throwable, A>) -> FlowableKWKind<Unit>): FlowableKW<Unit> =
            fa.ev().runAsync(cb)
}