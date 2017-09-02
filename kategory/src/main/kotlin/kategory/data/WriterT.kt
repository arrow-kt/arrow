package kategory

@Suppress("UNCHECKED_CAST") inline fun <F, W, A> WriterTKind<F, W, A>.value(): HK<F, Tuple2<W, A>> = this.ev().value

@higherkind data class WriterT<F, W, A>(val MF: Monad<F>, val value: HK<F, Tuple2<W, A>>) : WriterTKind<F, W, A> {

    companion object {
        inline fun <reified F, reified W, A> pure(a: A, MM: Monoid<W> = monoid(), MF: Monad<F> = kategory.monad()) = WriterT(MF.pure(MM.empty() toT a), MF)

        inline fun <reified F, W, A> both(w: W, a: A, MF: Monad<F> = kategory.monad()) = WriterT(MF.pure(w toT a), MF)

        inline fun <reified F, W, A> fromTuple(z: Tuple2<W, A>, MF: Monad<F> = kategory.monad()) = WriterT(MF.pure(z), MF)

        inline operator fun <reified F, W, A> invoke(value: HK<F, Tuple2<W, A>>, MF: Monad<F> = kategory.monad()) = WriterT(MF, value)

        inline fun <reified F, reified W> instances(MM: Monad<F> = kategory.monad(),
                                                    SG: Monoid<W> = kategory.monoid<W>(),
                                                    MF: MonadFilter<F> = kategory.monadFilter()): WriterTInstances<F, W> =
                object : WriterTInstances<F, W> {

                    override fun <A> writer(aw: Tuple2<W, A>): WriterT<F, W, A> = WriterT.put(aw.b, aw.a)

                    override fun <A> listen(fa: HK<WriterTKindPartial<F, W>, A>): HK<WriterTKindPartial<F, W>, Tuple2<W, A>> =
                            WriterT(MM, MM.flatMap(fa.ev().content(), { a -> MM.map(fa.ev().write(), { l -> Tuple2(l, Tuple2(l, a)) }) }))

                    override fun <A> pass(fa: HK<WriterTKindPartial<F, W>, Tuple2<(W) -> W, A>>): HK<WriterTKindPartial<F, W>, A> =
                            WriterT(MM, MM.flatMap(fa.ev().content(), { tuple2FA -> MM.map(fa.ev().write(), { l -> Tuple2(tuple2FA.a(l), tuple2FA.b) }) }))

                    override fun tell(w: W): HK<WriterTKindPartial<F, W>, Unit> = WriterT.tell(w)

                    override fun MM(): Monad<F> = MM

                    override fun SG(): Monoid<W> = SG

                    override fun F0(): MonadFilter<F> = MF
                }

        inline fun <reified F, reified W> functor(MM: Monad<F> = kategory.monad<F>(),
                                                  SG: Monoid<W> = kategory.monoid<W>(),
                                                  MF: MonadFilter<F> = kategory.monadFilter()): Functor<WriterTKindPartial<F, W>> =
                instances(MM, SG, MF)

        inline fun <reified F, reified W> applicative(MM: Monad<F> = kategory.monad<F>(),
                                                      SG: Monoid<W> = kategory.monoid<W>(),
                                                      MF: MonadFilter<F> = kategory.monadFilter()):
                Applicative<WriterTKindPartial<F, W>> = instances(MM, SG, MF)

        inline fun <reified F, reified W> monad(MM: Monad<F> = kategory.monad<F>(),
                                                SG: Monoid<W> = kategory.monoid<W>(),
                                                MF: MonadFilter<F> = kategory.monadFilter()): Monad<WriterTKindPartial<F, W>> =
                instances(MM, SG, MF)

        inline fun <reified F, reified W> semigroupK(MF: Monad<F> = monad<F>(), SGK: SemigroupK<F> = semigroupK<F>()): SemigroupK<WriterTKindPartial<F, W>> =
                object : WriterTSemigroupK<F, W> {
                    override fun MF(): Monad<F> = MF

                    override fun GF(): SemigroupK<F> = SGK
                }

        inline fun <reified F, reified W> monoidK(MF: Monad<F> = monad<F>(), MKF: MonoidK<F> = monoidK<F>()): MonoidK<WriterTKindPartial<F, W>> =
                object : WriterTMonoidK<F, W> {
                    override fun MF(): Monad<F> = MF

                    override fun GF(): MonoidK<F> = MKF
                }

        inline fun <reified F, reified W> monadWriter(MM: Monad<F> = kategory.monad(),
                                                      SG: Monoid<W> = kategory.monoid(),
                                                      MF: MonadFilter<F> = kategory.monadFilter()):
                MonadWriter<WriterTKindPartial<F, W>, W> = instances(MM, SG, MF)

        inline fun <reified F, reified W> monadFilter(MM: Monad<F> = kategory.monad(),
                                                      SG: Monoid<W> = kategory.monoid(),
                                                      MF: MonadFilter<F> = kategory.monadFilter()):
                MonadFilter<WriterTKindPartial<F, W>> = instances(MM, SG, MF)

        inline fun <reified F, W, A> putT(vf: HK<F, A>, w: W, MF: Monad<F> = kategory.monad()): WriterT<F, W, A> =
                WriterT(MF, MF.map(vf, { v -> Tuple2(w, v) }))

        inline fun <reified F, W, A> put(a: A, w: W, applicativeF: Applicative<F> = kategory.applicative()): WriterT<F, W, A> =
                WriterT.putT(applicativeF.pure(a), w)

        inline fun <reified F, W> tell(l: W, applicativeF: Applicative<F> = kategory.applicative()): WriterT<F, W, Unit> = WriterT.put(Unit, l)

        inline fun <reified F, reified W, A> value(v: A, applicativeF: Applicative<F> = kategory.applicative(), monoidW: Monoid<W> = monoid()):
                WriterT<F, W, A> = WriterT.put(v, monoidW.empty())

        inline fun <reified F, reified W, A> valueT(vf: HK<F, A>, functorF: Functor<F> = kategory.functor(), monoidW: Monoid<W> = monoid()): WriterT<F, W, A> =
                WriterT.putT(vf, monoidW.empty())
    }

    fun tell(w: W, SG: Semigroup<W>): WriterT<F, W, A> = mapAcc { SG.combine(it, w) }

    fun content(): HK<F, A> = MF.map(value, { it.b })

    fun write(): HK<F, W> = MF.map(value, { it.a })

    fun reset(MM: Monoid<W>): WriterT<F, W, A> = mapAcc { MM.empty() }

    inline fun <B> map(crossinline f: (A) -> B): WriterT<F, W, B> = WriterT(MF, MF.map(value, { it.a toT f(it.b) }))

    inline fun <U> mapAcc(crossinline f: (W) -> U): WriterT<F, U, A> = transform { f(it.a) toT it.b }

    inline fun <C, U> bimap(crossinline g: (W) -> U, crossinline f: (A) -> C): WriterT<F, U, C> = transform { g(it.a) toT f(it.b) }

    fun swap(): WriterT<F, A, W> = transform { it.b toT it.a }

    inline fun <B> flatMap(crossinline f: (A) -> WriterT<F, W, B>, SG: Semigroup<W>): WriterT<F, W, B> =
            WriterT(MF, MF.flatMap(value, { value -> MF.map(f(value.b).value, { SG.combine(it.a, value.a) toT it.b }) }))

    inline fun <B, U> transform(crossinline f: (Tuple2<W, A>) -> Tuple2<U, B>): WriterT<F, U, B> = WriterT(MF, MF.flatMap(value, { MF.pure(f(it)) }))

    fun <B> liftF(fa: HK<F, B>): WriterT<F, W, B> = WriterT(MF, MF.map2(fa, value, { it.b.a toT it.a }))

    inline fun <C> semiflatMap(crossinline f: (A) -> HK<F, C>, SG: Semigroup<W>): WriterT<F, W, C> = flatMap({ liftF(f(it)) }, SG)

    inline fun <B> subflatMap(crossinline f: (A) -> Tuple2<W, B>): WriterT<F, W, B> = transform({ f(it.b) })
}
