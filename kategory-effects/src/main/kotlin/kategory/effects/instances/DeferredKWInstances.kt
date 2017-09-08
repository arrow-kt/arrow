package kategory

interface DeferredKWHKMonadErrorInstance : MonadError<DeferredKWHK, Throwable>, DeferredKWHKMonadInstance {
    override fun <A> raiseError(e: Throwable): DeferredKW<A> =
            DeferredKW.raiseError(e)

    override fun <A> handleErrorWith(fa: DeferredKWKind<A>, f: (Throwable) -> DeferredKWKind<A>): DeferredKW<A> =
            fa.handleErrorWith { f(it).ev() }
}