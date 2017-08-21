package kategory

interface StateTInstances<F, S> :
        Functor<StateTKindPartial<F, S>>,
        Applicative<StateTKindPartial<F, S>>,
        Monad<StateTKindPartial<F, S>>,
        MonadState<StateTKindPartial<F, S>, S> {

    fun MF(): Monad<F>

    override fun <A, B> flatMap(fa: HK<StateTKindPartial<F, S>, A>, f: (A) -> HK<StateTKindPartial<F, S>, B>): StateT<F, S, B> = fa.ev().flatMap(f)

    override fun <A, B> map(fa: HK<StateTKindPartial<F, S>, A>, f: (A) -> B): StateT<F, S, B> = fa.ev().map(f)

    override fun <A> pure(a: A): StateT<F, S, A> = StateT(MF(), MF().pure({ s: S -> MF().pure(Tuple2(s, a)) }))

    override fun <A, B> ap(fa: HK<StateTKindPartial<F, S>, A>, ff: HK<StateTKindPartial<F, S>, (A) -> B>): StateT<F, S, B> =
            ff.ev().map2(fa.ev()) { f, a -> f(a) }

    override fun <A, B, Z> map2(fa: HK<StateTKindPartial<F, S>, A>, fb: HK<StateTKindPartial<F, S>, B>, f: (Tuple2<A, B>) -> Z): StateT<F, S, Z> =
            fa.ev().map2(fb.ev(), { a, b -> f(Tuple2(a, b)) })

    @Suppress("UNCHECKED_CAST")
    override fun <A, B, Z> map2Eval(fa: HK<StateTKindPartial<F, S>, A>, fb: Eval<HK<StateTKindPartial<F, S>, B>>, f: (Tuple2<A, B>) -> Z):
            Eval<StateT<F, S, Z>> = fa.ev().map2Eval(fb as Eval<StateT<F, S, B>>) { a, b -> f(Tuple2(a, b)) }

    override fun <A, B> product(fa: HK<StateTKindPartial<F, S>, A>, fb: HK<StateTKindPartial<F, S>, B>): HK<StateTKindPartial<F, S>, Tuple2<A, B>> =
            fa.ev().product(fb.ev())

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<StateTKindPartial<F, S>, Either<A, B>>): StateT<F, S, B> =
            StateT(MF(), MF().pure({ s: S ->
                MF().tailRecM(Tuple2(s, a), { (s, a0) ->
                    MF().map(f(a0).runM(s)) { (s, ab) ->
                        ab.bimap({ a1 -> Tuple2(s, a1) }, { b -> Tuple2(s, b) })
                    }
                })
            }))

    override fun get(): StateT<F, S, S> = StateT(MF(), MF().pure({ s: S -> MF().pure(Tuple2(s, s)) }))

    override fun set(s: S): StateT<F, S, Unit> = StateT(MF(), MF().pure({ _: S -> MF().pure(Tuple2(s, Unit)) }))

}

interface StateTSemigroupK<F, S> : SemigroupK<StateTKindPartial<F, S>> {

    fun F(): Monad<F>
    fun G(): SemigroupK<F>

    override fun <A> combineK(x: HK<HK2<StateTHK, F, S>, A>, y: HK<HK2<StateTHK, F, S>, A>): StateT<F, S, A> =
            StateT(F(), F().pure({ s -> G().combineK(x.ev().run(s), y.ev().run(s)) }))
}
