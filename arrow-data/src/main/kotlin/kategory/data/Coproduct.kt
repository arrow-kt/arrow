package arrow

@higherkind data class Coproduct<F, G, A>(val run: Either<HK<F, A>, HK<G, A>>) : CoproductKind<F, G, A>, CoproductKindedJ<F, G, A> {

    fun <B> map(CF: Functor<F>, CG: Functor<G>, f: (A) -> B): Coproduct<F, G, B> = Coproduct(run.bimap(CF.lift(f), CG.lift(f)))

    fun <B> coflatMap(CF: Comonad<F>, CG: Comonad<G>, f: (Coproduct<F, G, A>) -> B): Coproduct<F, G, B> =
            Coproduct(run.bimap(
                    { CF.coflatMap(it, { f(Coproduct(Left(it))) }) },
                    { CG.coflatMap(it, { f(Coproduct(Right(it))) }) }
            ))

    fun extract(CF: Comonad<F>, CG: Comonad<G>): A = run.fold({ CF.extract(it) }, { CG.extract(it) })

    fun <H> fold(f: FunctionK<F, H>, g: FunctionK<G, H>): HK<H, A> = run.fold({ f(it) }, { g(it) })

    fun <B> foldLeft(b: B, f: (B, A) -> B, FF: Foldable<F>, FG: Foldable<G>): B = run.fold({ FF.foldLeft(it, b, f) }, { FG.foldLeft(it, b, f) })

    fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>, FF: Foldable<F>, FG: Foldable<G>): Eval<B> =
            run.fold({ FF.foldRight(it, lb, f) }, { FG.foldRight(it, lb, f) })

    fun <H, B> traverse(f: (A) -> HK<H, B>, GA: Applicative<H>, FT: Traverse<F>, GT: Traverse<G>): HK<H, Coproduct<F, G, B>> =
            run.fold({
                GA.map(FT.traverse(it, f, GA), { Coproduct<F, G, B>(Left(it)) })
            }, {
                GA.map(GT.traverse(it, f, GA), { Coproduct<F, G, B>(Right(it)) })
            })

    companion object {
        inline operator fun <reified F, reified G, A> invoke(run: Either<HK<F, A>, HK<G, A>>): Coproduct<F, G, A> = Coproduct(run)

        inline fun <reified F, reified G> comonad(CF: Comonad<F> = arrow.comonad(), CG: Comonad<G>): CoproductComonadInstance<F, G> =
                CoproductComonadInstanceImplicits.instance(CF, CG)

        inline fun <reified F, reified G> functor(FF: Functor<F> = arrow.functor(), FG: Functor<G> = arrow.functor()): CoproductFunctorInstance<F, G> =
                CoproductFunctorInstanceImplicits.instance(FF, FG)

        inline fun <reified F, reified G> traverse(FF: Traverse<F> = traverse<F>(), FG: Traverse<G> = traverse<G>()): CoproductTraverseInstance<F, G> =
                CoproductTraverseInstanceImplicits.instance(FF, FG)

        inline fun <reified F, reified G> foldable(FF: Foldable<F> = foldable<F>(), FG: Foldable<G> = foldable<G>()): CoproductFoldableInstance<F, G> =
                CoproductFoldableInstanceImplicits.instance(FF, FG)
    }

}

inline fun <reified F, reified G, A> Either<HK<F, A>, HK<G, A>>.coproduct(): Coproduct<F, G, A> =
        Coproduct(this)
