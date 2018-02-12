package arrow.effecs

import arrow.core.Either
import arrow.effects.Async
import arrow.effects.Effect
import arrow.effects.MonadSuspend
import arrow.effects.Proc
import arrow.instance
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.MonadError

@instance(FluxK::class)
interface FluxKApplicativeErrorInstance :
        FluxKApplicativeInstance,
        ApplicativeError<ForFluxK, Throwable> {
    override fun <A> raiseError(e: Throwable): FluxK<A> =
            FluxK.raiseError(e)

    override fun <A> handleErrorWith(fa: FluxKOf<A>, f: (Throwable) -> FluxKOf<A>): FluxK<A> =
            fa.handleErrorWith { f(it).fix() }
}

@instance(FluxK::class)
interface FluxKMonadErrorInstance :
        FluxKApplicativeErrorInstance,
        FluxKMonadInstance,
        MonadError<ForFluxK, Throwable> {
    override fun <A, B> ap(fa: FluxKOf<A>, ff: FluxKOf<(A) -> B>): FluxK<B> =
            super<FluxKMonadInstance>.ap(fa, ff)

    override fun <A, B> map(fa: FluxKOf<A>, f: (A) -> B): FluxK<B> =
            super<FluxKMonadInstance>.map(fa, f)

    override fun <A> pure(a: A): FluxK<A> =
            super<FluxKMonadInstance>.pure(a)
}

@instance(FluxK::class)
interface FluxKMonadSuspendInstance :
        FluxKMonadErrorInstance,
        MonadSuspend<ForFluxK> {
    override fun <A> suspend(fa: () -> FluxKOf<A>): FluxK<A> =
            FluxK.suspend(fa)
}

@instance(FluxK::class)
interface FluxKAsyncInstance :
        FluxKMonadSuspendInstance,
        Async<ForFluxK> {
    override fun <A> async(fa: Proc<A>): FluxK<A> =
            FluxK.runAsync(fa)
}

@instance(FluxK::class)
interface FluxKEffectInstance :
        FluxKAsyncInstance,
        Effect<ForFluxK> {
    override fun <A> runAsync(fa: FluxKOf<A>, cb: (Either<Throwable, A>) -> FluxKOf<Unit>): FluxK<Unit> =
            fa.fix().runAsync(cb)
}