package kategory

interface IOInstances :
        Functor<IOHK>,
        Applicative<IOHK>,
        Monad<IOHK> {

    override fun <A, B> map(fa: HK<IOHK, A>, f: (A) -> B): IO<B> =
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

interface IOMonoid<A> : Monoid<HK<IOHK, A>>, Semigroup<HK<IOHK, A>> {

    fun SM(): Monoid<A>

    override fun combine(ioa: HK<IOHK, A>, iob: HK<IOHK, A>): IO<A> =
            ioa.ev().flatMap { a1 -> iob.ev().map { a2 -> SM().combine(a1, a2) } }

    override fun empty(): IO<A> =
            IO.pure(SM().empty())

}

interface IOSemigroup<A> : Semigroup<HK<IOHK, A>> {

    fun SG(): Semigroup<A>

    override fun combine(ioa: HK<IOHK, A>, iob: HK<IOHK, A>): IO<A> =
            ioa.ev().flatMap { a1 -> iob.ev().map { a2 -> SG().combine(a1, a2) } }
}