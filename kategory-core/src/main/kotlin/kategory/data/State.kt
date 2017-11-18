package kategory

/**
 * Alias that represent stateful computation of the form `(S) -> Tuple2<S, A>`.
 */
typealias StateFun<S, A> = StateTFun<IdHK, S, A>

/**
 * Alias that represents wrapped stateful computation in context `Id`.
 */
typealias StateFunKind<S, A> = StateTFunKind<IdHK, S, A>

/**
 * Alias for StateHK
 */
typealias StateHK = IndexedStateTHK

/**
 * Alias for StateKind
 */
typealias StateKind<S, A> = IndexedStateKind<S, S, A>

/**
 * Alias to partially apply type parameters [S] to [IndexedState]
 */
typealias StateKindPartial<S> = StateTKindPartial<IdHK, S>

/**
 * `State<S, A>` is a stateful computation that yields a value of type `A`.
 *
 * @param S the state we are preforming computation upon.
 * @param A current value of computation.
 */
typealias State<S, A> = IndexedState<S, S, A>

/**
 * Constructor for State.
 * State<S, A> is an alias for IndexedStateT<IdHK, S, S, A>
 */
@Suppress("FunctionName")
fun <S, A> State(run: (S) -> Tuple2<S, A>): State<S, A> = IndexedStateT(Id(run.andThen { Id(it) }))

/**
 * Syntax for constructing a `StateT<IdHK, S, A>` from a function `(S) -> Tuple2<S, A>`
 */
fun <S, A> StateFun<S, A>.toState(): State<S, A> = State(this)

/**
 * Syntax for constructing a `StateT<IdHK, S, A>` from a function `(S) -> Tuple2<S, A>`
 */
fun <S, A> StateFunKind<S, A>.toState(): State<S, A> = State(this)

/**
 * Alias for [IndexedStateT.run] `StateT<IdHK, S, A>`
 *
 * @param initial state to start stateful computation.
 */
fun <SA, SB, A> IndexedState<SA, SB, A>.run(initial: SA): Tuple2<SB, A> = run(Id.monad(), initial).value()

/**
 * Alias for [IndexedStateT.runA] `StateT<IdHK, S, A>`
 *
 * @param initial state to start stateful computation.
 */
fun <SA, SB, A> IndexedState<SA, SB, A>.runA(initial: SA): A = run(initial).b

/**
 * Alias for [IndexedStateT.runS] `StateT<IdHK, S, A>`
 *
 * @param initial state to start stateful computation.
 */
fun <SA, SB, A> IndexedState<SA, SB, A>.runS(initial: SA): SB = run(initial).a

/**
 * Alias for StateId to make working with `StateT<IdHK, S, A>` more elegant.
 */
@Suppress("FunctionName")
fun State(): StateApi = StateApi

object StateApi : IndexedStateApi {
    /**
     * Alias for [IndexedStateT.Companion.functor]
     */
    fun <S> functor() = IndexedStateT.functor<IdHK, S, S>(Id.functor(), dummy = Unit)
}