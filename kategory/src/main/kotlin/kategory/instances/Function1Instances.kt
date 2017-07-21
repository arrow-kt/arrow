package kategory

interface Function1Instances<P> :
        Functor<Function1.F>,
        Applicative<Function1.F>,
        Monad<Function1.F>,
        MonadReader<Function1.F, P> {

    override fun ask(): HK<Function1.F, P> =
            { a: P -> a }.k()

    override fun <A> local(f: (P) -> P, fa: HK<Function1.F, A>): Function1<P, A> =
            f.andThen { fa.ev().invoke(it) }.k()

    override fun <A> pure(a: A): Function1<P, A> =
            { _: P -> a }.k()

    override fun <A, B> map(fa: HK<Function1.F, A>, f: (A) -> B): HK<Function1.F, B> =
            f.compose { b: B -> fa.ev().invoke(b) }.k()

    override fun <A, B> flatMap(fa: HK<Function1.F, A>, f: (A) -> HK<Function1.F, B>): Function1<P, B> =
            Function1 { p -> f(fa.ev().invoke(p)).ev().invoke(p) }

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<Function1.F, Either<A, B>>): Function1<P, B> =
            Function1 { p ->
                tailrec fun loop(thisA: A): B =
                        f(thisA).ev().invoke(p).fold({ loop(it) }, { it })

                loop(a)
            }
}