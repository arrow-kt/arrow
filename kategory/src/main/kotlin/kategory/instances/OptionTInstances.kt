package kategory

interface OptionTInstances<F> :
        Functor<OptionTKindPartial<F>>,
        Applicative<OptionTKindPartial<F>>,
        Monad<OptionTKindPartial<F>> {

    fun MF(): Monad<F>

    override fun <A> pure(a: A): OptionT<F, A> = OptionT(MF(), MF().pure(Option(a)))

    override fun <A, B> flatMap(fa: OptionTKind<F, A>, f: (A) -> OptionTKind<F, B>): OptionT<F, B> = fa.ev().flatMap { f(it).ev() }

    override fun <A, B> map(fa: OptionTKind<F, A>, f: (A) -> B): OptionT<F, B> = fa.ev().map(f)

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<OptionTKindPartial<F>, Either<A, B>>): OptionT<F, B> =
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
        Foldable<OptionTKindPartial<F>>,
        Traverse<OptionTKindPartial<F>> {

    fun FF(): Traverse<F>

    fun MF(): Monad<F>

    override fun <G, A, B> traverse(fa: HK<OptionTKindPartial<F>, A>, f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, HK<OptionTKindPartial<F>, B>> =
            fa.ev().traverse(f, GA, FF(), MF())

    override fun <A, B> foldL(fa: HK<OptionTKindPartial<F>, A>, b: B, f: (B, A) -> B): B = fa.ev().foldL(b, f, FF())

    override fun <A, B> foldR(fa: HK<OptionTKindPartial<F>, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> = fa.ev().foldR(lb, f, FF())
}

interface OptionTSemigroupK<F> : SemigroupK<OptionTKindPartial<F>> {

    fun F(): Monad<F>

    override fun <A> combineK(x: HK<OptionTKindPartial<F>, A>, y: HK<OptionTKindPartial<F>, A>): OptionT<F, A> = x.ev().orElse { y.ev() }
}

interface OptionTMonoidK<F> : MonoidK<OptionTKindPartial<F>>, OptionTSemigroupK<F> {
    override fun <A> empty(): HK<OptionTKindPartial<F>, A> = OptionT(F(), F().pure(Option.None))
}

interface OptionTFunctor<F> : FunctorFilter<OptionTKindPartial<F>> {

    fun FF(): Functor<F>

    fun MF(): Monad<F>

    override fun <A, B> map(fa: HK<OptionTKindPartial<F>, A>, f: (A) -> B): OptionT<F, B> = fa.ev().map(f)

    override fun <A, B> mapFilter(fa: HK<OptionTKindPartial<F>, A>, f: (A) -> Option<B>): OptionT<F, B> = fa.ev().mapFilter(f, FF(), MF())
}
