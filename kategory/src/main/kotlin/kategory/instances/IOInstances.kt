package kategory

interface IOInstances :
        Functor<IO.F>,
        Applicative<IO.F>,
        Monad<IO.F> {

    override fun <A, B> map(fa: HK<IO.F, A>, f: (A) -> B): IO<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): IO<A> =
            IO.pure(a)

    override fun <A, B> flatMap(fa: IOKind<A>, f: (A) -> IOKind<B>): IO<B> =
            fa.ev().flatMap { f(it).ev() }

    tailrec override fun <A, B> tailRecM(a: A, f: (A) -> IOKind<Either<A, B>>): IO<B> =
            f(a).ev().flatMap {
                when (it) {
                    is Either.Left -> tailRecM(it.a, f)
                    is Either.Right -> IO.pure(it.b)
                }
            }
}

interface IOMonoid<A> : Monoid<HK<IO.F, A>>, Semigroup<HK<IO.F, A>> {

    fun SM(): Monoid<A>

    override fun combine(ioa: HK<IO.F, A>, iob: HK<IO.F, A>): IO<A> =
            ioa.ev().flatMap { a1 -> iob.ev().map { a2 -> SM().combine(a1, a2) } }

    override fun empty(): IO<A> =
            IO.pure(SM().empty())

}

interface IOSemigroup<A> : Semigroup<HK<IO.F, A>> {

    fun SG(): Semigroup<A>

    override fun combine(ioa: HK<IO.F, A>, iob: HK<IO.F, A>): IO<A> =
            ioa.ev().flatMap { a1 -> iob.ev().map { a2 -> SG().combine(a1, a2) } }
}