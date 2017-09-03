package kategory

interface JobKWHKMonadErrorInstance :
        MonadError<JobKWHK, Throwable>, JobKWHKMonadInstance {
    override fun <A> raiseError(e: Throwable): JobKW<A> =
            JobKW.raiseError(e)

    override fun <A> handleErrorWith(fa: HK<JobKWHK, A>, f: (Throwable) -> HK<JobKWHK, A>): JobKW<A> =
            fa.ev().handleErrorWith { err: Throwable -> f(err).ev() }

}
