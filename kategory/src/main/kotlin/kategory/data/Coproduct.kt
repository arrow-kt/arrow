package kategory

typealias CoproductF<F> = HK<Coproduct.F, F>
typealias CoproductFG<F, G> = HK2<Coproduct.F, F, G>
typealias CoproductKind<F, G, A> = HK3<Coproduct.F, F, G, A>

fun <F, G, A> CoproductKind<F, G, A>.ev(): Coproduct<F, G, A> =
        this as Coproduct<F, G, A>

data class Coproduct<F, G, A>(val CF: Comonad<F>, val CG: Comonad<G>, val run: Either<HK<F, A>, HK<G, A>>) : CoproductKind<F, G, A> {

    class F private constructor()

    fun <B> map(f: (A) -> B): Coproduct<F, G, B> =
            Coproduct(CF, CG, run.bimap(CF.lift(f), CG.lift(f)))

    fun <B> coflatMap(f: (Coproduct<F, G, A>) -> B): Coproduct<F, G, B> =
            Coproduct(CF, CG, run.bimap(
                    { CF.coflatMap(it, { f(Coproduct(CF, CG, Either.Left(it))) }) },
                    { CG.coflatMap(it, { f(Coproduct(CF, CG, Either.Right(it))) }) }
            ))

    fun extract(): A =
            run.fold({ CF.extract(it) }, { CG.extract(it) })

    fun <H> fold(f: FunctionK<F, H>, g: FunctionK<G, H>): HK<H, A> =
            run.fold({ f(it) }, { g(it) })

    fun <B> foldL(b: B, f: (B, A) -> B, FF: Foldable<F>, FG: Foldable<G>): B =
            run.fold({ FF.foldL(it, b, f) }, { FG.foldL(it, b, f) })

    fun <B> foldR(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>, FF: Foldable<F>, FG: Foldable<G>): Eval<B> =
            run.fold({ FF.foldR(it, lb, f) }, { FG.foldR(it, lb, f) })

    fun <H, B> traverse(f: (A) -> HK<H, B>, GA: Applicative<H>, FT: Traverse<F>, GT: Traverse<G>): HK<H, Coproduct<F, G, B>> =
            run.fold({
                GA.map(FT.traverse(it, f, GA), { Coproduct(CF, CG, Either.Left(it)) })
            }, {
                GA.map(GT.traverse(it, f, GA), { Coproduct(CF, CG, Either.Right(it)) })
            })

    companion object {
        inline operator fun <reified F, reified G, A> invoke(run: Either<HK<F, A>, HK<G, A>>, CF: Comonad<F> = comonad<F>(), CG: Comonad<G> = comonad<G>()): Coproduct<F, G, A> =
                Coproduct(CF, CG, run)

        fun <F, G> comonad(): CoproductComonad<F, G> = object : CoproductComonad<F, G> {}

        fun <F, G> functor(): CoproductFunctor<F, G> = object : CoproductFunctor<F, G> {}

        inline fun <reified F, reified G> traverse(FF: Traverse<F> = traverse<F>(), FG: Traverse<G> = traverse<G>()): CoproductTraverse<F, G> = object : CoproductTraverse<F, G> {
            override fun FF(): Traverse<F> = FF

            override fun FG(): Traverse<G> = FG
        }
    }

}

inline fun <reified F, reified G, A> Either<HK<F, A>, HK<G, A>>.coproduct(CF: Comonad<F> = comonad(), CG: Comonad<G> = comonad()): Coproduct<F, G, A> =
        Coproduct(CF, CG, this)
