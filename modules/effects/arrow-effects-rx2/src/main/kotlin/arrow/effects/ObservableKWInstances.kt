package arrow.effects

import arrow.core.Either
import arrow.instance
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.MonadError

@instance(ObservableKW::class)
interface ObservableKWApplicativeErrorInstance :
        ObservableKWApplicativeInstance,
        ApplicativeError<ForObservableKW, Throwable> {
    override fun <A> raiseError(e: Throwable): ObservableKW<A> =
            ObservableKW.raiseError(e)

    override fun <A> handleErrorWith(fa: ObservableKWKind<A>, f: (Throwable) -> ObservableKWKind<A>): ObservableKW<A> =
            fa.handleErrorWith { f(it).reify() }
}

@instance(ObservableKW::class)
interface ObservableKWMonadErrorInstance :
        ObservableKWApplicativeErrorInstance,
        ObservableKWMonadInstance,
        MonadError<ForObservableKW, Throwable> {
    override fun <A, B> ap(fa: ObservableKWKind<A>, ff: ObservableKWKind<(A) -> B>): ObservableKW<B> =
            super<ObservableKWMonadInstance>.ap(fa, ff)

    override fun <A, B> map(fa: ObservableKWKind<A>, f: (A) -> B): ObservableKW<B> =
            super<ObservableKWMonadInstance>.map(fa, f)

    override fun <A> pure(a: A): ObservableKW<A> =
            super<ObservableKWMonadInstance>.pure(a)
}

@instance(ObservableKW::class)
interface ObservableKWMonadSuspendInstance :
        ObservableKWMonadErrorInstance,
        MonadSuspend<ForObservableKW> {
    override fun <A> suspend(fa: () -> ObservableKWKind<A>): ObservableKW<A> =
            ObservableKW.suspend(fa)
}

@instance(ObservableKW::class)
interface ObservableKWAsyncInstance :
        ObservableKWMonadSuspendInstance,
        Async<ForObservableKW> {
    override fun <A> async(fa: Proc<A>): ObservableKW<A> =
            ObservableKW.runAsync(fa)
}

@instance(ObservableKW::class)
interface ObservableKWEffectInstance :
        ObservableKWAsyncInstance,
        Effect<ForObservableKW> {
    override fun <A> runAsync(fa: ObservableKWKind<A>, cb: (Either<Throwable, A>) -> ObservableKWKind<Unit>): ObservableKW<Unit> =
            fa.reify().runAsync(cb)
}
