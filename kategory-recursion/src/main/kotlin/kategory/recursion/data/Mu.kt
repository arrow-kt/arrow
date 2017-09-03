package kategory

@higherkind data class Mu<F>(val FF: Functor<F>, val unmu: FunctionK<Function1KindPartial<NestedType<F, MuHK, F>>, IdHK>) : MuKind<F> {

    fun projectT(): HK<Nested<MuHK, F>, MuKind<F>> =
            cataT<HK<Nested<MuHK, F>, MuKind<F>>>(
                    { fa: HK<F, NestedType<MuHK, F, MuKind<F>>> ->
                        val map: HK<F, MuKind<F>> = FF.map(fa) { bla: NestedType<MuHK, F, MuKind<F>> -> embedT(bla, FF) }
                        val nest: NestedType<F, MuHK, F> = map.nest()
                        val embedT: HK<Nested<MuHK, F>, MuKind<F>> = embedT<Int>(fa.projectT().nest(), FF).nest()
                        embedT
                    }.k()
            ).value

    fun <A> cataT(f: Function1<HK<F, NestedType<MuHK, F, MuKind<F>>>, A>): Id<A> =
            unmu({ a: HK<F, MuHK> -> f(a) }.k()).ev()

    companion object {
        //inline fun <reified F> embedT(compFG: NestedType<Nested<MuHK, F>, MuHK, F>, dummy: Unit = Unit, FF: Functor<F> = functor<F>()): MuKind<F> =
        //        embedT(compFG, FF)

        fun <F> embedT1(compFG: NestedType<F, MuHK, F>, FF: Functor<HK<F, MuKind<F>>>): NestedType<MuHK, F, MuKind<F>> =
                Mu(FF, object : FunctionK<Function1KindPartial<NestedType<F, MuHK, F>>, IdHK> {
                    override fun <A> invoke(fa: HK<Function1KindPartial<NestedType<F, MuHK, F>>, A>): HK<IdHK, A> {
                        val mu: HK<F, MuKind<F>> = compFG.unnest()
                        FF.map(mu, { a: MuKind<F> -> a.ev().cataT(fa.ev()) })
                        val ffa: Function1<HK<HK<F, MuKind<F>>, Nested<MuHK, HK<F, MuKind<F>>>>, A> = { a: HK<HK<F, MuKind<F>>, Nested<MuHK, HK<F, MuKind<F>>>> -> fa.ev().invoke(a) }.k()
                        return mu.cataT(ffa)                    }
                }).nest()


        fun <F> embedT(compFG: HK<Nested<MuHK, F>, MuKind<F>>, FF: Functor<F>): MuKind<F> =
                Mu(FF, object : FunctionK<Function1KindPartial<HK<F, MuHK>>, IdHK> {
                    override fun <A> invoke(fa: HK<Function1KindPartial<HK<F, MuHK>>, A>): Id<A> {
                        val mu: Mu<HK<F, MuKind<F>>> = compFG.unnest().ev()
                        val ffa: Function1<HK<HK<F, MuKind<F>>, Nested<MuHK, HK<F, MuKind<F>>>>, A> = { a: HK<HK<F, MuKind<F>>, Nested<MuHK, HK<F, MuKind<F>>>> -> fa.ev().invoke(a) }.k()
                        return mu.cataT(ffa)
                    }
                })

        inline fun <F> instances(FF: Functor<F>): MuInstances<F> = object : MuInstances<F> {
            override fun FG(): Functor<F> = FF
        }

        inline fun <reified F> birecursive(FF: Functor<F> = functor<F>()): Birecursive<MuHK, F> = instances(FF)
    }
}