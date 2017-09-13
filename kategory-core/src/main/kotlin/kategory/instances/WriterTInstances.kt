package kategory

interface WriterTFunctorInstance<F, W> : Functor<WriterTKindPartial<F, W>> {
    fun FF(): Functor<F>

    override fun <A, B> map(fa: WriterTKind<F, W, A>, f: (A) -> B): WriterT<F, W, B> = fa.ev().map({ f(it) }, FF())
}

object WriterTFunctorInstanceImplicits {
    @JvmStatic
    fun <F, W> instance(FF: Functor<F>): WriterTFunctorInstance<F, W> = object : WriterTFunctorInstance<F, W> {
        override fun FF(): Functor<F> = FF
    }
}

@instance
interface WriterTApplicativeInstance<F, W> : Applicative<WriterTKindPartial<F, W>>, WriterTFunctorInstance<F, W> {

    fun MF(): Monad<F>

    fun MW(): Monoid<W>

    override fun <A> pure(a: A): WriterTKind<F, W, A> =
            WriterT(MF().pure(MW().empty() toT a))

    override fun <A, B> ap(fa: WriterTKind<F, W, A>, ff: HK<WriterTKindPartial<F, W>, (A) -> B>): WriterT<F, W, B> =
            fa.ev().ap(ff, MW(), MF())

    override fun <A, B> map(fa: WriterTKind<F, W, A>, f: (A) -> B): WriterT<F, W, B> =
            fa.ev().map({ f(it) }, FF())
}

//object WriterTApplicativeInstanceImplicits {
//    @JvmStatic
//    fun <F, W> instance(MF: Monad<F>, MW: Monoid<W>): WriterTApplicativeInstance<F, W> = object : WriterTApplicativeInstance<F, W> {
//        override fun FF(): Functor<F> = MF
//        override fun MF(): Monad<F> = MF
//        override fun MW(): Monoid<W> = MW
//    }
//}

interface WriterTMonadInstance<F, W> : WriterTApplicativeInstance<F, W>, Monad<WriterTKindPartial<F, W>> {
    override fun <A, B> flatMap(fa: WriterTKind<F, W, A>, f: (A) -> HK<WriterTKindPartial<F, W>, B>): WriterT<F, W, B> =
            fa.ev().flatMap({ f(it).ev() }, MW(), MF())

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<WriterTKindPartial<F, W>, Either<A, B>>): WriterT<F, W, B> =
            WriterT.tailRecM(a, f, MF())

    override fun <A, B> ap(fa: WriterTKind<F, W, A>, ff: HK<WriterTKindPartial<F, W>, (A) -> B>): WriterT<F, W, B> =
            fa.ev().ap(ff, MW(), MF())
}

object WriterTMonadInstanceImplicits {
    @JvmStatic
    fun <F, W> instance(MF: Monad<F>, MW: Monoid<W>): WriterTMonadInstance<F, W> = object : WriterTMonadInstance<F, W> {
        override fun FF(): Functor<F> = MF
        override fun MF(): Monad<F> = MF
        override fun MW(): Monoid<W> = MW
    }
}

interface WriterTMonadFilterInstance<F, W> : WriterTMonadInstance<F, W>, MonadFilter<WriterTKindPartial<F, W>> {
    fun MFL(): MonadFilter<F>

    override fun <A> empty(): WriterTKind<F, W, A> = WriterT(MFL().empty())
}

object WriterTMonadFilterInstanceImplicits {
    @JvmStatic
    fun <F, W> instance(MFL: MonadFilter<F>, MW: Monoid<W>): WriterTMonadFilterInstance<F, W> = object : WriterTMonadFilterInstance<F, W> {
        override fun FF(): Functor<F> = MFL

        override fun MF(): Monad<F> = MFL

        override fun MW(): Monoid<W> = MW

        override fun MFL(): MonadFilter<F> = MFL
    }
}

interface WriterTSemigroupKInstance<F, W> : SemigroupK<WriterTKindPartial<F, W>> {

    fun MF(): Monad<F>

    fun SF(): SemigroupK<F>

    override fun <A> combineK(x: WriterTKind<F, W, A>, y: WriterTKind<F, W, A>): WriterT<F, W, A> =
            x.ev().combineK(y, SF())
}

object WriterTSemigroupKInstanceImplicits {
    @JvmStatic
    fun <F, W> instance(MF: Monad<F>, SKF: SemigroupK<F>): WriterTSemigroupKInstance<F, W> = object : WriterTSemigroupKInstance<F, W> {
        override fun MF(): Monad<F> = MF

        override fun SF(): SemigroupK<F> = SKF
    }
}

interface WriterTMonoidKInstance<F, W> : MonoidK<WriterTKindPartial<F, W>>, WriterTSemigroupKInstance<F, W> {

    fun MMF(): MonoidK<F>

    override fun <A> empty(): WriterT<F, W, A> = WriterT(MMF().empty())
}

object WriterTMonoidKInstanceImplicits {
    @JvmStatic
    fun <F, W> instance(MF: Monad<F>, MKF: MonoidK<F>): WriterTMonoidKInstance<F, W> = object : WriterTMonoidKInstance<F, W> {
        override fun MF(): Monad<F> = MF

        override fun SF(): SemigroupK<F> = MKF

        override fun MMF(): MonoidK<F> = MKF
    }
}

interface WriterTMonadWriterInstance<F, W> : MonadWriter<WriterTKindPartial<F, W>, W>, WriterTMonadInstance<F, W> {

    override fun <A> listen(fa: WriterTKind<F, W, A>): HK<WriterTKindPartial<F, W>, Tuple2<W, A>> =
            WriterT(MF().flatMap(fa.ev().content(MF()), { a -> MF().map(fa.ev().write(MF()), { l -> Tuple2(l, Tuple2(l, a)) }) }))

    override fun <A> pass(fa: HK<WriterTKindPartial<F, W>, Tuple2<(W) -> W, A>>): WriterT<F, W, A> =
            WriterT(MF().flatMap(fa.ev().content(MF()), { tuple2FA -> MF().map(fa.ev().write(MF()), { l -> Tuple2(tuple2FA.a(l), tuple2FA.b) }) }))

    override fun <A> writer(aw: Tuple2<W, A>): WriterT<F, W, A> = WriterT.put2(aw.b, aw.a, MF())

    override fun tell(w: W): HK<WriterTKindPartial<F, W>, Unit> = WriterT.tell2(w, MF())

}

object WriterTMonadWriterInstanceImplicits {
    @JvmStatic
    fun <F, W> instance(MF: Monad<F>, MW: Monoid<W>): WriterTMonadWriterInstance<F, W> = object : WriterTMonadWriterInstance<F, W> {
        override fun MF(): Monad<F> = MF
        override fun MW(): Monoid<W> = MW
        override fun FF(): Functor<F> = MF
    }
}
