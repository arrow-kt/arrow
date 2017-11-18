package kategory


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
@Suppress("FunctionName")
inline fun <reified F, S, A> StateT(noinline run: StateTFun<F, S, A>, MF: Applicative<F> = applicative()): StateT<F, S, A> =
        IndexedStateT(MF.pure(run))

/**
 * Syntax for constructing a `StateT<F, S, A>` from a function `(S) -> HK<F, Tuple2<S, A>>`
 */
inline fun <reified F, S, A> StateTFunKind<F, S, A>.toStateT(MF: Monad<F> = kategory.monad()): StateT<F, S, A> =
        StateT(this)

/**
 * Alias that represents wrapped stateful computation in context `F`.
 */
inline fun <reified F, S, A> StateTFun<F, S, A>.toStateT(MF: Monad<F> = kategory.monad()): StateT<F, S, A> =
        StateT(this, MF)