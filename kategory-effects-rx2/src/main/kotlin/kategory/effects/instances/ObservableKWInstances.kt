package kategory.effects

import kategory.MonadError
import kategory.instance

@instance(ObservableKW::class)
interface ObservableKWMonadErrorInstance :
        ObservableKWMonadInstance,
        MonadError<ObservableKWHK, Throwable> {
    override fun <A> raiseError(e: Throwable): ObservableKW<A> =
            ObservableKW.raiseError(e)

    override fun <A> handleErrorWith(fa: ObservableKWKind<A>, f: (Throwable) -> ObservableKWKind<A>): ObservableKW<A> =
            fa.handleErrorWith { f(it).ev() }
}