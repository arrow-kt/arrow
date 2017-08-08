package kategory

interface IdInstances :
        Functor<Id.F>,
        Applicative<Id.F>,
        Monad<Id.F>,
        Foldable<Id.F>,
        Traverse<Id.F>,
        Comonad<Id.F>,
        Bimonad<Id.F> {

    override fun <A> pure(a: A): Id<A> = Id(a)

    override fun <A, B> flatMap(fa: IdKind<A>, f: (A) -> IdKind<B>): Id<B> = fa.ev().flatMap { f(it).ev() }

    override fun <A, B> map(fa: IdKind<A>, f: (A) -> B): Id<B> = fa.ev().map(f)

    tailrec override fun <A, B> tailRecM(a: A, f: (A) -> IdKind<Either<A, B>>): Id<B> {
        val x: Either<A, B> = f(a).ev().value
        return when (x) {
            is Either.Left<A, B> -> tailRecM(x.a, f)
            is Either.Right<A, B> -> Id(x.b)
        }
    }

    override fun <A, B> foldL(fa: IdKind<A>, b: B, f: (B, A) -> B): B = f(b, fa.ev().value)

    override fun <A, B> foldR(fa: IdKind<A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> = f(fa.ev().value, lb)

    override fun <G, A, B> traverse(fa: IdKind<A>, f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, Id<B>> = GA.map(f(fa.ev().value), { Id(it) })

    override fun <A, B> coflatMap(fa: IdKind<A>, f: (IdKind<A>) -> B): Id<B> = fa.ev().map({ f(fa) })

    override fun <A> extract(fa: IdKind<A>): A = fa.ev().value

}
