package kategory

interface Function1Instances<P> :
        Functor<Function1.F>,
        Applicative<Function1.F>,
        Monad<Function1.F>,
        MonadReader<Function1.F, P> {

    override fun ask(): Function1<P, P> =
            { a: P -> a }.k()

    override fun <A> local(f: (P) -> P, fa: HK<Function1.F, A>): Function1<P, A> =
            f.andThen { fa.invoke(it) }.k()

    override fun <A> pure(a: A): Function1<P, A> =
            { _: P -> a }.k()

    override fun <A, B> map(fa: HK<Function1.F, A>, f: (A) -> B): Function1<P, B> =
            f.compose { a: P -> fa.invoke(a) }.k()

    override fun <A, B> flatMap(fa: HK<Function1.F, A>, f: (A) -> HK<Function1.F, B>): Function1<P, B> =
            Function1 { p -> f(fa.invoke(p)).invoke(p) }

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<Function1.F, Either<A, B>>): Function1<P, B> =
            Function1 { p ->
                tailrec fun loop(thisA: A): B =
                        f(thisA).invoke(p).fold({ loop(it) }, { it })

                loop(a)
            }
}