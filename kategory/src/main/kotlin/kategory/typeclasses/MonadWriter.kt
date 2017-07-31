package kategory

/** A monad that support monoidal accumulation (e.g. logging List<String>) */
interface MonadWriter<F, W> : Monad<F> {

    /** Lift a writer action into the effect */
    fun <A> writer(aw: Tuple2<W, A>): HK<F, A>

    /** Run the effect and pair the accumulator with the result */
    fun <A> listen(fa: HK<F, A>): HK<F, Tuple2<W, A>>

    /** Apply the effectful function to the accumulator */
    fun <A> pass(fa: HK<F, Tuple2<(W) -> W, A>>): HK<F, A>

    /** Lift the log into the effect */
    fun tell(w: W): HK<F, Unit> = writer(Tuple2(w, Unit))

    /** Pair the value with an inspection of the accumulator */
    fun <A, B> listens(fa: HK<F, A>, f: (W) -> B): HK<F, Tuple2<B, A>> =
            map(listen(fa)) { Tuple2(f(it.a), it.b) }

    /** Modify the accumulator */
    fun <A> censor(fa: HK<F, A>, f: (W) -> W): HK<F, A> =
            flatMap(listen(fa)) { writer(Tuple2(f(it.a), it.b)) }

    companion object {

        inline fun <reified F, reified W> invoke(MWF: MonadWriter<F, W> = monadWriter<F, W>()) = MWF
    }
}

inline fun <reified F, reified W> monadWriter(): MonadWriter<F, W> =
        instance(InstanceParametrizedType(MonadWriter::class.java, listOf(F::class.java, W::class.java)))
