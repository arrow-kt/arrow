package arrow

/**
 * Alias that represent stateful computation of the form `(S) -> Tuple2<S, A>` with a result in certain context `F`.
 */
typealias StateTFun<F, S, A> = (S) -> HK<F, Tuple2<S, A>>

/**
 * Alias that represents wrapped stateful computation in context `F`.
 */
typealias StateTFunKind<F, S, A> = HK<F, StateTFun<F, S, A>>

/**
 * Run the stateful computation within the context `F`.
 *
 * @param MF [Monad] for the context [F]
 * @param s initial state to run stateful computation
 */
fun <F, S, A> StateTKind<F, S, A>.runM(MF: Monad<F>, initial: S): HK<F, Tuple2<S, A>> = ev().run(initial, MF)

/**
 * Run the stateful computation within the context `F`.
 *
 * @param s initial state to run stateful computation
 * @param MF [Monad] for the context [F]
 */
inline fun <reified F, S, A> StateTKind<F, S, A>.runM(initial: S, MF: Monad<F> = monad()): HK<F, Tuple2<S, A>> = ev().run(initial, MF)

/**
 * `StateT<F, S, A>` is a stateful computation within a context `F` yielding
 * a value of type `A`. i.e. StateT<EitherPartialKind<E>, S, A> = Either<E, State<S, A>>
 *
 * @param F the context that wraps the stateful computation.
 * @param S the state we are preforming computation upon.
 * @param A current value of computation.
 * @param runF the stateful computation that is wrapped and managed by `StateT`
 */
@higherkind
class StateT<F, S, A>(
        val runF: StateTFunKind<F, S, A>
) : StateTKind<F, S, A>, StateTKindedJ<F, S, A> {

    companion object {

        inline fun <reified F, S, T> pure(t: T, MF: Monad<F> = monad<F>()): StateT<F, S, T> =
                StateT { s -> MF.pure(s toT t) }

        /**
         * Constructor to create `StateT<F, S, A>` given a [StateTFun].
         *
         * @param run the stateful function to wrap with [StateT].
         * @param MF [Monad] for the context [F].
         */
        inline operator fun <reified F, S, A> invoke(noinline run: StateTFun<F, S, A>, MF: Monad<F> = monad<F>()): StateT<F, S, A> = StateT(MF.pure(run))

        /**
         * Constructor to create `StateT<F, S, A>` given a [StateTFun].
         *
         * @param MF [Monad] for the context [F].
         * @param run the stateful function to wrap with [StateT].
         */
        inline operator fun <reified F, S, A> invoke(MF: Monad<F> = monad<F>(), noinline run: StateTFun<F, S, A>): StateT<F, S, A> = StateT(MF.pure(run))

        /**
         * Alias for constructor [StateT].
         *
         * @param runF the function to wrap within [StateT].
         */
        fun <F, S, A> invokeF(runF: StateTFunKind<F, S, A>): StateT<F, S, A> = StateT(runF)

        /**
         * Lift a value of type `A` into `StateT<F, S, A>`.
         *
         * @param MF [Monad] for the context [F].
         * @param fa the value to lift.
         */
        fun <F, S, A> lift(MF: Monad<F>, fa: HK<F, A>): StateT<F, S, A> = StateT(MF.pure({ s -> MF.map(fa, { a -> Tuple2(s, a) }) }))

        /**
         * Return input without modifying it.
         *
         * @param AF [Applicative] for the context [F].
         */
        fun <F, S> get(AF: Applicative<F>): StateT<F, S, S> = StateT(AF.pure({ s -> AF.pure(Tuple2(s, s)) }))

        /**
         * Inspect a value of the state [S] with [f] `(S) -> T` without modifying the state.
         *
         *
         *
         * @param AF [Applicative] for the context [F].
         * @param f the function applied to inspect [T] from [S].
         */
        fun <F, S, T> inspect(AF: Applicative<F>, f: (S) -> T): StateT<F, S, T> = StateT(AF.pure({ s -> AF.pure(Tuple2(s, f(s))) }))

        /**
         * Modify the state with [f] `(S) -> S` and return [Unit].
         *
         * @param AF [Applicative] for the context [F].
         * @param f the modify function to apply.
         */
        fun <F, S> modify(AF: Applicative<F>, f: (S) -> S): StateT<F, S, Unit> = StateT(AF.pure({ s -> AF.map(AF.pure(f(s))) { it toT Unit } }))

        /**
         * Modify the state with an [Applicative] function [f] `(S) -> HK<F, S>` and return [Unit].
         *
         * @param AF [Applicative] for the context [F].
         * @param f the modify function to apply.
         */
        fun <F, S> modifyF(AF: Applicative<F>, f: (S) -> HK<F, S>): StateT<F, S, Unit> = StateT(AF.pure({ s -> AF.map(f(s)) { it toT Unit } }))

        /**
         * Set the state to a value [s] and return [Unit].
         *
         * @param AF [Applicative] for the context [F].
         * @param s value to set.
         */
        fun <F, S> set(AF: Applicative<F>, s: S): StateT<F, S, Unit> = StateT(AF.pure({ _ -> AF.pure(Tuple2(s, Unit)) }))

        /**
         * Set the state to a value [s] of type `HK<F, S>` and return [Unit].
         *
         * @param AF [Applicative] for the context [F].
         * @param s value to set.
         */
        fun <F, S> setF(AF: Applicative<F>, s: HK<F, S>): StateT<F, S, Unit> = StateT(AF.pure({ _ -> AF.map(s) { Tuple2(it, Unit) } }))

        /**
         * Tail recursive function that keeps calling [f]  until [arrow.Either.Right] is returned.
         *
         * @param a initial value to start running recursive call to [f]
         * @param f function that is called recusively until [arrow.Either.Right] is returned.
         * @param MF [Monad] for the context [F].
         */
        fun <F, S, A, B> tailRecM(a: A, f: (A) -> HK<StateTKindPartial<F, S>, Either<A, B>>, MF: Monad<F>): StateT<F, S, B> =
                StateT(MF.pure({ s: S ->
                    MF.tailRecM(Tuple2(s, a), { (s, a0) ->
                        MF.map(f(a0).runM(MF, s)) { (s, ab) ->
                            ab.bimap({ a1 -> Tuple2(s, a1) }, { b -> Tuple2(s, b) })
                        }
                    })
                }))
    }

    /**
     * Map current value [A] given a function [f].
     *
     * @param f the function to apply.
     * @param FF [Functor] for the context [F].
     */
    fun <B> map(f: (A) -> B, FF: Functor<F>): StateT<F, S, B> = transform({ (s, a) -> Tuple2(s, f(a)) }, FF)

    /**
     * Combine with another [StateT] of same context [F] and state [S].
     *
     * @param sb other state with value of type `B`.
     * @param f the function to apply.
     * @param MF [Monad] for the context [F].
     */
    fun <B, Z> map2(sb: StateT<F, S, B>, fn: (A, B) -> Z, MF: Monad<F>): StateT<F, S, Z> =
            invokeF(MF.map2(runF, sb.runF) { (ssa, ssb) ->
                ssa.andThen { fsa ->
                    MF.flatMap(fsa) { (s, a) ->
                        MF.map(ssb(s)) { (s, b) -> Tuple2(s, fn(a, b)) }
                    }
                }
            })

    /**
     * Controlled combination of [StateT] that is of same context [F] and state [S] using [Eval].
     *
     * @param sb other state with value of type `B`.
     * @param f the function to apply.
     * @param MF [Monad] for the context [F].
     */
    fun <B, Z> map2Eval(sb: Eval<StateT<F, S, B>>, fn: (A, B) -> Z, MF: Monad<F>): Eval<StateT<F, S, Z>> =
            MF.map2Eval(runF, sb.map { it.runF }) { (ssa, ssb) ->
                ssa.andThen { fsa ->
                    MF.flatMap(fsa) { (s, a) ->
                        MF.map(ssb((s))) { (s, b) -> Tuple2(s, fn(a, b)) }
                    }
                }
            }.map { invokeF(it) }

    /**
     * Apply a function `(S) -> B` that operates within the [StateT] context.
     *
     * @param ff function with the [StateT] context.
     * @param MF [Monad] for the context [F].
     */
    fun <B> ap(ff: StateTKind<F, S, (A) -> B>, MF: Monad<F>): StateT<F, S, B> =
            ff.ev().map2(this, { f, a -> f(a) }, MF)

    /**
     * Create a product of the value types of [StateT].
     *
     * @param sb other stateful computation.
     * @param MF [Monad] for the context [F].
     */
    fun <B> product(sb: StateT<F, S, B>, MF: Monad<F>): StateT<F, S, Tuple2<A, B>> = map2(sb, { a, b -> Tuple2(a, b) }, MF)

    /**
     * Map the value [A] to another [StateT] object for the same state [S] and context [F] and flatten the structure.
     *
     * @param fas the function to apply.
     * @param MF [Monad] for the context [F].
     */
    fun <B> flatMap(fas: (A) -> StateTKind<F, S, B>, MF: Monad<F>): StateT<F, S, B> =
            invokeF(
                    MF.map(runF) { sfsa ->
                        sfsa.andThen { fsa ->
                            MF.flatMap(fsa) {
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
    fun <B> flatMapF(faf: (A) -> HK<F, B>, MF: Monad<F>): StateT<F, S, B> =
            invokeF(
                    MF.map(runF) { sfsa ->
                        sfsa.andThen { fsa ->
                            MF.flatMap(fsa) { (s, a) ->
                                MF.map(faf(a)) { b -> Tuple2(s, b) }
                            }
                        }
                    })

    /**
     * Transform the product of state [S] and value [A] to an another product of state [S] and an arbitrary type [B].
     *
     * @param f the function to apply.
     * @param FF [Functor] for the context [F].
     */
    fun <B> transform(f: (Tuple2<S, A>) -> Tuple2<S, B>, FF: Functor<F>): StateT<F, S, B> =
            invokeF(
                    FF.map(runF) { sfsa ->
                        sfsa.andThen { fsa ->
                            FF.map(fsa, f)
                        }
                    })

    /**
     * Combine two [StateT] objects using an instance of [SemigroupK] for [F].
     *
     * @param y other [StateT] object to combine.
     * @param MF [Monad] for the context [F].
     * @param SF [SemigroupK] for [F].
     */
    fun combineK(y: StateTKind<F, S, A>, MF: Monad<F>, SF: SemigroupK<F>): StateT<F, S, A> =
            StateT(MF.pure({ s -> SF.combineK(run(s, MF), y.ev().run(s, MF)) }))

    /**
     * Run the stateful computation within the context `F`.
     *
     * @param s initial state to run stateful computation.
     * @param MF [Monad] for the context [F].
     */
    fun run(initial: S, MF: Monad<F>): HK<F, Tuple2<S, A>> = MF.flatMap(runF) { f -> f(initial) }

    /**
     * Run the stateful computation within the context `F` and get the value [A].
     *
     * @param s initial state to run stateful computation.
     * @param MF [Monad] for the context [F].
     */
    fun runA(s: S, MF: Monad<F>): HK<F, A> = MF.map(run(s, MF)) { it.b }

    /**
     * Run the stateful computation within the context `F` and get the state [S].
     *
     * @param s initial state to run stateful computation.
     * @param MF [Monad] for the context [F].
     */
    fun runS(s: S, MF: Monad<F>): HK<F, S> = MF.map(run(s, MF)) { it.a }
}

/**
 * Wrap the function with [StateT].
 *
 * @param MF [Monad] for the context [F].
 */
inline fun <reified F, S, A> StateTFunKind<F, S, A>.stateT(MF: Monad<F> = monad()): StateT<F, S, A> = StateT(this)

/**
 * Wrap the function with [StateT].
 *
 * @param MF [Monad] for the context [F].
 */
inline fun <reified F, S, A> StateTFun<F, S, A>.stateT(MF: Monad<F> = monad()): StateT<F, S, A> = StateT(this, MF)

/**
 * Lift a value of type `A` into `StateT<F, S, A>`.
 *
 * @param MF [Monad] for the context [F].
 * @param fa the value to lift.
 */
inline fun <reified F, S, A> StateT.Companion.lift(fa: HK<F, A>, MF: Monad<F> = monad<F>()): StateT<F, S, A> = StateT.lift(MF, fa)

/**
 * Return input without modifying it.
 *
 * @param AF [Applicative] for the context [F].
 */
inline fun <reified F, S> StateT.Companion.get(AF: Applicative<F> = applicative<F>(), dummy: Unit = Unit): StateT<F, S, S> = StateT.get(AF)

/**
 * Inspect a value of the state [S] with [f] `(S) -> T` without modifying the state.
 *
 * @param AF [Applicative] for the context [F].
 * @param f the function applied to extrat [T] from [S].
 */
inline fun <reified F, S, T> StateT.Companion.inspect(AF: Applicative<F> = applicative<F>(), dummy: Unit = Unit, crossinline f: (S) -> T): StateT<F, S, T> = StateT.inspect(AF) { f(it) }

/**
 * Modify the state with [f] `(S) -> S` and return [Unit].
 *
 * @param AF [Applicative] for the context [F].
 * @param f the modify function to apply.
 */
inline fun <reified F, S> StateT.Companion.modify(AF: Applicative<F> = applicative<F>(), dummy: Unit = Unit, crossinline f: (S) -> S): StateT<F, S, Unit> = StateT.modify(AF) { f(it) }

/**
 * Modify the state with an [Applicative] function [f] `(S) -> HK<F, S>` and return [Unit].
 *
 * @param AF [Applicative] for the context [F].
 * @param f the modify function to apply.
 */
inline fun <reified F, S> StateT.Companion.modifyF(AF: Applicative<F> = applicative<F>(), dummy: Unit = Unit, crossinline f: (S) -> HK<F, S>): StateT<F, S, Unit> = StateT.modifyF(AF) { f(it) }

/**
 * Set the state to a value [s] and return [Unit].
 *
 * @param AF [Applicative] for the context [F].
 * @param s value to set.
 */
inline fun <reified F, S> StateT.Companion.set(s: S, AF: Applicative<F> = applicative<F>()): StateT<F, S, Unit> = StateT.set(AF, s)

/**
 * Set the state to a value [s] of type `HK<F, S>` and return [Unit].
 *
 * @param AF [Applicative] for the context [F].
 * @param s value to set.
 */
inline fun <reified F, S> StateT.Companion.set(s: HK<F, S>, AF: Applicative<F> = applicative<F>()): StateT<F, S, Unit> = StateT.setF(AF, s)
