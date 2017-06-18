package kategory

data class WriterTMonad<F, W>(val MM: Monad<F>, val SG: Monoid<W>, val dummy: Unit = Unit) : Monad<WriterF<F, W>> {
    override fun <A> pure(a: A): HK<WriterF<F, W>, A> =
            WriterT(MM, MM.pure(SG.empty() toT a))

    override fun <A, B> map(fa: HK<WriterF<F, W>, A>, f: (A) -> B): HK<WriterF<F, W>, B> =
            fa.ev().map { f(it) }

    override fun <A, B> flatMap(fa: WriterTKind<F, W, A>, f: (A) -> HK<WriterF<F, W>, B>): WriterT<F, W, B> =
            fa.ev().flatMap({ f(it).ev() }, SG)

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<WriterF<F, W>, Either<A, B>>): WriterT<F, W, B> =
            WriterT(MM, MM.tailRecM(a, {
                MM.map(f(it).ev().value) {
                    when (it.b) {
                        is Either.Left<A> -> Either.Left(it.b.a)
                        is Either.Right<B> -> Either.Right(it.a toT it.b.b)
                    }
                }
            }))

    companion object {
        inline operator fun <reified F, reified W> invoke(MF: Monad<F> = monad<F>(), SG: Monoid<W>): WriterTMonad<F, W> =
                WriterTMonad(MF, SG, Unit)
    }
}