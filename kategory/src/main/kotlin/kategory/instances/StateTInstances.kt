package kategory

interface StateTInstances<F, S> :
        Functor<StateTF<F, S>>,
        Applicative<StateTF<F, S>>,
        Monad<StateTF<F, S>>,
        MonadState<StateTF<F, S>, S> {

    fun MF(): Monad<F>

    override fun <A, B> flatMap(fa: HK<StateTF<F, S>, A>, f: (A) -> HK<StateTF<F, S>, B>): StateT<F, S, B> =
            fa.ev().flatMap(f)

    override fun <A, B> map(fa: HK<StateTF<F, S>, A>, f: (A) -> B): StateT<F, S, B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): StateT<F, S, A> =
            StateT(MF(), MF().pure({ s: S -> MF().pure(Tuple2(s, a)) }))

    override fun <A, B> ap(fa: HK<StateTF<F, S>, A>, ff: HK<StateTF<F, S>, (A) -> B>): StateT<F, S, B> =
            ff.ev().map2(fa.ev()) { f, a -> f(a) }

    override fun <A, B, Z> map2(fa: HK<StateTF<F, S>, A>, fb: HK<StateTF<F, S>, B>, f: (Tuple2<A, B>) -> Z): StateT<F, S, Z> =
            fa.ev().map2(fb.ev(), { a, b -> f(Tuple2(a, b)) })

    @Suppress("UNCHECKED_CAST")
    override fun <A, B, Z> map2Eval(fa: HK<StateTF<F, S>, A>, fb: Eval<HK<StateTF<F, S>, B>>, f: (Tuple2<A, B>) -> Z): Eval<StateT<F, S, Z>> =
            fa.ev().map2Eval(fb as Eval<StateT<F, S, B>>) { a, b -> f(Tuple2(a, b)) }

    override fun <A, B> product(fa: HK<StateTF<F, S>, A>, fb: HK<StateTF<F, S>, B>): HK<StateTF<F, S>, Tuple2<A, B>> =
            fa.ev().product(fb.ev())

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<StateTF<F, S>, Either<A, B>>): StateT<F, S, B> =
            StateT(MF(), MF().pure({ s: S ->
                MF().tailRecM(Tuple2(s, a), { (s, a0) ->
                    MF().map(f(a0).ev().run(s)) { (s, ab) ->
                        ab.bimap({ a1 -> Tuple2(s, a1) }, { b -> Tuple2(s, b) })
                    }
                })
            }))

    override fun get(): HK<StateTF<F, S>, S> = StateT(MF, { s: S -> MF.pure(Tuple2(s, s)) })

    /*def get[F[_], S](implicit F: Applicative[F]): StateT[F, S, S] =
    StateT(s => F.pure((s, s)))

    def set[F[_], S](s: S)(implicit F: Applicative[F]): StateT[F, S, Unit] =
    StateT(_ => F.pure((s, ())))*/
}
