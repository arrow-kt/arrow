package kategory

interface WriterTInstances : WriterTInstances0 {

    fun <F, W> monadFilterForWriterT(MF: MonadFilter<F>, MW: Monoid<W>):
            MonadFilter<WriterTKindPartial<F, W>> = object : WriterTMonadFilter<F, W> {
        override fun F0(): MonadFilter<F> = MF
        override fun L0(): Monoid<W> = MW
    }
}

interface WriterTInstances0 : WriterTInstances1 {
    fun <F, W> monadWriterForWriterT(MF: Monad<F>, MW: Monoid<W>): MonadWriter<WriterTKindPartial<F, W>, W> =
            object : WriterTMonadWriter<F, W> {
                override fun F0(): Monad<F> = MF
                override fun L0(): Monoid<W> = MW
            }
}

interface WriterTInstances1: WriterTInstances2 {
    fun <F, W> applicativeForWriterT(MF: Monad<F>, MW: Monoid<W>): Applicative<WriterTKindPartial<F, W>> =
            object : WriterTApplicative<F, W> {
                override fun F0(): Monad<F> = MF
                override fun L0(): Monoid<W> = MW
            }

    fun <F, W> monoidKForWriterT(MF: Monad<F>, MKF: MonoidK<F>): MonoidK<WriterTKindPartial<F, W>> =
            object : WriterTMonoidK<F, W> {
                override fun MF(): Monad<F> = MF
                override fun F0(): MonoidK<F> = MKF
            }
}

interface WriterTInstances2 {
    fun <F, W> monadForWriterT(MF: Monad<F>, MW: Monoid<W>): Monad<WriterTKindPartial<F, W>> =
            object : WriterTMonad<F, W> {
                override fun F0(): Monad<F> = MF
                override fun L0(): Monoid<W> = MW
            }
}

interface WriterTApplicative<F, W> : Applicative<WriterTKindPartial<F, W>>, WriterTFunctor<F, W> {

    override fun F0(): Monad<F>
    fun L0(): Monoid<W>

    override fun <A> pure(a: A): HK<WriterTKindPartial<F, W>, A> = WriterT(F0(), F0().pure(L0().empty() toT a))

    override fun <A, B> ap(fa: HK<WriterTKindPartial<F, W>, A>, ff: HK<WriterTKindPartial<F, W>, (A) -> B>): HK<WriterTKindPartial<F, W>, B> =
            fa.ev().ap(F0(), ff.ev())
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

    override fun <A> writer(aw: Tuple2<W, A>): HK<WriterTKindPartial<F, W>, A> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

inline fun <reified F, W, A> WriterTMonadWriter<F, W>.writer(aw: Tuple2<W, A>): HK<WriterTKindPartial<F, W>, A> = WriterT.put(aw.b, aw.a, F0())

inline fun <reified F, W> WriterTMonadWriter<F, W>.tell(w: W): HK<WriterTKindPartial<F, W>, Unit> = WriterT.tell(w)

/*
interface WriterTInstances<F, W> :
        Functor<WriterTKindPartial<F, W>>,
        Applicative<WriterTKindPartial<F, W>>,
        Monad<WriterTKindPartial<F, W>> {
}
*/
