package arrow.effects

import arrow.core.Either
import arrow.instance
import arrow.typeclasses.MonadError

@instance(ObservableKW::class)
interface ObservableKWMonadErrorInstance :
        ObservableKWMonadInstance,
        MonadError<ObservableKWHK, Throwable> {
    override fun <A> raiseError(e: Throwable): ObservableKW<A> =
            ObservableKW.raiseError(e)

    override fun <A> handleErrorWith(fa: ObservableKWKind<A>, f: (Throwable) -> ObservableKWKind<A>): ObservableKW<A> =
            fa.handleErrorWith { f(it).ev() }
}

@instance(ObservableKW::class)
interface ObservableKWSyncInstance :
        ObservableKWMonadErrorInstance,
        Sync<ObservableKWHK> {
    override fun <A> suspend(fa: () -> ObservableKWKind<A>): ObservableKW<A> =
            ObservableKW.suspend(fa)
}

@instance(ObservableKW::class)
interface ObservableKWAsyncInstance :
        ObservableKWSyncInstance,
        Async<ObservableKWHK> {
    override fun <A> async(fa: Proc<A>): ObservableKW<A> =
            ObservableKW.runAsync(fa)
}

@instance(ObservableKW::class)
interface ObservableKWEffectInstance :
        ObservableKWAsyncInstance,
        Effect<ObservableKWHK> {
    override fun <A> runAsync(fa: ObservableKWKind<A>, cb: (Either<Throwable, A>) -> ObservableKWKind<Unit>): ObservableKW<Unit> =
            fa.ev().runAsync(cb)
}
