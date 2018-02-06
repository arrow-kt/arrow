package arrow.effects

import arrow.core.Either
import arrow.instance
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.MonadError
import io.reactivex.BackpressureStrategy

@instance(FlowableKW::class)
interface FlowableKWApplicativeErrorInstance :
        FlowableKWApplicativeInstance,
        ApplicativeError<ForFlowableKW, Throwable> {
    override fun <A> raiseError(e: Throwable): FlowableKW<A> =
            FlowableKW.raiseError(e)

    override fun <A> handleErrorWith(fa: FlowableKWKind<A>, f: (Throwable) -> FlowableKWKind<A>): FlowableKW<A> =
            fa.handleErrorWith { f(it).ev() }
}

@instance(FlowableKW::class)
interface FlowableKWMonadErrorInstance :
        FlowableKWApplicativeErrorInstance,
        FlowableKWMonadInstance,
        MonadError<ForFlowableKW, Throwable> {
    override fun <A, B> ap(fa: FlowableKWKind<A>, ff: FlowableKWKind<(A) -> B>): FlowableKW<B> =
            super<FlowableKWMonadInstance>.ap(fa, ff)

    override fun <A, B> map(fa: FlowableKWKind<A>, f: (A) -> B): FlowableKW<B> =
            super<FlowableKWMonadInstance>.map(fa, f)

    override fun <A> pure(a: A): FlowableKW<A> =
            super<FlowableKWMonadInstance>.pure(a)
}

@instance(FlowableKW::class)
interface FlowableKWMonadSuspendInstance :
        FlowableKWMonadErrorInstance,
        MonadSuspend<ForFlowableKW> {
    override fun <A> suspend(fa: () -> FlowableKWKind<A>): FlowableKW<A> =
            FlowableKW.suspend(fa)

    fun BS(): BackpressureStrategy = BackpressureStrategy.BUFFER
}

@instance(FlowableKW::class)
interface FlowableKWAsyncInstance :
        FlowableKWMonadSuspendInstance,
        Async<ForFlowableKW> {
    override fun <A> async(fa: Proc<A>): FlowableKW<A> =
            FlowableKW.async(fa, BS())
}

@instance(FlowableKW::class)
interface FlowableKWEffectInstance :
        FlowableKWAsyncInstance,
        Effect<ForFlowableKW> {
    override fun <A> runAsync(fa: FlowableKWKind<A>, cb: (Either<Throwable, A>) -> FlowableKWKind<Unit>): FlowableKW<Unit> =
            fa.ev().runAsync(cb)
}