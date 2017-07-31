package kategory

interface WriterTInstances<F, W> :
        Functor<WriterF<F, W>>,
        Applicative<WriterF<F, W>>,
        Monad<WriterF<F, W>> {

    fun MM(): Monad<F>

    fun SG(): Monoid<W>

    override fun <A> pure(a: A): HK<WriterF<F, W>, A> =
            WriterT(MM(), MM().pure(SG().empty() toT a))

    override fun <A, B> map(fa: HK<WriterF<F, W>, A>, f: (A) -> B): HK<WriterF<F, W>, B> =
            fa.ev().map { f(it) }

    override fun <A, B> flatMap(fa: WriterTKind<F, W, A>, f: (A) -> HK<WriterF<F, W>, B>): WriterT<F, W, B> =
            fa.ev().flatMap({ f(it).ev() }, SG())

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<WriterF<F, W>, Either<A, B>>): WriterT<F, W, B> =
            WriterT(MM(), MM().tailRecM(a, {
                MM().map(f(it).ev().value) {
                    when (it.b) {
                        is Either.Left<A, B> -> Either.Left(it.b.a)
                        is Either.Right<A, B> -> Either.Right(it.a toT it.b.b)
                    }
                }
            }))

}

interface WriterTSemigroupK<F, W> : SemigroupK<WriterF<F, W>> {

    fun MF(): Monad<F>

    fun F0(): SemigroupK<F>

    override fun <A> combineK(x: HK<WriterF<F, W>, A>, y: HK<WriterF<F, W>, A>): WriterT<F, W, A> =
            WriterT(MF(), F0().combineK(x.ev().value, y.ev().value))
}

interface WriterTMonoidK<F, W> : MonoidK<WriterF<F, W>>, WriterTSemigroupK<F, W> {

    override fun F0(): MonoidK<F>

    override fun <A> empty(): HK<WriterF<F, W>, A> = WriterT(MF(), F0().empty())
}
