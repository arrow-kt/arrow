package katz

typealias CoproductF<F> = HK<Coproduct.F, F>
typealias CoproductFG<F, G> = HK2<Coproduct.F, F, G>
typealias CoproductKind<F, G, A> = HK3<Coproduct.F, F, G, A>

fun <F, G, A> CoproductKind<F, G, A>.ev(): Coproduct<F, G, A> = this as Coproduct<F, G, A>

data class Coproduct<F, G, A>(val CF: Comonad<F>, val CG: Comonad<G>, val run: Either<HK<F, A>, HK<G, A>>) : CoproductKind<F, G, A> {

    class F private constructor()

    fun <B> map(f: (A) -> B): Coproduct<F, G, B> {
        return Coproduct(CF, CG, run.bimap(CF.lift(f), CG.lift(f)))
    }

    fun <B> coflatMap(f: (Coproduct<F, G, A>) -> B): Coproduct<F, G, B> =
            Coproduct(CF, CG, run.bimap(
                    { CF.coflatMap(it, { f(Coproduct(CF, CG, Either.Left(it))) }) },
                    { CG.coflatMap(it, { f(Coproduct(CF, CG, Either.Right(it))) }) }
            ))

    fun extract(): A =
            run.fold({ CF.extract(it) }, { CG.extract(it) })

    fun <H> fold(f: FunctionK<F, H>, g: FunctionK<G, H>): HK<H, A> =
            run.fold({ f(it) }, { g(it) })

    companion object {
        inline operator fun <reified F, reified G, A> invoke(run: Either<HK<F, A>, HK<G, A>>, CF: Comonad<F> = comonad<F>(), CG: Comonad<G> = comonad<G>()) =
                Coproduct(CF, CG, run)
    }

}

