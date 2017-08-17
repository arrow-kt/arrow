package kategory

interface WriterTInstances<F, W> :
        Functor<WriterTKindPartial<F, W>>,
        Applicative<WriterTKindPartial<F, W>>,
        Monad<WriterTKindPartial<F, W>> {

    fun MM(): Monad<F>

    fun SG(): Monoid<W>

    override fun <A> pure(a: A): HK<WriterTKindPartial<F, W>, A> = WriterT(MM(), MM().pure(SG().empty() toT a))

    override fun <A, B> map(fa: HK<WriterTKindPartial<F, W>, A>, f: (A) -> B): HK<WriterTKindPartial<F, W>, B> = fa.ev().map { f(it) }

    override fun <A, B> flatMap(fa: WriterTKind<F, W, A>, f: (A) -> HK<WriterTKindPartial<F, W>, B>): WriterT<F, W, B> = fa.ev().flatMap({ f(it).ev() }, SG())

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<WriterTKindPartial<F, W>, Either<A, B>>): WriterT<F, W, B> =
            WriterT(MM(), MM().tailRecM(a, {
                MM().map(f(it).ev().value) {
                    when (it.b) {
                        is Either.Left<A, B> -> Either.Left(it.b.a)
                        is Either.Right<A, B> -> Either.Right(it.a toT it.b.b)
                    }
                }
            }))

}

abstract class WriterTMonadWriter<F, W> : MonadWriter<WriterTKindPartial<F, W>, W> {

    companion object {

        inline fun <reified F, W, A> writer(aw: Tuple2<W, A>): WriterT<F, W, A> = WriterT.put(aw.b, aw.a)

        inline fun <reified F, W, A> listen(fa: HK<WriterTKindPartial<F, W>, A>, MF: Monad<F> = monad()): HK<WriterTKindPartial<F, W>, Tuple2<W, A>> =
                WriterT(MF, MF.flatMap(fa.ev().content(), { a -> MF.map(fa.ev().write(), { l -> Tuple2(l, Tuple2(l, a)) }) }))

        inline fun <reified F, W, A> pass(fa: HK<WriterTKindPartial<F, W>, Tuple2<(W) -> W, A>>, MF: Monad<F> = monad()): HK<WriterTKindPartial<F, W>, A> =
                WriterT(MF, MF.flatMap(fa.ev().content(), { tuple2FA -> MF.map(fa.ev().write(), { l -> Tuple2(tuple2FA.a(l), tuple2FA.b) }) }))

        inline fun <reified F, W, A> tell(w: W): HK<WriterTKindPartial<F, W>, Unit> = WriterT.tell(w)
    }
}

interface WriterTSemigroupK<F, W> : SemigroupK<WriterTKindPartial<F, W>> {

    fun MF(): Monad<F>

    fun F0(): SemigroupK<F>

    override fun <A> combineK(x: HK<WriterTKindPartial<F, W>, A>, y: HK<WriterTKindPartial<F, W>, A>):
            WriterT<F, W, A> = WriterT(MF(), F0().combineK(x.ev().value, y.ev().value))
}

interface WriterTMonoidK<F, W> : MonoidK<WriterTKindPartial<F, W>>, WriterTSemigroupK<F, W> {

    override fun F0(): MonoidK<F>

    override fun <A> empty(): HK<WriterTKindPartial<F, W>, A> = WriterT(MF(), F0().empty())
}
