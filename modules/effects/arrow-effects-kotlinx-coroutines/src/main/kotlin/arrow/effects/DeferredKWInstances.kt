package arrow.effects

import arrow.Kind
import arrow.core.Either
import arrow.instance
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.MonadError

@instance(DeferredK::class)
interface DeferredKApplicativeErrorInstance :
        DeferredKApplicativeInstance,
        ApplicativeError<ForDeferredK, Throwable> {
    override fun <A> raiseError(e: Throwable): DeferredK<A> =
            DeferredK.raiseError(e)

    override fun <A> handleErrorWith(fa: DeferredKOf<A>, f: (Throwable) -> DeferredKOf<A>): DeferredK<A> =
            fa.handleErrorWith { f(it).extract() }
}

@instance(DeferredK::class)
interface DeferredKMonadErrorInstance :
        DeferredKApplicativeErrorInstance,
        DeferredKMonadInstance,
        MonadError<ForDeferredK, Throwable> {
    override fun <A, B> ap(fa: DeferredKOf<A>, ff: DeferredKOf<(A) -> B>): DeferredK<B> =
            super<DeferredKMonadInstance>.ap(fa, ff)

    override fun <A, B> map(fa: DeferredKOf<A>, f: (A) -> B): DeferredK<B> =
            super<DeferredKMonadInstance>.map(fa, f)

    override fun <A> pure(a: A): DeferredK<A> =
            super<DeferredKMonadInstance>.pure(a)
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
    override fun <A> runAsync(fa: Kind<ForDeferredK, A>, cb: (Either<Throwable, A>) -> DeferredKOf<Unit>): DeferredK<Unit> =
            fa.extract().runAsync(cb)
}
