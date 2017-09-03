package kategory

interface WriterTApplicative<F, W> : Applicative<WriterTKindPartial<F, W>>, WriterTFunctor<F, W> {

    override fun F0(): Monad<F>
    fun L0(): Monoid<W>

    override fun <A> pure(a: A): HK<WriterTKindPartial<F, W>, A> = WriterT(F0(), F0().pure(L0().empty() toT a))

    override fun <A, B> ap(fa: HK<WriterTKindPartial<F, W>, A>, ff: HK<WriterTKindPartial<F, W>, (A) -> B>): HK<WriterTKindPartial<F, W>, B> =
            ap(fa, ff)

    override fun <A, B> map(fa: HK<WriterTKindPartial<F, W>, A>, f: (A) -> B): HK<WriterTKindPartial<F, W>, B> {
        return super<WriterTFunctor>.map(fa, f)
    }
}

interface WriterTMonad<F, W> : WriterTApplicative<F, W>, Monad<WriterTKindPartial<F, W>> {
    override fun <A, B> flatMap(fa: WriterTKind<F, W, A>, f: (A) -> HK<WriterTKindPartial<F, W>, B>): WriterT<F, W, B> = fa.ev().flatMap({ f(it).ev() }, L0())

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<WriterTKindPartial<F, W>, Either<A, B>>): WriterT<F, W, B> =
            WriterT(F0(), F0().tailRecM(a, {
                F0().map(f(it).ev().value) {
                    when (it.b) {
                        is Either.Left<A, B> -> Either.Left(it.b.a)
                        is Either.Right<A, B> -> Either.Right(it.a toT it.b.b)
                    }
                }
            }))

    override fun <A, B> ap(fa: HK<WriterTKindPartial<F, W>, A>, ff: HK<WriterTKindPartial<F, W>, (A) -> B>): HK<WriterTKindPartial<F, W>, B> {
        return super<Monad>.ap(fa, ff)
    }
}

interface WriterTFunctor<F, W> : Functor<WriterTKindPartial<F, W>> {
    fun F0(): Functor<F>

    override fun <A, B> map(fa: HK<WriterTKindPartial<F, W>, A>, f: (A) -> B): HK<WriterTKindPartial<F, W>, B> = fa.ev().map { f(it) }
}

interface WriterTMonadFilter<F, W> : WriterTMonad<F, W>, MonadFilter<WriterTKindPartial<F, W>> {
    override fun F0(): MonadFilter<F>

    override fun <A> empty(): HK<WriterTKindPartial<F, W>, A> = WriterT(F0(), F0().empty())
}

interface WriterTSemigroupK<F, W> : SemigroupK<WriterTKindPartial<F, W>> {

    fun MF(): Monad<F>

    fun GF(): SemigroupK<F>

    override fun <A> combineK(x: HK<WriterTKindPartial<F, W>, A>, y: HK<WriterTKindPartial<F, W>, A>):
            WriterT<F, W, A> = WriterT(MF(), GF().combineK(x.ev().value, y.ev().value))
}

interface WriterTMonoidK<F, W> : MonoidK<WriterTKindPartial<F, W>>, WriterTSemigroupK<F, W> {

    override fun GF(): MonoidK<F>

    override fun <A> empty(): HK<WriterTKindPartial<F, W>, A> = WriterT(MF(), GF().empty())
}

interface WriterTMonadWriter<F, W> : MonadWriter<WriterTKindPartial<F, W>, W>, WriterTMonad<F, W> {

    override fun <A> listen(fa: HK<WriterTKindPartial<F, W>, A>): HK<WriterTKindPartial<F, W>, Tuple2<W, A>> =
            WriterT(F0(), F0().flatMap(fa.ev().content(), { a -> F0().map(fa.ev().write(), { l -> Tuple2(l, Tuple2(l, a)) }) }))

    override fun <A> pass(fa: HK<WriterTKindPartial<F, W>, Tuple2<(W) -> W, A>>): HK<WriterTKindPartial<F, W>, A> =
            WriterT(F0(), F0().flatMap(fa.ev().content(), { tuple2FA -> F0().map(fa.ev().write(), { l -> Tuple2(tuple2FA.a(l), tuple2FA.b) }) }))

    override fun <A> writer(aw: Tuple2<W, A>): HK<WriterTKindPartial<F, W>, A> = WriterT.put2(aw.b, aw.a, F0())

    override fun tell(w: W): HK<WriterTKindPartial<F, W>, Unit> = WriterT.tell2(w, F0())
}
