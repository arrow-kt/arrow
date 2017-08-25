package kategory

interface TryMonadError : TryHKMonadInstance, MonadError<TryHK, Throwable> {

    override fun <A> raiseError(e: Throwable): Try<A> = Try.Failure(e)

    override fun <A> handleErrorWith(fa: TryKind<A>, f: (Throwable) -> TryKind<A>): Try<A> = fa.ev().recoverWith { f(it).ev() }

}
