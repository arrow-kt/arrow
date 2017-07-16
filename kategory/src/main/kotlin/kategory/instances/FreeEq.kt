package kategory

data class FreeEq<in F, in G, in A>(private val interpreter: FunctionK<F, G>, private val MG: Monad<G>) : Eq<HK<FreeF<F>, A>> {
    override fun eqv(a: HK<FreeF<F>, A>, b: HK<FreeF<F>, A>): Boolean =
            a.ev().foldMap(interpreter, MG) == b.ev().foldMap(interpreter, MG)

    companion object {
        inline operator fun <F, reified G, A> invoke(interpreter: FunctionK<F, G>, MG: Monad<G> = monad(), dummy: Unit = Unit): FreeEq<F, G, A> =
                FreeEq(interpreter, MG)
    }
}