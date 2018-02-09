package arrow.effects

import arrow.core.Either
import arrow.instance
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.MonadError
import io.reactivex.BackpressureStrategy

@instance(FlowableK::class)
interface FlowableKApplicativeErrorInstance :
        FlowableKApplicativeInstance,
        ApplicativeError<ForFlowableK, Throwable> {
    override fun <A> raiseError(e: Throwable): FlowableK<A> =
            FlowableK.raiseError(e)

    override fun <A> handleErrorWith(fa: FlowableKOf<A>, f: (Throwable) -> FlowableKOf<A>): FlowableK<A> =
            fa.handleErrorWith { f(it).extract() }
}

@instance(FlowableK::class)
interface FlowableKMonadErrorInstance :
        FlowableKApplicativeErrorInstance,
        FlowableKMonadInstance,
        MonadError<ForFlowableK, Throwable> {
    override fun <A, B> ap(fa: FlowableKOf<A>, ff: FlowableKOf<(A) -> B>): FlowableK<B> =
            super<FlowableKMonadInstance>.ap(fa, ff)

    override fun <A, B> map(fa: FlowableKOf<A>, f: (A) -> B): FlowableK<B> =
            super<FlowableKMonadInstance>.map(fa, f)

    override fun <A> pure(a: A): FlowableK<A> =
            super<FlowableKMonadInstance>.pure(a)
}

@instance(FlowableK::class)
interface FlowableKMonadSuspendInstance :
        FlowableKMonadErrorInstance,
        MonadSuspend<ForFlowableK> {
    override fun <A> suspend(fa: () -> FlowableKOf<A>): FlowableK<A> =
            FlowableK.suspend(fa)

    fun BS(): BackpressureStrategy = BackpressureStrategy.BUFFER
}

@instance(FlowableK::class)
interface FlowableKAsyncInstance :
        FlowableKMonadSuspendInstance,
        Async<ForFlowableK> {
    override fun <A> async(fa: Proc<A>): FlowableK<A> =
            FlowableK.async(fa, BS())
}

@instance(FlowableK::class)
interface FlowableKEffectInstance :
        FlowableKAsyncInstance,
        Effect<ForFlowableK> {
    override fun <A> runAsync(fa: FlowableKOf<A>, cb: (Either<Throwable, A>) -> FlowableKOf<Unit>): FlowableK<Unit> =
            fa.extract().runAsync(cb)
}