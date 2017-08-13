package kategory

interface IOInstances :
        Functor<IOHK>,
        Applicative<IOHK>,
        Monad<IOHK>,
        MonadError<IOHK, Throwable>,
        AsyncContext<IOHK> {

    override fun <A, B> map(fa: HK<IOHK, A>, f: (A) -> B): IO<B> = fa.ev().map(f)

    override fun <A> pure(a: A): IO<A> = Pure(a)

    override fun <A, B> flatMap(fa: IOKind<A>, f: (A) -> IOKind<B>): IO<B> = fa.ev().flatMap { f(it).ev() }

    override fun <A> raiseError(e: Throwable): IO<A> =
            RaiseError(e)

    override fun <A> handleErrorWith(fa: HK<IOHK, A>, f: (Throwable) -> HK<IOHK, A>): HK<IOHK, A> =
            fa.ev().attempt().flatMap { it.fold(f, { IO.pure(it) }).ev() }

    override fun <A> runAsync(fa: Proc<A>): IO<A> =
            IO.async(fa)

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

    override fun combine(ioa: HK<IOHK, A>, iob: HK<IOHK, A>): IO<A> = ioa.ev().flatMap { a1: A -> iob.ev().map { a2: A -> SM().combine(a1, a2) } }

    override fun empty(): IO<A> = IO.pure(SM().empty())

}

interface IOSemigroup<A> : Semigroup<HK<IOHK, A>> {

    fun SG(): Semigroup<A>

    override fun combine(ioa: HK<IOHK, A>, iob: HK<IOHK, A>): IO<A> = ioa.ev().flatMap { a1: A -> iob.ev().map { a2: A -> SG().combine(a1, a2) } }
}