package arrow

@instance(WriterT::class)
interface WriterTFunctorInstance<F, W> : Functor<WriterTKindPartial<F, W>> {
    fun FF(): Functor<F>

    override fun <A, B> map(fa: WriterTKind<F, W, A>, f: (A) -> B): WriterT<F, W, B> = fa.ev().map({ f(it) }, FF())
}

@instance(WriterT::class)
interface WriterTApplicativeInstance<F, W> : Applicative<WriterTKindPartial<F, W>>, WriterTFunctorInstance<F, W> {

    override fun FF(): Monad<F>

    fun MM(): Monoid<W>

    override fun <A> pure(a: A): WriterTKind<F, W, A> =
            WriterT(FF().pure(MM().empty() toT a))

    override fun <A, B> ap(fa: WriterTKind<F, W, A>, ff: HK<WriterTKindPartial<F, W>, (A) -> B>): WriterT<F, W, B> =
            fa.ev().ap(ff, MM(), FF())

    override fun <A, B> map(fa: WriterTKind<F, W, A>, f: (A) -> B): WriterT<F, W, B> =
            fa.ev().map({ f(it) }, FF())
}

@instance(WriterT::class)
interface WriterTMonadInstance<F, W> : WriterTApplicativeInstance<F, W>, Monad<WriterTKindPartial<F, W>> {
    override fun <A, B> flatMap(fa: WriterTKind<F, W, A>, f: (A) -> HK<WriterTKindPartial<F, W>, B>): WriterT<F, W, B> =
            fa.ev().flatMap({ f(it).ev() }, MM(), FF())

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<WriterTKindPartial<F, W>, Either<A, B>>): WriterT<F, W, B> =
            WriterT.tailRecM(a, f, FF())

    override fun <A, B> ap(fa: WriterTKind<F, W, A>, ff: HK<WriterTKindPartial<F, W>, (A) -> B>): WriterT<F, W, B> =
            fa.ev().ap(ff, MM(), FF())
}

@instance(WriterT::class)
interface WriterTMonadFilterInstance<F, W> : WriterTMonadInstance<F, W>, MonadFilter<WriterTKindPartial<F, W>> {
    override fun FF(): MonadFilter<F>

    override fun <A> empty(): WriterTKind<F, W, A> = WriterT(FF().empty())
}

@instance(WriterT::class)
interface WriterTSemigroupKInstance<F, W> : SemigroupK<WriterTKindPartial<F, W>> {

    fun SS(): SemigroupK<F>

    override fun <A> combineK(x: WriterTKind<F, W, A>, y: WriterTKind<F, W, A>): WriterT<F, W, A> =
            x.ev().combineK(y, SS())
}

@instance(WriterT::class)
interface WriterTMonoidKInstance<F, W> : MonoidK<WriterTKindPartial<F, W>>, WriterTSemigroupKInstance<F, W> {

    override fun SS(): MonoidK<F>

    override fun <A> empty(): WriterT<F, W, A> = WriterT(SS().empty())
}

@instance(WriterT::class)
interface WriterTMonadWriterInstance<F, W> : MonadWriter<WriterTKindPartial<F, W>, W>, WriterTMonadInstance<F, W> {

    override fun <A> listen(fa: WriterTKind<F, W, A>): HK<WriterTKindPartial<F, W>, Tuple2<W, A>> =
            WriterT(FF().flatMap(fa.ev().content(FF()), { a -> FF().map(fa.ev().write(FF()), { l -> Tuple2(l, Tuple2(l, a)) }) }))

    override fun <A> pass(fa: HK<WriterTKindPartial<F, W>, Tuple2<(W) -> W, A>>): WriterT<F, W, A> =
            WriterT(FF().flatMap(fa.ev().content(FF()), { tuple2FA -> FF().map(fa.ev().write(FF()), { l -> Tuple2(tuple2FA.a(l), tuple2FA.b) }) }))

    override fun <A> writer(aw: Tuple2<W, A>): WriterT<F, W, A> = WriterT.put2(aw.b, aw.a, FF())

    override fun tell(w: W): HK<WriterTKindPartial<F, W>, Unit> = WriterT.tell2(w, FF())

}
