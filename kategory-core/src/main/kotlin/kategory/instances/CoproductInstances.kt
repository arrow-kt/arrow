package kategory

interface CoproductFunctorInstance<F, G> : Functor<CoproductKindPartial<F, G>> {
    override fun <A, B> map(fa: CoproductKind<F, G, A>, f: (A) -> B): Coproduct<F, G, B> = fa.ev().map(f)
}

object CoproductFunctorInstanceImplicits {
    fun <F, G> instance(): CoproductFunctorInstance<F, G> = object : CoproductFunctorInstance<F, G> {}
}

interface CoproductComonadInstance<F, G> : Comonad<CoproductKindPartial<F, G>> {

    override fun <A, B> coflatMap(fa: CoproductKind<F, G, A>, f: (CoproductKind<F, G, A>) -> B): Coproduct<F, G, B> = fa.ev().coflatMap(f)

    override fun <A> extract(fa: CoproductKind<F, G, A>): A = fa.ev().extract()

    override fun <A, B> map(fa: CoproductKind<F, G, A>, f: (A) -> B): Coproduct<F, G, B> = fa.ev().map(f)

    companion object {
        // Cobinding for HK2 requires an instance to infer the types.
        // As cobinding cannot be delegated you have to create an <Any, Any> so any internal type can be used
        fun any(): CoproductComonadInstance<Any, Any> = object : CoproductComonadInstance<Any, Any> {}
    }
}

object CoproductComonadInstanceImplicits {
    fun <F, G> instance(): CoproductComonadInstance<F, G> = object : CoproductComonadInstance<F, G> {}
}

interface CoproductFoldableInstance<F, G> : Foldable<CoproductKindPartial<F, G>> {

    fun FF(): Foldable<F>

    fun FG(): Foldable<G>

    override fun <A, B> foldL(fa: CoproductKind<F, G, A>, b: B, f: (B, A) -> B): B = fa.ev().foldL(b, f, FF(), FG())

    override fun <A, B> foldR(fa: CoproductKind<F, G, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> = fa.ev().foldR(lb, f, FF(), FG())

}

object CoproductFoldableInstanceImplicits {
    fun <F, G> instance(FF: Foldable<F>, FG: Foldable<G>): CoproductFoldableInstance<F, G> = object : CoproductFoldableInstance<F, G> {
        override fun FF(): Foldable<F> = FF

        override fun FG(): Foldable<G> = FG
    }
}

interface CoproductTraverseInstance<F, G> : Traverse<CoproductKindPartial<F, G>> {

    fun TF(): Traverse<F>

    fun TG(): Traverse<G>

    override fun <H, A, B> traverse(fa: CoproductKind<F, G, A>, f: (A) -> HK<H, B>, GA: Applicative<H>): HK<H, Coproduct<F, G, B>> =
            fa.ev().traverse(f, GA, TF(), TG())

    override fun <A, B> foldL(fa: CoproductKind<F, G, A>, b: B, f: (B, A) -> B): B = fa.ev().foldL(b, f, TF(), TG())

    override fun <A, B> foldR(fa: CoproductKind<F, G, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> = fa.ev().foldR(lb, f, TF(), TG())

}

object CoproductTraverseInstanceImplicits {
    fun <F, G> instance(TF: Traverse<F>, TG: Traverse<G>): CoproductTraverseInstance<F, G> = object : CoproductTraverseInstance<F, G> {
        override fun TF(): Traverse<F> = TF

        override fun TG(): Traverse<G> = TG
    }
}
