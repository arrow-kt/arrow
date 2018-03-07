package arrow.effecs

import arrow.Kind
import arrow.core.Either
import arrow.effects.Async
import arrow.effects.Effect
import arrow.effects.MonadSuspend
import arrow.effects.Proc
import arrow.instance
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.MonadError

@instance(MonoK::class)
interface MonoKApplicativeErrorInstance :
        MonoKApplicativeInstance,
        ApplicativeError<ForMonoK, Throwable> {
    override fun <A> raiseError(e: Throwable): MonoK<A> =
            MonoK.raiseError(e)

    override fun <A> handleErrorWith(fa: MonoKOf<A>, f: (Throwable) -> MonoKOf<A>): MonoK<A> =
            fa.handleErrorWith { f(it).fix() }
}

@instance(MonoK::class)
interface MonoKMonadErrorInstance :
        MonoKApplicativeErrorInstance,
        MonoKMonadInstance,
        MonadError<ForMonoK, Throwable> {
    override fun <A, B> ap(fa: MonoKOf<A>, ff: MonoKOf<(A) -> B>): MonoK<B> =
            super<MonoKMonadInstance>.ap(fa, ff)

    override fun <A, B> map(fa: MonoKOf<A>, f: (A) -> B): MonoK<B> =
            super<MonoKMonadInstance>.map(fa, f)

    override fun <A> pure(a: A): MonoK<A> =
            super<MonoKMonadInstance>.pure(a)
}

@instance(MonoK::class)
interface MonoKMonadSuspendInstance :
        MonoKMonadErrorInstance,
        MonadSuspend<ForMonoK> {
    override fun <A> suspend(fa: () -> Kind<ForMonoK, A>): Kind<ForMonoK, A> =
            MonoK.suspend(fa)
}

@instance(MonoK::class)
interface MonoKAsyncInstance :
        MonoKMonadSuspendInstance,
        Async<ForMonoK> {
    override fun <A> async(fa: Proc<A>): Kind<ForMonoK, A> =
            MonoK.runAsync(fa)
}

@instance(MonoK::class)
interface MonoKEffectInstance :
        MonoKAsyncInstance,
        Effect<ForMonoK> {
    override fun <A> runAsync(fa: MonoKOf<A>, cb: (Either<Throwable, A>) -> MonoKOf<Unit>): MonoK<Unit> =
            fa.fix().runAsync(cb)
}