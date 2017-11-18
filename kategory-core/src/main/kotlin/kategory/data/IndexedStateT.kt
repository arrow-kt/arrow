package kategory

/**
 * Alias that represent stateful computation of the form `(SA) -> Tuple2<SB, A>` with a result in certain context `F`.
 */
typealias IndexedStateTFun<F, SA, SB, A> = (SA) -> HK<F, Tuple2<SB, A>>

/**
 * Alias that represents wrapped stateful computation in context `F`.
 */
typealias IndexedStateTFunKind<F, SA, SB, A> = HK<F, IndexedStateTFun<F, SA, SB, A>>

/**
 * Run the stateful computation within the context `F`.
 *
 * @param MF [Monad] for the context [F]
 * @param initial state to run stateful computation
 */
fun <F, SA, SB, A> IndexedStateTKind<F, SA, SB, A>.runM(MF: Monad<F>, initial: SA): HK<F, Tuple2<SB, A>> = (this as IndexedStateT<F, SA, SB, A>).run(MF, initial)

/**
 * Run the stateful computation within the context `F`.
 *
 * @param initial state to run stateful computation
 * @param MF [Monad] for the context [F]
 */
inline fun <reified F, SA, SB, A> IndexedStateTKind<F, SA, SB, A>.runM(initial: SA, MF: Monad<F> = monad()): HK<F, Tuple2<SB, A>> = (this as IndexedStateT<F, SA, SB, A>).run(MF, initial)

/**
 * `IndexedStateT<F, SA, SB, A>` is a stateful computation from SA to SB within a context `F` yielding  a value of type `A`.
 * i.e. IndexedStateT<EitherPartialKind<E>, SA, SB, A> = Either<E, IndexedState<SA, SB, A>>
 *
 * @param F the context that wraps the stateful computation.
 * @param SA the state we are preforming computation upon.
 * @param SB the resulting state of the computation
 * @param A current value of computation.
 * @param runF the stateful computation that is wrapped and managed by `IndexedStateT`
 */
@higherkind
class IndexedStateT<F, SA, SB, A>(
        val runF: HK<F, (SA) -> HK<F, Tuple2<SB, A>>>
) : IndexedStateTKind<F, SA, SB, A> {

    companion object {

        /**
         * Constructor to create `IndexedStateT<F, S, A>` given a [StateTFun].
         *
         * @param MF [Monad] for the context [F].
         * @param run the stateful function to wrap with [IndexedStateT].
         */
        operator fun <F, SA, SB, A> invoke(AF: Applicative<F>, run: IndexedStateTFun<F, SA, SB, A>): IndexedStateT<F, SA, SB, A> = IndexedStateT(AF.pure(run))

        /**
         * Alias for constructor [IndexedStateT].
         *
         * @param runF the function to wrap within [IndexedStateT].
         */
        fun <F, SA, SB, A> invokeF(runF: IndexedStateTFunKind<F, SA, SB, A>): IndexedStateT<F, SA, SB, A> = IndexedStateT(runF)

        /**
         * Lift a value of type `A` into `IndexedStateT<F, S, S, A>`.
         *
         * @param MF [Monad] for the context [F].
         * @param fa the value to lift.
         */
        fun <F, S, A> lift(MF: Applicative<F>, fa: HK<F, A>): IndexedStateT<F, S, S, A> = IndexedStateT(MF.pure({ s -> MF.map(fa, { a -> Tuple2(s, a) }) }))

        /**
         * Return input without modifying it.
         *
         * @param AF [Applicative] for the context [F].
         */
        fun <F, S> get(AF: Applicative<F>): IndexedStateT<F, S, S, S> = IndexedStateT(AF.pure({ s -> AF.pure(Tuple2(s, s)) }))

        /**
         * Inspect a value of the state [S] with [f] `(S) -> B` without modifying the state.
         *
         * @param AF [Applicative] for the context [F].
         * @param f the function applied to extract [B] from [S].
         */
        inline fun <F, S, B> inspect(AF: Applicative<F>, crossinline f: (S) -> B): IndexedStateT<F, S, S, B> = IndexedStateT(AF.pure({ s -> AF.pure(Tuple2(s, f(s))) }))

        /**
         * Inspect a value of the state [S] with [f] `(S) -> HK<F, B>` without modifying the state.
         *
         * @param AF [Applicative] for the context [F].
         * @param f the function applied to extract [B] in context of [F] from [S].
         */
        inline fun <F, S, B> inspectF(AF: Applicative<F>, crossinline f: (S) -> HK<F, B>): IndexedStateT<F, S, S, B> = IndexedStateT(AF.pure({ s -> AF.map(f(s)) { Tuple2(s, it) } }))

        /**
         * Modify the state with [f] `(S) -> S` and return [Unit].
         *
         * @param AF [Applicative] for the context [F].
         * @param f the modify function to apply.
         */
        inline fun <F, S> modify(AF: Applicative<F>, crossinline f: (S) -> S): IndexedStateT<F, S, S, Unit> = IndexedStateT(AF.pure({ s -> AF.map(AF.pure(f(s))) { it toT Unit } }))

        /**
         * Modify the state with an [Applicative] function [f] `(S) -> HK<F, S>` and return [Unit].
         *
         * @param AF [Applicative] for the context [F].
         * @param f the modify function to apply.
         */
        inline fun <F, S> modifyF(AF: Applicative<F>, crossinline f: (S) -> HK<F, S>): IndexedStateT<F, S, S, Unit> = IndexedStateT(AF.pure({ s -> AF.map(f(s)) { it toT Unit } }))

        /**
         * Set the state to a value [s] and return [Unit].
         *
         * @param AF [Applicative] for the context [F].
         * @param s value to set.
         */
        fun <F, SA, SB> set(AF: Applicative<F>, s: SB): IndexedStateT<F, SA, SB, Unit> = IndexedStateT(AF.pure({ _ -> AF.pure(Tuple2(s, Unit)) }))

        /**
         * Set the state to a value [s] of type `HK<F, S>` and return [Unit].
         *
         * @param AF [Applicative] for the context [F].
         * @param s value to set.
         */
        fun <F, SA, SB> setF(AF: Applicative<F>, s: HK<F, SB>): IndexedStateT<F, SA, SB, Unit> = IndexedStateT(AF.pure({ _ -> AF.map(s) { Tuple2(it, Unit) } }))

        /**
         * Construct a [IndexedStateT] with a current value [a].
         *
         * @param AF [Applicative] for the context [F].
         * @param a current value of the state.
         */
        fun <F, S, A> pure(AF: Applicative<F>, a: A): IndexedStateT<F, S, S, A> = IndexedStateT(AF.pure({ s: S -> AF.pure(Tuple2(s, a)) }))

        /**
         * Tail recursive function that keeps calling [f]  until [kategory.Either.Right] is returned.
         *
         * @param a initial value to start running recursive call to [f]
         * @param f function that is called recusively until [kategory.Either.Right] is returned.
         * @param MF [Monad] for the context [F].
         */
        inline fun <F, S, A, B> tailRecM(a: A, crossinline f: (A) -> HK<IndexedStateTKindPartial<F, S, S>, Either<A, B>>, MF: Monad<F>): IndexedStateT<F, S, S, B> =
                IndexedStateT(MF, run = { s ->
                    MF.tailRecM(Tuple2(s, a)) { (s, a) ->
                        MF.map(f(a).ev().run(MF, s)) { (s, ab) ->
                            ab.bimap({ s toT it }, { s toT it })
                        }
                    }
                })

    }

    /**
     * Map current value [A] to [B] given a function [f].
     *
     * @param f the function to apply.
     * @param FF [Functor] for the context [F].
     */
    inline fun <B> map(FF: Functor<F>, crossinline f: (A) -> B): IndexedStateT<F, SA, SB, B> = transform(FF, { (s, a) -> Tuple2(s, f(a)) })

    /**
     * Combine with another [IndexedStateT] of same context [F] and state [S].
     *
     * @param sb other state with value of type `B`.
     * @param f the function to apply.
     * @param MF [Monad] for the context [F].
     */
    inline fun <B, SC, Z> map2(MF: Monad<F>, sb: IndexedStateTKind<F, SB, SC, B>, crossinline fn: (A, B) -> Z): IndexedStateT<F, SA, SC, Z> =
            invokeF(MF.map2(runF, sb.ev().runF) { (ssa, ssb) ->
                ssa.andThen { fsa ->
                    MF.flatMap(fsa) { (s, a) ->
                        MF.map(ssb(s)) { (s, b) -> Tuple2(s, fn(a, b)) }
                    }
                }
            })

    /**
     * Controlled combination of [IndexedStateT] that is of same context [F] and state [S] using [Eval].
     *
     * @param sb other state with value of type `B`.
     * @param f the function to apply.
     * @param MF [Monad] for the context [F].
     */
    inline fun <B, SC, Z> map2Eval(sb: Eval<IndexedStateTKind<F, SB, SC, B>>, crossinline fn: (A, B) -> Z, MF: Monad<F>): Eval<IndexedStateT<F, SA, SC, Z>> =
            MF.map2Eval(runF, sb.map { it.ev().runF }) { (ssa, ssb) ->
                ssa.andThen { fsa ->
                    MF.flatMap(fsa) { (s, a) ->
                        MF.map(ssb((s))) { (s, b) -> Tuple2(s, fn(a, b)) }
                    }
                }
            }.map { IndexedStateT.invokeF(it) }

    /**
     * Apply a function `(S) -> B` that operates within the [IndexedStateT] context.
     *
     * @param ff function with the [IndexedStateT] context.
     * @param MF [Monad] for the context [F].
     */
    fun <B, SC> ap(MF: Monad<F>, ff: IndexedStateTKind<F, SB, SC, (A) -> B>): IndexedStateT<F, SA, SC, B> =
            map2(MF, ff) { a, f -> f(a) }

    /**
     * Create a product of the value types of [IndexedStateT].
     *
     * @param sb other stateful computation.
     * @param MF [Monad] for the context [F].
     */
    fun <B, SC> product(MF: Monad<F>, sb: IndexedStateTKind<F, SB, SC, B>): IndexedStateT<F, SA, SC, Tuple2<A, B>> = map2(MF, sb.ev(), { a, b -> Tuple2(a, b) })

    /**
     * Map the value [A] to another [IndexedStateT] object for the same state [S] and context [F] and flatten the structure.
     *
     * @param fas the function to apply.
     * @param MF [Monad] for the context [F].
     */
    inline fun <B, SC> flatMap(MF: Monad<F>, crossinline fas: (A) -> IndexedStateTKind<F, SB, SC, B>): IndexedStateT<F, SA, SC, B> =
            invokeF(
                    MF.map(runF) { safsba ->
                        safsba.andThen { fsba ->
                            MF.flatMap(fsba) {
                                fas(it.b).runM(MF, it.a)
                            }
                        }
                    })

    /**
     * Map the value [A] to a arbitrary type [B] that is within the context of [F].
     *
     * @param faf the function to apply.
     * @param MF [Monad] for the context [F].
     */
    inline fun <B> flatMapF(MF: Monad<F>, crossinline faf: (A) -> HK<F, B>): IndexedStateT<F, SA, SB, B> =
            invokeF(MF.map(runF) { sfsa ->
                sfsa.andThen { fsa ->
                    MF.flatMap(fsa) { (s, a) ->
                        MF.map(faf(a)) {
                            s toT it
                        }
                    }
                }
            })

    /**
     * Change state to [S0] given that you provide a function that proofs you can transform [S0] back into [SA].
     *
     * @param FF [Functor] for the context [F].
     * @param f function that proofs that you can transform [S0] back into [SA].
     */
    inline fun <S0> contramap(FF: Functor<F>, crossinline f: (S0) -> SA): IndexedStateT<F, S0, SB, A> =
            IndexedStateT.invokeF(FF.map(runF) { sfsa ->
                { s0: S0 -> sfsa(f(s0)) }
            })

    /**
     * [Contramap] and [leftMap].
     *
     * @param FF [Functor] for the context [F].
     * @param f function that proofs that you can transform [S0] back into [SA].
     * @param g function to map resulting state [SB] to [SC].
     */
    inline fun <S0, SC> dimap(FF: Functor<F>, crossinline f: (S0) -> SA, crossinline g: (SB) -> SC): IndexedStateT<F, S0, SC, A> =
            contramap(FF, f).modify(FF, g)

    /**
     * Map both the value [A] and the resulting state [SB].
     *
     * @param FF [Functor] for the context [F].
     * @param f function to map resulting state [SB] to [SC].
     * @param g function to map value [A] to [B].
     */
    inline fun <B, SC> bimap(crossinline f: (SB) -> SC, crossinline g: (A) -> B, FF: Functor<F>): IndexedStateT<F, SA, SC, B> =
            transform(FF, { (sb, a) -> f(sb) toT g(a) })

    /**
     * Map the resulting state [SB] to a new state [SC].
     *
     * @param FF [Functor] for the context [F].
     * @param f function to map resulting state [SB] to [SC].
     */
    inline fun <SC> mapLeft(FF: Functor<F>, crossinline f: (SB) -> SC): IndexedStateT<F, SA, SC, A> = bimap(f, ::identity, FF)

    /**
     * Transform the product of state [S] and value [A] to an another product of state [S] and an arbitrary type [B].
     *
     * @param f the function to apply.
     * @param FF [Functor] for the context [F].
     */
    inline fun <B, SC> transform(FF: Functor<F>, crossinline f: (Tuple2<SB, A>) -> Tuple2<SC, B>): IndexedStateT<F, SA, SC, B> =
            invokeF(FF.map(runF) { sfsa ->
                sfsa.andThen { fsa ->
                    FF.map(fsa) { (s, a) -> f(s toT a) }
                }
            })

    /**
     * Like [transform], but allows the context to change from [F] to [G].
     *
     * @param MF [Monad] for the context [F].
     * @param AG [Applicative] for the context [F].
     * @param f function to transform state within context [F] to state in context [G].
     */
    inline fun <G, B, SC> transformF(MF: Monad<F>, AG: Applicative<G>, crossinline f: (HK<F, Tuple2<SB, A>>) -> HK<G, Tuple2<SC, B>>): IndexedStateT<G, SA, SC, B> = IndexedStateT(AG, run = { s ->
        f(run(MF, s))
    })

    /**
     * Transform the state used to an arbitrary type [R].
     *
     * @param FF [Functor] for the context [F].
     * @param f function that can extract state [SA] from [R].
     * @param g function that can calculate new state [R].
     */
    inline fun <R> transformS(FF: Functor<F>, crossinline f: (R) -> SA, crossinline g: (Tuple2<R, SB>) -> R): IndexedStateT<F, R, R, A> = IndexedStateT.invokeF(FF.map(runF) { sfsa ->
        { r: R ->
            val sa = f(r)
            val fsba = sfsa(sa)
            FF.map(fsba) { (sb, a) ->
                g(Tuple2(r, sb)) toT a
            }
        }
    })

    /**
     * Modify the state [SB].
     *
     * @param FF [Functor] for the context [F].
     * @param f function to modify state [SB] to [SC].
     */
    inline fun <SC> modify(FF: Functor<F>, crossinline f: (SB) -> SC): IndexedStateT<F, SA, SC, A> = transform(FF) { (sb, a) ->
        Tuple2(f(sb), a)
    }

    /**
     * Inspect a value from the input state, without modifying the state.
     *
     * @param FF [Functor] for the context [F].
     * @param f function to inspect value from the state [SB].
     */
    inline fun <B> inspect(FF: Functor<F>, crossinline f: (SB) -> B): IndexedStateT<F, SA, SB, B> = transform(FF) { (sb, _) ->
        Tuple2(sb, f(sb))
    }

    /**
     * Get the input state, without modifying the state.
     *
     * @param FF [Functor] for the context [F].
     */
    fun get(FF: Functor<F>): IndexedStateT<F, SA, SB, SB> = inspect(FF, ::identity)

    /**
     * Combine two [IndexedStateT] objects using an instance of [SemigroupK] for [F].
     *
     * @param MF [Monad] for the context [F].
     * @param SF [SemigroupK] for [F].
     * @param y other [IndexedStateT] object to combine.
     */
    fun combineK(MF: Monad<F>, SF: SemigroupK<F>, y: IndexedStateTKind<F, SA, SB, A>): IndexedStateT<F, SA, SB, A> =
            IndexedStateT(MF.pure({ s -> SF.combineK(run(MF, s), y.ev().run(MF, s)) }))

    /**
     * Run the stateful computation within the context `F`.
     *
     * @param MF [Monad] for the context [F].
     * @param s initial state to run stateful computation.
     */
    fun run(MF: Monad<F>, initial: SA): HK<F, Tuple2<SB, A>> = MF.flatMap(runF) { f -> f(initial) }

    /**
     * Run the stateful computation within the context `F` and get the value [A].
     *
     * @param s initial state to run stateful computation.
     * @param MF [Monad] for the context [F].
     */
    fun runA(MF: Monad<F>, s: SA): HK<F, A> = MF.map(run(MF, s)) { it.b }

    /**
     * Run the stateful computation within the context `F` and get the state [S].
     *
     * @param s initial state to run stateful computation.
     * @param MF [Monad] for the context [F].
     */
    fun runS(MF: Monad<F>, s: SA): HK<F, SB> = MF.map(run(MF, s)) { it.a }

}

/**
 * Wrap the function with [IndexedStateT].
 *
 * @param MF [Monad] for the context [F].
 */
inline fun <reified F, SA, SB, A> IndexedStateTFunKind<F, SA, SB, A>.toIndexedStateT(): IndexedStateT<F, SA, SB, A> =
        IndexedStateT(this)

/**
 * Wrap the function with [IndexedStateT].
 *
 * @param AF [Applicative] for the context [F].
 */
inline fun <reified F, SA, SB, A> IndexedStateTFun<F, SA, SB, A>.toIndexedStateT(AF: Applicative<F> = applicative()): IndexedStateT<F, SA, SB, A> =
        IndexedStateT(this, AF)

/**
 * Constructor to create `IndexedStateT<F, SA, SB, A>` given a [IndexedStateTFun].
 *
 * @param run the stateful function to wrap with [IndexedStateT].
 * @param AF [Applicative] for the context [F].
 */
inline operator fun <reified F, SA, SB, A> IndexedStateT.Companion.invoke(noinline run: IndexedStateTFun<F, SA, SB, A>, AF: Applicative<F> = applicative<F>()): IndexedStateT<F, SA, SB, A> =
        IndexedStateT(AF.pure(run))

/**
 * Lift a value of type `A` into `IndexedStateT<F, S, S, A>`.
 *
 * @param AF [Applicative] for the context [F].
 * @param fa the value to lift.
 */
inline fun <reified F, S, A> IndexedStateT.Companion.lift(fa: HK<F, A>, AF: Applicative<F> = applicative<F>()): IndexedStateT<F, S, S, A> =
        IndexedStateT.lift(AF, fa)

/**
 * Return input without modifying it.
 *
 * @param AF [Applicative] for the context [F].
 */
inline fun <reified F, S> IndexedStateT.Companion.get(AF: Applicative<F> = applicative<F>(), dummy: Unit = Unit): IndexedStateT<F, S, S, S> =
        IndexedStateT.get(AF)

/**
 * Inspect a value of the state [S] with [f] `(S) -> T` without modifying the state.
 *
 * @param AF [Applicative] for the context [F].
 * @param f the function applied to extrat [T] from [S].
 */
inline fun <reified F, S, T> IndexedStateT.Companion.inspect(AF: Applicative<F> = applicative<F>(), dummy: Unit = Unit, crossinline f: (S) -> T): IndexedStateT<F, S, S, T> =
        IndexedStateT.inspect(AF) { f(it) }

/**
 * Set the state to a value [s] and return [Unit].
 *
 * @param AF [Applicative] for the context [F].
 * @param s value to set.
 */
inline fun <reified F, SA, SB> IndexedStateT.Companion.set(s: SB, AF: Applicative<F> = applicative<F>()): IndexedStateT<F, SA, SB, Unit> =
        IndexedStateT.set(AF, s)

/**
 * Set the state to a value [s] of type `HK<F, S>` and return [Unit].
 *
 * @param AF [Applicative] for the context [F].
 * @param s value to set.
 */
inline fun <reified F, S> IndexedStateT.Companion.setF(s: HK<F, S>, AF: Applicative<F> = applicative<F>()): IndexedStateT<F, S, S, Unit> =
        IndexedStateT.setF(AF, s)

/**
 * Modify the state with [f] `(S) -> S` and return [Unit].
 *
 * @param AF [Applicative] for the context [F].
 * @param f the modify function to apply.
 */
inline fun <reified F, S> IndexedStateT.Companion.modify(AF: Applicative<F> = applicative<F>(), dummy: Unit = Unit, crossinline f: (S) -> S): IndexedStateT<F, S, S, Unit> =
        IndexedStateT.modify(AF) { f(it) }

/**
 * Modify the state with an [Applicative] function [f] `(S) -> HK<F, S>` and return [Unit].
 *
 * @param AF [Applicative] for the context [F].
 * @param f the modify function to apply.
 */
inline fun <reified F, S> IndexedStateT.Companion.modifyF(AF: Applicative<F> = applicative<F>(), dummy: Unit = Unit, crossinline f: (S) -> HK<F, S>): IndexedStateT<F, S, S, Unit> =
        IndexedStateT.modifyF(AF) { f(it) }

/**
 * Map current value [A] to [B] given a function [f].
 *
 * @param f the function to apply.
 * @param FF [Functor] for the context [F].
 */
inline fun <reified F, SA, SB, A, B> IndexedStateT<F, SA, SB, A>.map(FF: Functor<F> = functor(), dummy: Unit = Unit, crossinline f: (A) -> B): IndexedStateT<F, SA, SB, B> =
        map(FF) { f(it) }

/**
 * Combine with another [IndexedStateT] of same context [F] and state [S].
 *
 * @param sb other state with value of type `B`.
 * @param MF [Monad] for the context [F].
 * @param f the function to apply.
 */
inline fun <reified F, SA, SB, A, B, SC, Z> IndexedStateT<F, SA, SB, A>.map2(sb: IndexedStateTKind<F, SB, SC, B>, MF: Monad<F> = monad(), crossinline fn: (A, B) -> Z): IndexedStateT<F, SA, SC, Z> =
        map2(MF, sb, fn)

/**
 * Apply a function `(S) -> B` that operates within the [IndexedStateT] context.
 *
 * @param ff function with the [IndexedStateT] context.
 * @param MF [Monad] for the context [F].
 */
inline fun <reified F, SA, SB, A, B, SC> IndexedStateT<F, SA, SB, A>.ap(ff: IndexedStateTKind<F, SB, SC, (A) -> B>, MF: Monad<F> = monad()): IndexedStateT<F, SA, SC, B> =
        ap(MF, ff)

/**
 * Create a product of the value types of [IndexedStateT].
 *
 * @param sb other stateful computation.
 * @param MF [Monad] for the context [F].
 */
inline fun <reified F, SA, SB, A, B, SC> IndexedStateT<F, SA, SB, A>.product(sb: IndexedStateTKind<F, SB, SC, B>, MF: Monad<F> = monad()): IndexedStateT<F, SA, SC, Tuple2<A, B>> =
        product(MF, sb)

/**
 * Map the value [A] to another [IndexedStateT] object for the same state [S] and context [F] and flatten the structure.
 *
 * @param fas the function to apply.
 * @param MF [Monad] for the context [F].
 */
inline fun <reified F, SA, SB, A, B, SC> IndexedStateT<F, SA, SB, A>.flatMap(MF: Monad<F> = monad(), dummy: Unit = Unit, crossinline fas: (A) -> IndexedStateTKind<F, SB, SC, B>): IndexedStateT<F, SA, SC, B> =
        flatMap(MF, fas)

/**
 * Map the value [A] to a arbitrary type [B] that is within the context of [F].
 *
 * @param faf the function to apply.
 * @param MF [Monad] for the context [F].
 */
inline fun <reified F, SA, SB, A, B> IndexedStateT<F, SA, SB, A>.flatMapF(MF: Monad<F> = monad(), dummy: Unit = Unit, crossinline faf: (A) -> HK<F, B>): IndexedStateT<F, SA, SB, B> =
        flatMapF(MF, faf)

/**
 * Change state to [S0] given that you provide a function that proofs you can transform [S0] back into [SA].
 *
 * @param FF [Functor] for the context [F].
 * @param f function that proofs that you can transform [S0] back into [SA].
 */
inline fun <reified F, SA, SB, A, S0> IndexedStateT<F, SA, SB, A>.contramap(FF: Functor<F> = functor(), dummy: Unit = Unit, crossinline f: (S0) -> SA): IndexedStateT<F, S0, SB, A> =
        contramap(FF, f)

/**
 * [Contramap] and [leftMap].
 *
 * @param FF [Functor] for the context [F].
 * @param f function that proofs that you can transform [S0] back into [SA].
 * @param g function to map resulting state [SB] to [SC].
 */
inline fun <reified F, SA, SB, A, S0, SC> IndexedStateT<F, SA, SB, A>.dimap(crossinline f: (S0) -> SA, crossinline g: (SB) -> SC, FF: Functor<F> = functor()): IndexedStateT<F, S0, SC, A> =
        dimap(FF, f, g)

/**
 * Bimap the value [A] and the state [SB].
 *
 * @param FF [Functor] for the context [F].
 * @param f function to map state [SB] to [SC].
 * @param g function to map value [A] to [B].
 */
inline fun <reified F, SA, SB, A, B, SC> IndexedStateT<F, SA, SB, A>.bimap(FF: Functor<F> = functor(), crossinline f: (SB) -> SC, crossinline g: (A) -> B): IndexedStateT<F, SA, SC, B> =
        bimap(f, g, FF)

/**
 * Map the resulting state [SB] to a new state [SC].
 *
 * @param FF [Functor] for the context [F].
 * @param f function to map resulting state [SB] to [SC].
 */
inline fun <reified F, SA, SB, A, SC> IndexedStateT<F, SA, SB, A>.mapLeft(FF: Functor<F> = functor(), dummy: Unit = Unit, crossinline f: (SB) -> SC): IndexedStateT<F, SA, SC, A> =
        mapLeft(FF, f)

/**
 * Transform the product of state [S] and value [A] to an another product of state [S] and an arbitrary type [B].
 *
 * @param f the function to apply.
 * @param FF [Functor] for the context [F].
 */
inline fun <reified F, SA, SB, A, B, SC> IndexedStateT<F, SA, SB, A>.transform(FF: Functor<F> = functor(), dummy: Unit = Unit, noinline f: (Tuple2<SB, A>) -> Tuple2<SC, B>): IndexedStateT<F, SA, SC, B> =
        transform(FF, f)

/**
 * Like [transform], but allows the context to change from [F] to [G].
 *
 * @param MF [Monad] for the context [F].
 * @param AG [Applicative] for the context [F].
 * @param f function to transform state within context [F] to state in context [G].
 */
inline fun <reified F, reified G, SA, SB, A, B, SC> IndexedStateT<F, SA, SB, A>.transformF(MF: Monad<F> = monad(), AG: Applicative<G> = applicative(), dummy: Unit = Unit, noinline f: (HK<F, Tuple2<SB, A>>) -> HK<G, Tuple2<SC, B>>): IndexedStateT<G, SA, SC, B> =
        transformF(MF, AG, f)

/**
 * Transform the state used to an arbitrary type [R].
 *
 * @param FF [Functor] for the context [F].
 * @param f function that can extract state [SA] from [R].
 * @param g function that can calculate new state [R].
 */
inline fun <reified F, SA, SB, A, R> IndexedStateT<F, SA, SB, A>.transformS(FF: Functor<F> = functor(), dummy: Unit = Unit, noinline f: (R) -> SA, noinline g: (Tuple2<R, SB>) -> R): IndexedStateT<F, R, R, A> =
        transformS(FF, f, g)

/**
 * Modify the state [SB].
 *
 * @param FF [Functor] for the context [F].
 * @param f function to modify state [SB] to [SC].
 */
inline fun <reified F, SA, SB, A, SC> IndexedStateT<F, SA, SB, A>.modify(FF: Functor<F> = functor(), dummy: Unit = Unit, noinline f: (SB) -> SC): IndexedStateT<F, SA, SC, A> =
        modify(FF, f)

/**
 * Inspect a value from the input state, without modifying the state.
 *
 * @param FF [Functor] for the context [F].
 * @param f function to inspect value from the state [SB].
 */
inline fun <reified F, SA, SB, A, B> IndexedStateT<F, SA, SB, A>.inspect(FF: Functor<F> = functor(), dummy: Unit = Unit, noinline f: (SB) -> B): IndexedStateT<F, SA, SB, B> =
        inspect(FF, f)

/**
 * Get the input state, without modifying the state.
 *
 * @param FF [Functor] for the context [F].
 */
inline fun <reified F, SA, SB, A> IndexedStateT<F, SA, SB, A>.get(FF: Functor<F> = functor(), dummy: Unit = Unit): IndexedStateT<F, SA, SB, SB> =
        inspect(FF, ::identity)

/**
 * Combine two [IndexedStateT] objects using an instance of [SemigroupK] for [F].
 *
 * @param y other [IndexedStateT] object to combine.
 * @param MF [Monad] for the context [F].
 * @param SF [SemigroupK] for [F].
 */
inline fun <reified F, SA, SB, A> IndexedStateT<F, SA, SB, A>.combineK(y: IndexedStateTKind<F, SA, SB, A>, MF: Monad<F> = monad(), SF: SemigroupK<F> = semigroupK()): IndexedStateT<F, SA, SB, A> =
        IndexedStateT(MF.pure({ s -> SF.combineK(run(MF, s), y.ev().run(MF, s)) }))

/**
 * Run the stateful computation within the context `F`.
 *
 * @param MF [Monad] for the context [F].
 * @param s initial state to run stateful computation.
 */
inline fun <reified F, SA, SB, A> IndexedStateT<F, SA, SB, A>.run(initial: SA, MF: Monad<F> = monad()): HK<F, Tuple2<SB, A>> =
        run(MF, initial)

/**
 * Run the stateful computation within the context `F` and get the value [A].
 *
 * @param s initial state to run stateful computation.
 * @param MF [Monad] for the context [F].
 */
inline fun <reified F, SA, SB, A> IndexedStateT<F, SA, SB, A>.runA(s: SA, MF: Monad<F> = monad()): HK<F, A> =
        runA(MF, s)

/**
 * Run the stateful computation within the context `F` and get the state [S].
 *
 * @param s initial state to run stateful computation.
 * @param MF [Monad] for the context [F].
 */
inline fun <reified F, SA, SB, A> IndexedStateT<F, SA, SB, A>.runS(s: SA, MF: Monad<F> = monad()): HK<F, SB> =
        runS(MF, s)
