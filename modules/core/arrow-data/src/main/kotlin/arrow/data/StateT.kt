package arrow.data

import arrow.typeclasses.Applicative

/**
 * Alias that represent stateful computation of the form `(S) -> Tuple2<S, A>` with a result in certain context `F`.
 */
typealias StateTFun<F, S, A> = IndexedStateTFun<F, S, S, A>

/**
 * Alias that represents wrapped stateful computation in context `F`.
 */
typealias StateTFunOf<F, S, A> = IndexedStateTFunOf<F, S, S, A>

/**
 * Alias for StateTHK
 */
typealias ForStateT = ForIndexedStateT

/**
 * Alias for StateTKind
 */
typealias StateTOf<F, S, A> = IndexedStateTOf<F, S, S, A>

/**
 * Alias to partially apply type parameters [F] and [S] to [StateT]
 */
typealias StateTPartialOf<F, S> = IndexedStateTPartialOf<F, S, S>

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
 * Constructor for State.
 * StateT<F, S, A> is an alias for IndexedStateT<F, S, S, A>
 *
 * @param runF the stateful function to wrap with [State].
 */
@Suppress("FunctionName")
fun <F, S, A> StateT(runF: StateTFunOf<F, S, A>,  @Suppress("UNUSED_PARAMETER") dummy: Unit = Unit): StateT<F, S, A> = IndexedStateT(runF)

/**
 * Constructor for State.
 * StateT<F, S, A> is an alias for IndexedStateT<F, S, S, A>
 *
 * @param AF applicative instance for context [F].
 * @param run the stateful function to wrap with [State].
 */
@Suppress("FunctionName")
fun <F, S, A> StateT(AF: Applicative<F>, run: StateTFun<F, S, A>): StateT<F, S, A> = IndexedStateT(AF.just(run))
