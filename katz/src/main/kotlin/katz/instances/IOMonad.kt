package katz

interface IOMonad : Monad<IO.F> {
    override fun <A, B> map(fa: HK<IO.F, A>, f: (A) -> B): IO<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): IO<A> =
            IO.just(a)

    override fun <A, B> flatMap(fa: IOKind<A>, f: (A) -> IOKind<B>): IO<B> =
            fa.ev().flatMap { f(it).ev() }

    tailrec override fun <A, B> tailRecM(a: A, f: (A) -> IOKind<Either<A, B>>): IO<B> =
            f(a).ev().flatMap {
                when (it) {
                    is Either.Left -> tailRecM(it.a, f)
                    is Either.Right -> IO.just(it.b)
                }
            }
}
