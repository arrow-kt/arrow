package arrow.effects

import io.reactivex.BackpressureStrategy
import arrow.typeclasses.MonadError
import arrow.instance

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
interface FlowableKWAsyncContextInstance : AsyncContext<FlowableKWHK> {
    override fun <A> runAsync(fa: Proc<A>): FlowableKW<A> = FlowableKW.runAsync(fa, BS())
    fun BS(): BackpressureStrategy = BackpressureStrategy.BUFFER
}
