package kategory

interface OptionTFunctorInstance<F> : Functor<OptionTKindPartial<F>> {

    fun FF(): Functor<F>

    override fun <A, B> map(fa: OptionTKind<F, A>, f: (A) -> B): OptionT<F, B> = fa.ev().map(f, FF())

}

object OptionTFunctorInstanceImplicits {
    @JvmStatic
    fun <F> instance(FF: Functor<F>): OptionTFunctorInstance<F> = object : OptionTFunctorInstance<F> {
        override fun FF(): Functor<F> = FF
    }
}

interface OptionTFunctorFilterInstance<F> : OptionTFunctorInstance<F>, FunctorFilter<OptionTKindPartial<F>> {

    override fun <A, B> mapFilter(fa: OptionTKind<F, A>, f: (A) -> Option<B>): OptionT<F, B> =
            fa.ev().mapFilter(f, FF())

}

object OptionTFunctorFilterInstanceImplicits {
    @JvmStatic
    fun <F> instance(FF: Functor<F>): OptionTFunctorFilterInstance<F> = object : OptionTFunctorFilterInstance<F> {
        override fun FF(): Functor<F> = FF
    }
}

interface OptionTApplicativeInstance<F> : OptionTFunctorInstance<F>, Applicative<OptionTKindPartial<F>> {

    fun MF(): Monad<F>

    override fun <A> pure(a: A): OptionT<F, A> = OptionT(MF().pure(Option(a)))

    override fun <A, B> map(fa: OptionTKind<F, A>, f: (A) -> B): OptionT<F, B> = fa.ev().map(f, FF())

    override fun <A, B> ap(fa: OptionTKind<F, A>, ff: OptionTKind<F, (A) -> B>): OptionT<F, B> =
            fa.ev().ap(ff, MF())
}

object OptionTApplicativeInstanceImplicits {
    @JvmStatic
    fun <F> instance(MF: Monad<F>): OptionTApplicativeInstance<F> = object : OptionTApplicativeInstance<F> {
        override fun MF(): Monad<F> = MF

        override fun FF(): Functor<F> = MF
    }
}

interface OptionTMonadInstance<F> : OptionTApplicativeInstance<F>, Monad<OptionTKindPartial<F>> {

    override fun <A, B> flatMap(fa: OptionTKind<F, A>, f: (A) -> OptionTKind<F, B>): OptionT<F, B> = fa.ev().flatMap({ f(it).ev() }, MF())

    override fun <A, B> ap(fa: OptionTKind<F, A>, ff: OptionTKind<F, (A) -> B>): OptionT<F, B> =
            fa.ev().ap(ff, MF())

    override fun <A, B> tailRecM(a: A, f: (A) -> OptionTKind<F, Either<A, B>>): OptionT<F, B> =
            OptionT.tailRecM(a, f, MF())

}

object OptionTMonadInstanceImplicits {
    @JvmStatic
    fun <F> instance(MF: Monad<F>): OptionTMonadInstance<F> = object : OptionTMonadInstance<F> {
        override fun MF(): Monad<F> = MF

        override fun FF(): Functor<F> = MF
    }
}

interface OptionTFoldableInstance<F> : Foldable<OptionTKindPartial<F>> {

    fun FFF(): Foldable<F>

    override fun <A, B> foldL(fa: OptionTKind<F, A>, b: B, f: (B, A) -> B): B = fa.ev().foldL(b, f, FFF())

    override fun <A, B> foldR(fa: OptionTKind<F, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> = fa.ev().foldR(lb, f, FFF())

}

object OptionTFoldableInstanceImplicits {
    @JvmStatic
    fun <F> instance(FFF: Foldable<F>): OptionTFoldableInstance<F> = object : OptionTFoldableInstance<F> {
        override fun FFF(): Foldable<F> = FFF
    }
}

interface OptionTTraverseInstance<F> : OptionTFoldableInstance<F>, Traverse<OptionTKindPartial<F>> {

    fun TF(): Traverse<F>

    override fun <G, A, B> traverse(fa: OptionTKind<F, A>, f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, OptionT<F, B>> =
            fa.ev().traverse(f, GA, TF())

}

object OptionTTraverseInstanceImplicits {
    @JvmStatic
    fun <F> instance(TF: Traverse<F>): OptionTTraverseInstance<F> = object : OptionTTraverseInstance<F> {
        override fun FFF(): Foldable<F> = TF

        override fun TF(): Traverse<F> = TF
    }
}

interface OptionTSemigroupKInstance<F> : SemigroupK<OptionTKindPartial<F>> {

    fun MF(): Monad<F>

    override fun <A> combineK(x: OptionTKind<F, A>, y: OptionTKind<F, A>): OptionT<F, A> = x.ev().orElse({ y.ev() }, MF())
}

object OptionTSemigroupKInstanceImplicits {
    @JvmStatic
    fun <F> instance(MF: Monad<F>): OptionTSemigroupKInstance<F> = object : OptionTSemigroupKInstance<F> {
        override fun MF(): Monad<F> = MF
    }
}

interface OptionTMonoidKInstance<F> : MonoidK<OptionTKindPartial<F>>, OptionTSemigroupKInstance<F> {
    override fun <A> empty(): OptionT<F, A> = OptionT(MF().pure(Option.None))
}

object OptionTMonoidKInstanceImplicits {
    @JvmStatic
    fun <F> instance(MF: Monad<F>): OptionTMonoidKInstance<F> = object : OptionTMonoidKInstance<F> {
        override fun MF(): Monad<F> = MF
    }
}
