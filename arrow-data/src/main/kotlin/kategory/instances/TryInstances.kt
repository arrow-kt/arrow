package arrow

@instance(Try::class)
interface TryMonadErrorInstance : TryMonadInstance, MonadError<TryHK, Throwable> {

    override fun <A> raiseError(e: Throwable): Try<A> = Failure(e)

    override fun <A> handleErrorWith(fa: TryKind<A>, f: (Throwable) -> TryKind<A>): Try<A> = fa.ev().recoverWith { f(it).ev() }

}

@instance(Try::class)
interface TryEqInstance<A> : Eq<Try<A>> {

    fun EQA(): Eq<A>

    override fun eqv(a: Try<A>, b: Try<A>): Boolean = when (a) {
        is Success -> when (b) {
            is Failure -> false
            is Success -> EQA().eqv(a.value, b.value)
        }
        is Failure -> when (b) {
        //currently not supported by implicit resolution to have implicit that does not occur in type params
            is Failure -> a.exception == b.exception
            is Success -> false
        }
    }

}