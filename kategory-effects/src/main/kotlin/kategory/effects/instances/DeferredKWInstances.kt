package kategory

interface DeferredKWHKMonadErrorInstance : MonadError<DeferredKWHK, Throwable>, DeferredKWHKMonadInstance {
    override fun <A> raiseError(e: Throwable): HK<DeferredKWHK, A> =
            DeferredKW.raiseError(e)

    override fun <A> handleErrorWith(fa: HK<DeferredKWHK, A>, f: (Throwable) -> HK<DeferredKWHK, A>): HK<DeferredKWHK, A> =
            fa.handleErrorWith { f(it).ev() }
}