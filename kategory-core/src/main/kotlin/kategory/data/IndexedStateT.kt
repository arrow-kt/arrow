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
fun <F, SA, SB, A> IndexedStateTKind<F, SA, SB, A>.runM(MF: Monad<F>, initial: SA): HK<F, Tuple2<SB, A>> = (this as IndexedStateT<F, SA, SB, A>).run(initial, MF)

/**
 * Run the stateful computation within the context `F`.
 *
 * @param initial state to run stateful computation
 * @param MF [Monad] for the context [F]
 */
inline fun <reified F, SA, SB, A> IndexedStateTKind<F, SA, SB, A>.runM(initial: SA, MF: Monad<F> = monad()): HK<F, Tuple2<SB, A>> = (this as IndexedStateT<F, SA, SB, A>).run(initial, MF)

/**
 * `StateT<F, S, A>` is a stateful computation within a context `F` yielding  a value of type `A`.
 * i.e. IndexedStateT<EitherPartialKind<E>, SA, SB, A> = Either<E, IndexedState<SA, SB, A>>
 *
 * @param F the context that wraps the stateful computation.
 * @param SA the state we are preforming computation upon.
 * @param SB the resulting state of the computation
 * @param A current value of computation.
 * @param runF the stateful computation that is wrapped and managed by `StateT`
 */
@higherkind
class IndexedStateT<F, SA, SB, A>(
        val runF: HK<F, (SA) -> HK<F, Tuple2<SB, A>>>
) : IndexedStateTKind<F, SA, SB, A> {

    companion object {

        /**
         * Constructor to create `StateT<F, S, A>` given a [StateTFun].
         *
         * @param run the stateful function to wrap with [StateT].
         * @param MF [Monad] for the context [F].
         */
        inline operator fun <reified F, SA, SB, A> invoke(noinline run: IndexedStateTFun<F, SA, SB, A>, MF: Applicative<F> = applicative<F>()): IndexedStateT<F, SA, SB, A> = IndexedStateT(MF.pure(run))

        /**
         * Constructor to create `StateT<F, S, A>` given a [StateTFun].
         *
         * @param MF [Monad] for the context [F].
         * @param run the stateful function to wrap with [StateT].
         */
        operator fun <F, SA, SB, A> invoke(AF: Applicative<F>, run: IndexedStateTFun<F, SA, SB, A>): IndexedStateT<F, SA, SB, A> = IndexedStateT(AF.pure(run))

        /**
         * Alias for constructor [StateT].
         *
         * @param runF the function to wrap within [StateT].
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
         * Inspect a value of the state [S] with [f] `(S) -> T` without modifying the state.
         *
         * @param AF [Applicative] for the context [F].
         * @param f the function applied to extrat [T] from [S].
         */
        fun <F, S, T> inspect(AF: Applicative<F>, f: (S) -> T): IndexedStateT<F, S, S, T> = IndexedStateT(AF.pure({ s -> AF.pure(Tuple2(s, f(s))) }))

        /**
         * Modify the state with [f] `(S) -> S` and return [Unit].
         *
         * @param AF [Applicative] for the context [F].
         * @param f the modify function to apply.
         */
        fun <F, S> modify(AF: Applicative<F>, f: (S) -> S): IndexedStateT<F, S, S, Unit> = IndexedStateT(AF.pure({ s -> AF.map(AF.pure(f(s))) { it toT Unit } }))

        /**
         * Modify the state with an [Applicative] function [f] `(S) -> HK<F, S>` and return [Unit].
         *
         * @param AF [Applicative] for the context [F].
         * @param f the modify function to apply.
         */
        fun <F, S> modifyF(AF: Applicative<F>, f: (S) -> HK<F, S>): IndexedStateT<F, S, S, Unit> = IndexedStateT(AF.pure({ s -> AF.map(f(s)) { it toT Unit } }))

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
        fun <F, S, A, B> tailRecM(a: A, f: (A) -> HK<IndexedStateTKindPartial<F, S, S>, Either<A, B>>, MF: Monad<F>): IndexedStateT<F, S, S, B> =
                IndexedStateT(MF, run = { s ->
                    MF.tailRecM(Tuple2(s, a)) { (s, a) ->
                        MF.map(f(a).ev().run(s, MF)) { (s, ab) ->
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
    fun <B> map(f: (A) -> B, FF: Functor<F>): IndexedStateT<F, SA, SB, B> = transform(FF, { (s, a) -> Tuple2(s, f(a)) })

    /**
     * Combine with another [StateT] of same context [F] and state [S].
     *
     * @param sb other state with value of type `B`.
     * @param f the function to apply.
     * @param MF [Monad] for the context [F].
     */
    fun <B, SC, Z> map2(sb: IndexedStateTKind<F, SB, SC, B>, fn: (A, B) -> Z, MF: Monad<F>): IndexedStateT<F, SA, SC, Z> =
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
    fun <B, SC, Z> map2Eval(sb: Eval<IndexedStateTKind<F, SB, SC, B>>, fn: (A, B) -> Z, MF: Monad<F>): Eval<IndexedStateT<F, SA, SC, Z>> =
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
    fun <B, SC> ap(ff: IndexedStateTKind<F, SB, SC, (A) -> B>, MF: Monad<F>): IndexedStateT<F, SA, SC, B> =
            map2(ff, { a, f -> f(a) }, MF)

    /**
     * Create a product of the value types of [IndexedStateT].
     *
     * @param sb other stateful computation.
     * @param MF [Monad] for the context [F].
     */
    fun <B, SC> product(sb: IndexedStateTKind<F, SB, SC, B>, MF: Monad<F>): IndexedStateT<F, SA, SC, Tuple2<A, B>> = map2(sb.ev(), { a, b -> Tuple2(a, b) }, MF)

    /**
     * Map the value [A] to another [IndexedStateT] object for the same state [S] and context [F] and flatten the structure.
     *
     * @param fas the function to apply.
     * @param MF [Monad] for the context [F].
     */
    fun <B, SC> flatMap(fas: (A) -> IndexedStateTKind<F, SB, SC, B>, MF: Monad<F>): IndexedStateT<F, SA, SC, B> =
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
    fun <B> flatMapF(faf: (A) -> HK<F, B>, MF: Monad<F>): IndexedStateT<F, SA, SB, B> =
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
     * Bimap the value [A] and the state [SB].
     *
     * @param FF [Functor] for the context [F].
     * @param f function to map state [SB] to [SC].
     * @param g function to map value [A] to [B].
     */
    fun <B, SC> bimap(FF: Functor<F>, f: (SB) -> SC, g: (A) -> B): IndexedStateT<F, SA, SC, B> =
            transform(FF, { (sb, a) -> f(sb) toT g(a) })

    /**
     * Transform the product of state [S] and value [A] to an another product of state [S] and an arbitrary type [B].
     *
     * @param f the function to apply.
     * @param FF [Functor] for the context [F].
     */
    fun <B, SC> transform(FF: Functor<F>, f: (Tuple2<SB, A>) -> Tuple2<SC, B>): IndexedStateT<F, SA, SC, B> =
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
    fun <G, B, SC> transformF(MF: Monad<F>, AG: Applicative<G>, f: (HK<F, Tuple2<SB, A>>) -> HK<G, Tuple2<SC, B>>): IndexedStateT<G, SA, SC, B> = IndexedStateT(AG, run = { s ->
        f(run(s, MF))
    })

    /**
     * Transform the state used to an arbitrary type [R].
     *
     * @param FF [Functor] for the context [F].
     * @param f function that can extract state [SA] from [R].
     * @param g function that can calculate new state [R].
     */
    fun <R> transformS(FF: Functor<F>, f: (R) -> SA, g: (Tuple2<R, SB>) -> R): IndexedStateT<F, R, R, A> = IndexedStateT.invokeF(FF.map(runF) { sfsa ->
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
    fun <SC> modify(FF: Functor<F>, f: (SB) -> SC): IndexedStateT<F, SA, SC, A> = transform(FF) { (sb, a) ->
        Tuple2(f(sb), a)
    }

    /**
     * Inspect a value from the input state, without modifying the state.
     *
     * @param FF [Functor] for the context [F].
     * @param f function to inspect value from the state [SB].
     */
    fun <B> inspect(FF: Functor<F>, f: (SB) -> B): IndexedStateT<F, SA, SB, B> = transform(FF) { (sb, _) ->
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
     * @param y other [IndexedStateT] object to combine.
     * @param MF [Monad] for the context [F].
     * @param SF [SemigroupK] for [F].
     */
    fun combineK(y: IndexedStateTKind<F, SA, SB, A>, MF: Monad<F>, SF: SemigroupK<F>): IndexedStateT<F, SA, SB, A> =
            IndexedStateT(MF.pure({ s -> SF.combineK(run(s, MF), y.ev().run(s, MF)) }))

    /**
     * Run the stateful computation within the context `F`.
     *
     * @param s initial state to run stateful computation.
     * @param MF [Monad] for the context [F].
     */
    fun run(initial: SA, MF: Monad<F>): HK<F, Tuple2<SB, A>> = MF.flatMap(runF) { f -> f(initial) }

    /**
     * Run the stateful computation within the context `F` and get the value [A].
     *
     * @param s initial state to run stateful computation.
     * @param MF [Monad] for the context [F].
     */
    fun runA(s: SA, MF: Monad<F>): HK<F, A> = MF.map(run(s, MF)) { it.b }

    /**
     * Run the stateful computation within the context `F` and get the state [S].
     *
     * @param s initial state to run stateful computation.
     * @param MF [Monad] for the context [F].
     */
    fun runS(s: SA, MF: Monad<F>): HK<F, SB> = MF.map(run(s, MF)) { it.a }

}

/**
 * Wrap the function with [IndexedStateT].
 *
 * @param MF [Monad] for the context [F].
 */
inline fun <reified F, SA, SB, A> IndexedStateTFunKind<F, SA, SB, A>.toIndexedStateT(MF: Monad<F> = monad()): IndexedStateT<F, SA, SB, A> = IndexedStateT(this)

/**
 * Wrap the function with [IndexedStateT].
 *
 * @param MF [Monad] for the context [F].
 */
inline fun <reified F, SA, SB, A> IndexedStateTFun<F, SA, SB, A>.toIndexedStateT(MF: Monad<F> = monad()): IndexedStateT<F, SA, SB, A> = IndexedStateT(this, MF)

/**
 * Lift a value of type `A` into `IndexedStateT<F, S, S, A>`.
 *
 * @param MF [Monad] for the context [F].
 * @param fa the value to lift.
 */
inline fun <reified F, S, A> IndexedStateT.Companion.lift(fa: HK<F, A>, MF: Monad<F> = monad<F>()): IndexedStateT<F, S, S, A> = IndexedStateT.lift(MF, fa)

/**
 * Return input without modifying it.
 *
 * @param AF [Applicative] for the context [F].
 */
inline fun <reified F, S> IndexedStateT.Companion.get(AF: Applicative<F> = applicative<F>(), dummy: Unit = Unit): IndexedStateT<F, S, S, S> = IndexedStateT.get(AF)

/**
 * Inspect a value of the state [S] with [f] `(S) -> T` without modifying the state.
 *
 * @param AF [Applicative] for the context [F].
 * @param f the function applied to extrat [T] from [S].
 */
inline fun <reified F, S, T> IndexedStateT.Companion.inspect(AF: Applicative<F> = applicative<F>(), dummy: Unit = Unit, crossinline f: (S) -> T): IndexedStateT<F, S, S, T> = IndexedStateT.inspect(AF) { f(it) }

/**
 * Set the state to a value [s] and return [Unit].
 *
 * @param AF [Applicative] for the context [F].
 * @param s value to set.
 */
inline fun <reified F, SA, SB> IndexedStateT.Companion.set(s: SB, AF: Applicative<F> = applicative<F>()): IndexedStateT<F, SA, SB, Unit> = IndexedStateT.set(AF, s)

/**
 * Set the state to a value [s] of type `HK<F, S>` and return [Unit].
 *
 * @param AF [Applicative] for the context [F].
 * @param s value to set.
 */
inline fun <reified F, S> IndexedStateT.Companion.setF(s: HK<F, S> , AF: Applicative<F> = applicative<F>()): IndexedStateT<F, S, S, Unit> = IndexedStateT.setF(AF, s)

/**
 * Modify the state with [f] `(S) -> S` and return [Unit].
 *
 * @param AF [Applicative] for the context [F].
 * @param f the modify function to apply.
 */
inline fun <reified F, S> IndexedStateT.Companion.modify(AF: Applicative<F> = applicative<F>(), dummy: Unit = Unit, crossinline f: (S) -> S): StateT<F, S, Unit> = IndexedStateT.modify(AF) { f(it) }

/**
 * Modify the state with an [Applicative] function [f] `(S) -> HK<F, S>` and return [Unit].
 *
 * @param AF [Applicative] for the context [F].
 * @param f the modify function to apply.
 */
inline fun <reified F, S> IndexedStateT.Companion.modifyF(AF: Applicative<F> = applicative<F>(), dummy: Unit = Unit, crossinline f: (S) -> HK<F, S>): StateT<F, S, Unit> = IndexedStateT.modifyF(AF) { f(it) }

/**
 * Alias that represent stateful computation of the form `(S) -> Tuple2<S, A>` with a result in certain context `F`.
 */
typealias StateTFun<F, S, A> = IndexedStateTFun<F, S, S, A>

/**
 * Alias that represents wrapped stateful computation in context `F`.
 */
typealias StateTFunKind<F, S, A> = IndexedStateTFunKind<F, S, S, A>

/**
 * Alias for StateTHK
 */
typealias StateTHK = IndexedStateTHK

/**
 * Alias for StateTKind
 */
typealias StateTKind<F, S, A> = IndexedStateTKind<F, S, S, A>

/**
 * Alias to partially apply type parameters [F] and [S] to [StateT]
 */
typealias StateTKindPartial<F, S> = IndexedStateTKindPartial<F, S, S>

/**
 * `StateT<F, S, A>` is a stateful computation within a context `F` yielding
 * a value of type `A`. i.e. StateT<EitherPartialKind<E>, S, A> = Either<E, State<S, A>>
 *
 * @param F the context that wraps the stateful computation.
 * @param S the state we are preforming computation upon.
 * @param A current value of computation.
 */
typealias StateT<F, S, A> = IndexedStateT<F, S, S, A>

/**
 * Constructor for StateT.
 * StateT<F, S, A> is an alias for IndexedStateT<F, S, S, A>
 */
inline fun <reified F, S, A> StateT(noinline run: StateTFun<F, S, A>, MF: Applicative<F> = applicative()): StateT<F, S, A> = IndexedStateT(MF.pure(run))

/**
 * Syntax for constructing a `StateT<F, S, A>` from a function `(S) -> HK<F, Tuple2<S, A>>`
 */
inline fun <reified F, S, A> StateTFunKind<F, S, A>.toStateT(MF: Monad<F> = monad()): StateT<F, S, A> = StateT(this)

/**
 * Alias that represents wrapped stateful computation in context `F`.
 */
inline fun <reified F, S, A> StateTFun<F, S, A>.toStateT(MF: Monad<F> = monad()): StateT<F, S, A> = StateT(this, MF)