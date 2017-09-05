package kategory

@Suppress("UNCHECKED_CAST") inline fun <F, W, A> WriterTKind<F, W, A>.value(): HK<F, Tuple2<W, A>> = this.ev().value

@higherkind data class WriterT<F, W, A>(val value: HK<F, Tuple2<W, A>>) : WriterTKind<F, W, A> {

    companion object {

        inline fun <reified F, reified W, A> pure(a: A, MM: Monoid<W> = monoid(), MF: Monad<F> = kategory.monad()) = WriterT(MF.pure(MM.empty() toT a), MF)

        inline fun <reified F, W, A> both(w: W, a: A, MF: Monad<F> = kategory.monad()) = WriterT(MF.pure(w toT a), MF)

        inline fun <reified F, W, A> fromTuple(z: Tuple2<W, A>, MF: Monad<F> = kategory.monad()) = WriterT(MF.pure(z), MF)

        inline operator fun <reified F, W, A> invoke(value: HK<F, Tuple2<W, A>>, MF: Monad<F> = kategory.monad()) = WriterT(value)

        inline fun <reified F, reified W> functor(FF: Functor<F> = kategory.functor<F>()): WriterTFunctor<F, W> =
                object : WriterTFunctor<F, W> {
                    override fun F0(): Functor<F> = FF
                }

        inline fun <reified F, reified W> applicative(AP: Applicative<F> = kategory.applicative<F>(),
                                                      MW: Monoid<W> = kategory.monoid<W>()): WriterTApplicative<F, W> =
                object : WriterTApplicative<F, W> {
                    override fun F0(): Applicative<F> = AP
                    override fun L0(): Monoid<W> = MW
                }

        inline fun <reified F, reified W> monad(MF: Monad<F> = kategory.monad<F>(), MW: Monoid<W> = kategory.monoid<W>()): WriterTMonad<F, W> =
                object : WriterTMonadWriter<F, W> {
                    override fun F0(): Monad<F> = MF
                    override fun L0(): Monoid<W> = MW
                }

        inline fun <reified F, reified W> semigroupK(MF: Monad<F> = monad<F>(), MKF: SemigroupK<F> = semigroupK<F>()): WriterTSemigroupK<F, W> =
                object : WriterTSemigroupK<F, W> {
                    override fun MF(): Monad<F> = MF
                    override fun F0(): SemigroupK<F> = MKF
                }

        inline fun <reified F, reified W> monoidK(MF: Monad<F> = monad<F>(), MKF: MonoidK<F> = monoidK<F>()): WriterTMonoidK<F, W> =
                object : WriterTMonoidK<F, W> {
                    override fun MF(): Monad<F> = MF
                    override fun F0(): MonoidK<F> = MKF
                }

        inline fun <reified F, reified W> monadWriter(MF: Monad<F> = kategory.monad(),
                                                      MW: Monoid<W> = kategory.monoid()): WriterTMonadWriter<F, W> =
                object : WriterTMonadWriter<F, W> {
                    override fun F0(): Monad<F> = MF
                    override fun L0(): Monoid<W> = MW
                }

        inline fun <reified F, reified W> monadFilter(MF: MonadFilter<F> = kategory.monadFilter(),
                                                      MW: Monoid<W> = kategory.monoid()): WriterTMonadFilter<F, W> =
                object : WriterTMonadFilter<F, W> {
                    override fun F0(): MonadFilter<F> = MF
                    override fun L0(): Monoid<W> = MW
                }

        inline fun <reified F, W, A> putT(vf: HK<F, A>, w: W, MF: Monad<F> = kategory.monad()): WriterT<F, W, A> =
                WriterT(MF.map(vf, { v -> Tuple2(w, v) }))

        inline fun <reified F, W, A> put(a: A, w: W, applicativeF: Applicative<F> = kategory.applicative()): WriterT<F, W, A> =
                WriterT.putT(applicativeF.pure(a), w)

        fun <F, W, A> putT2(vf: HK<F, A>, w: W, MF: Monad<F>): WriterT<F, W, A> =
                WriterT(MF.map(vf, { v -> Tuple2(w, v) }))

        fun <F, W, A> put2(a: A, w: W, MF: Monad<F>): WriterT<F, W, A> =
                WriterT.putT2(MF.pure(a), w, MF)

        inline fun <reified F, W> tell(l: W, applicativeF: Applicative<F> = kategory.applicative()): WriterT<F, W, Unit> = WriterT.put(Unit, l)

        fun <F, W> tell2(l: W, MF: Monad<F>): WriterT<F, W, Unit> = WriterT.put2(Unit, l, MF)

        inline fun <reified F, reified W, A> value(v: A, applicativeF: Applicative<F> = kategory.applicative(), monoidW: Monoid<W> = monoid()):
                WriterT<F, W, A> = WriterT.put(v, monoidW.empty())

        inline fun <reified F, reified W, A> valueT(vf: HK<F, A>, functorF: Functor<F> = kategory.functor(), monoidW: Monoid<W> = monoid()): WriterT<F, W, A> =
                WriterT.putT(vf, monoidW.empty())
    }
}

inline fun <reified F, W, A> WriterT<F, W, A>.tell(w: W, SG: Semigroup<W>, FF: Functor<F> = functor()): WriterT<F, W, A> = mapWritten { SG.combine(it, w) }

inline fun <reified F, W, A> WriterT<F, W, A>.written(FF: Functor<F> = functor()): HK<F, W> = FF.map(value, { it.a })

inline fun <reified F, W, A> WriterT<F, W, A>.value(FF: Functor<F> = functor()): HK<F, A> = FF.map(value, { it.b })

inline fun <reified F, reified W, A> WriterT<F, W, A>.reset(MM: Monoid<W> = monoid(), FF: Functor<F>): WriterT<F, W, A> = mapWritten { MM.empty() }

inline fun <reified F, W, A, B> WriterT<F, W, A>.map(crossinline f: (A) -> B, FF: Functor<F> = functor()): WriterT<F, W, B> =
        WriterT(FF.map(value, { it.a toT f(it.b) }))

inline fun <reified F, W, A, U> WriterT<F, W, A>.mapWritten(FF: Functor<F> = functor(), crossinline f: (W) -> U): WriterT<F, U, A> =
        mapBoth { f(it.a) toT it.b }

inline fun <reified F, W, A, C, U> WriterT<F, W, A>.bimap(FF: Functor<F> = functor(), crossinline g: (W) -> U, crossinline f: (A) -> C): WriterT<F, U, C> =
        mapBoth { g(it.a) toT f(it.b) }

inline fun <reified F, W, A> WriterT<F, W, A>.swap(FF: Functor<F> = functor()): WriterT<F, A, W> = mapBoth { it.b toT it.a }

inline fun <reified F, W, A, B> WriterT<F, W, A>.flatMap(SG: Semigroup<W>, MF: Monad<F> = monad(), crossinline f: (A) -> WriterT<F, W, B>): WriterT<F, W, B> =
        WriterT(MF.flatMap(value, { value -> MF.map(f(value.b).value, { SG.combine(it.a, value.a) toT it.b }) }))

inline fun <reified F, W, A, B, U> WriterT<F, W, A>.mapBoth(FF: Functor<F> = functor(), noinline f: (Tuple2<W, A>) -> Tuple2<U, B>): WriterT<F, U, B> =
        WriterT(FF.map(value, f))

inline fun <reified F, W, A, B> WriterT<F, W, A>.liftF(MF: Monad<F> = monad(), fa: HK<F, B>): WriterT<F, W, B> =
        WriterT(MF.map2(fa, value, { it.b.a toT it.a }))

inline fun <reified F, W, A, C> WriterT<F, W, A>.semiflatMap(MF: Monad<F> = monad(), crossinline f: (A) -> HK<F, C>, SG: Semigroup<W>): WriterT<F, W, C> =
        flatMap(SG, MF, { liftF(MF, f(it)) })

inline fun <reified F, W, A, B> WriterT<F, W, A>.subflatMap(FF: Functor<F> = functor(), crossinline f: (A) -> Tuple2<W, B>): WriterT<F, W, B> =
        mapBoth(FF, { f(it.b) })
