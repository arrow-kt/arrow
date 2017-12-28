package arrow

@instance(OptionT::class)
interface OptionTFunctorInstance<F> : Functor<OptionTKindPartial<F>> {

    fun FF(): Functor<F>

    override fun <A, B> map(fa: OptionTKind<F, A>, f: (A) -> B): OptionT<F, B> = fa.ev().map(f, FF())

}

@instance(OptionT::class)
interface OptionTFunctorFilterInstance<F> : OptionTFunctorInstance<F>, FunctorFilter<OptionTKindPartial<F>> {

    override fun <A, B> mapFilter(fa: OptionTKind<F, A>, f: (A) -> Option<B>): OptionT<F, B> =
            fa.ev().mapFilter(f, FF())

}

@instance(OptionT::class)
interface OptionTApplicativeInstance<F> : OptionTFunctorInstance<F>, Applicative<OptionTKindPartial<F>> {

    override fun FF(): Monad<F>

    override fun <A> pure(a: A): OptionT<F, A> = OptionT(FF().pure(Option(a)))

    override fun <A, B> map(fa: OptionTKind<F, A>, f: (A) -> B): OptionT<F, B> = fa.ev().map(f, FF())

    override fun <A, B> ap(fa: OptionTKind<F, A>, ff: OptionTKind<F, (A) -> B>): OptionT<F, B> =
            fa.ev().ap(ff, FF())
}

@instance(OptionT::class)
interface OptionTMonadInstance<F> : OptionTApplicativeInstance<F>, Monad<OptionTKindPartial<F>> {

    override fun <A, B> flatMap(fa: OptionTKind<F, A>, f: (A) -> OptionTKind<F, B>): OptionT<F, B> = fa.ev().flatMap({ f(it).ev() }, FF())

    override fun <A, B> ap(fa: OptionTKind<F, A>, ff: OptionTKind<F, (A) -> B>): OptionT<F, B> =
            fa.ev().ap(ff, FF())

    override fun <A, B> tailRecM(a: A, f: (A) -> OptionTKind<F, Either<A, B>>): OptionT<F, B> =
            OptionT.tailRecM(a, f, FF())

}

@instance(OptionT::class)
interface OptionTFoldableInstance<F> : Foldable<OptionTKindPartial<F>> {

    fun FFF(): Foldable<F>

    override fun <A, B> foldLeft(fa: OptionTKind<F, A>, b: B, f: (B, A) -> B): B = fa.ev().foldLeft(b, f, FFF())

    override fun <A, B> foldRight(fa: OptionTKind<F, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> = fa.ev().foldRight(lb, f, FFF())

}

@instance(OptionT::class)
interface OptionTTraverseInstance<F> : OptionTFoldableInstance<F>, Traverse<OptionTKindPartial<F>> {

    override fun FFF(): Traverse<F>

    override fun <G, A, B> traverse(fa: OptionTKind<F, A>, f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, OptionT<F, B>> =
            fa.ev().traverse(f, GA, FFF())

}

@instance(OptionT::class)
interface OptionTTraverseFilterInstance<F> :
        OptionTTraverseInstance<F>,
        TraverseFilter<OptionTKindPartial<F>> {

    override fun FFF(): TraverseFilter<F>

    override fun <G, A, B> traverseFilter(fa: OptionTKind<F, A>, f: (A) -> HK<G, Option<B>>, GA: Applicative<G>): HK<G, OptionT<F, B>> =
            fa.ev().traverseFilter(f, GA, FFF())

}

@instance(OptionT::class)
interface OptionTSemigroupKInstance<F> : SemigroupK<OptionTKindPartial<F>> {

    fun FF(): Monad<F>

    override fun <A> combineK(x: OptionTKind<F, A>, y: OptionTKind<F, A>): OptionT<F, A> = x.ev().orElse({ y.ev() }, FF())
}

@instance(OptionT::class)
interface OptionTMonoidKInstance<F> : MonoidK<OptionTKindPartial<F>>, OptionTSemigroupKInstance<F> {
    override fun <A> empty(): OptionT<F, A> = OptionT(FF().pure(None))
}
