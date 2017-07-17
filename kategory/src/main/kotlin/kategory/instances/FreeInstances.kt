package kategory

interface FreeInstances<S> :
        Functor<FreeF<S>>,
        Applicative<FreeF<S>>,
        Monad<FreeF<S>> {

    override fun <A> pure(a: A): Free<S, A> =
            Free.pure(a)

    override fun <A, B> map(fa: FreeKind<S, A>, f: (A) -> B): Free<S, B> =
            fa.ev().map(f)

    override fun <A, B> flatMap(fa: FreeKind<S, A>, f: (A) -> FreeKind<S, B>): Free<S, B> =
            fa.ev().flatMap { f(it).ev() }

    override fun <A, B> tailRecM(a: A, f: (A) -> FreeKind<S, Either<A, B>>): Free<S, B> {
        return f(a).ev().flatMap {
            when (it) {
                is Either.Left -> tailRecM(it.a, f)
                is Either.Right -> pure(it.b)
            }
        }
    }
}