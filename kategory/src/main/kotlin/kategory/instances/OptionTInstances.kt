package kategory

interface OptionTInstances<F> :
        Functor<OptionTF<F>>,
        Applicative<OptionTF<F>>,
        Monad<OptionTF<F>> {

    fun MF(): Monad<F>

    override fun <A> pure(a: A): OptionT<F, A> = OptionT(MF(), MF().pure(Option(a)))

    override fun <A, B> flatMap(fa: OptionTKind<F, A>, f: (A) -> OptionTKind<F, B>): OptionT<F, B> =
            fa.ev().flatMap { f(it).ev() }

    override fun <A, B> map(fa: OptionTKind<F, A>, f: (A) -> B): OptionT<F, B> =
            fa.ev().map(f)

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<OptionTF<F>, Either<A, B>>): OptionT<F, B> =
            OptionT(MF(), MF().tailRecM(a, {
                MF().map(f(it).ev().value, {
                    it.fold({
                        Either.Right<Option<B>>(Option.None)
                    }, {
                        it.map { Option.Some(it) }
                    })
                })
            }))

}

interface OptionTTraverse<F> :
        Foldable<OptionTF<F>>,
        Traverse<OptionTF<F>> {

    fun FF(): Traverse<F>

    fun MF(): Monad<F>

    override fun <G, A, B> traverse(fa: HK<OptionTF<F>, A>, f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, HK<OptionTF<F>, B>> =
            fa.ev().traverse(f, GA, FF(), MF())

    override fun <A, B> foldL(fa: HK<OptionTF<F>, A>, b: B, f: (B, A) -> B): B =
            fa.ev().foldL(b, f, FF())

    override fun <A, B> foldR(fa: HK<OptionTF<F>, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
            fa.ev().foldR(lb, f, FF())
}