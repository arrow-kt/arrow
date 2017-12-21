package arrow

interface CoproductFunctorInstance<F, G> : Functor<CoproductKindPartial<F, G>> {

    fun FF(): Functor<F>

    fun FG(): Functor<G>

    override fun <A, B> map(fa: CoproductKind<F, G, A>, f: (A) -> B): Coproduct<F, G, B> = fa.ev().map(FF(), FG(), f)
}

object CoproductFunctorInstanceImplicits {
    fun <F, G> instance(FF: Functor<F>, FG: Functor<G>): CoproductFunctorInstance<F, G> = object : CoproductFunctorInstance<F, G> {
        override fun FF(): Functor<F> = FF

        override fun FG(): Functor<G> = FG
    }
}

interface CoproductComonadInstance<F, G> : Comonad<CoproductKindPartial<F, G>> {

    fun CF(): Comonad<F>

    fun CG(): Comonad<G>

    override fun <A, B> coflatMap(fa: CoproductKind<F, G, A>, f: (CoproductKind<F, G, A>) -> B): Coproduct<F, G, B> = fa.ev().coflatMap(CF(), CG(), f)

    override fun <A> extract(fa: CoproductKind<F, G, A>): A = fa.ev().extract(CF(), CG())

    override fun <A, B> map(fa: CoproductKind<F, G, A>, f: (A) -> B): Coproduct<F, G, B> = fa.ev().map(CF(), CG(), f)

}

object CoproductComonadInstanceImplicits {
    fun <F, G> instance(CF: Comonad<F>, CG: Comonad<G>): CoproductComonadInstance<F, G> = object : CoproductComonadInstance<F, G> {
        override fun CF(): Comonad<F> = CF

        override fun CG(): Comonad<G> = CG
    }
}

interface CoproductFoldableInstance<F, G> : Foldable<CoproductKindPartial<F, G>> {

    fun FF(): Foldable<F>

    fun FG(): Foldable<G>

    override fun <A, B> foldLeft(fa: CoproductKind<F, G, A>, b: B, f: (B, A) -> B): B = fa.ev().foldLeft(b, f, FF(), FG())

    override fun <A, B> foldRight(fa: CoproductKind<F, G, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> = fa.ev().foldRight(lb, f, FF(), FG())

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

    override fun <A, B> foldLeft(fa: CoproductKind<F, G, A>, b: B, f: (B, A) -> B): B = fa.ev().foldLeft(b, f, TF(), TG())

    override fun <A, B> foldRight(fa: CoproductKind<F, G, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> = fa.ev().foldRight(lb, f, TF(), TG())

}

object CoproductTraverseInstanceImplicits {
    fun <F, G> instance(TF: Traverse<F>, TG: Traverse<G>): CoproductTraverseInstance<F, G> = object : CoproductTraverseInstance<F, G> {
        override fun TF(): Traverse<F> = TF

        override fun TG(): Traverse<G> = TG
    }
}
