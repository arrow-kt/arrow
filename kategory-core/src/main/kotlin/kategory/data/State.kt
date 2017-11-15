package kategory

/**
 * Alias that represent stateful computation of the form `(SA) -> Tuple2<SB, A>`.
 */
typealias IndexedStateFun<SA, SB, A> = IndexedStateTFun<IdHK, SA, SB, A>

/**
 * Alias that represents wrapped stateful computation in context `Id`.
 */
typealias IndexedStateFunKind<SA, SB, A> = IndexedStateTFunKind<IdHK, SA, SB, A>

/**
 * Alias for IndexedStateHK
 */
typealias IndexedStateHK = IndexedStateTHK

/**
 * Alias for IndexedStateKind
 */
typealias IndexedStateKind<SA, SB, A> = IndexedStateTKind<IdHK, SA, SB, A>

/**
 * Alias to partially apply type parameters [SA] and [SB] to [IndexedState]
 */
typealias IndexedStateKindPartial<SA, SB> = IndexedStateTKindPartial<IdHK, SA, SB>

/**
 * `IndexedState<SA, SB, A>` is a stateful computation which represents a state transition of [SA] to [SB] that yields a value of type `A`.
 *
 * @param SA the state we are preforming computation upon.
 * @param SB the state we transition to
 * @param A current value of computation.
 */
typealias IndexedState<SA, SB, A> = IndexedStateT<IdHK, SA, SB, A>

/**
 * Constructor for IndexedState.
 * IndexedState<SA, SB, A> is an alias for IndexedStateT<IdHK, SA, SB, A>
 */
fun <SA, SB, A> IndexedState(run: (SA) -> Tuple2<SB, A>): IndexedState<SA, SB, A> = IndexedStateT(Id(run.andThen { Id(it) }))

/**
 * Syntax for constructing a `IndexedState<IdHK, SA, SB, A>` from a function `(SA) -> Tuple2<SB, A>`
 */
fun <SA, SB, A> IndexedStateFun<SA, SB, A>.toIndexedState(): IndexedState<SA, SB, A> = IndexedState(this)

/**
 * Syntax for constructing a `IndexedState<IdHK, SA, SB, A>` from a function `(SA) -> Tuple2<SB, A>`
 */
fun <SA, SB, A> IndexedStateFunKind<SA, SB, A>.toIndexedState(): IndexedState<SA, SB, A> = IndexedState(this)

/**
 * Alias for IndexedState to make working with `StateT<IdHK, S, A>` more elegant.
 */
fun IndexedState(): StateId = StateId

/**
 * Alias that represent stateful computation of the form `(S) -> Tuple2<S, A>`.
 */
typealias StateFun<S, A> = StateTFun<IdHK, S,A>

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
 * Alias for StateId to make working with `StateT<IdHK, S, A>` more elegant.
 */
fun State(): StateId = StateId

object StateId {

    /**
     * Return input without modifying it.
     */
    fun <S> get(): State<S, S> = IndexedStateT.get(Id.applicative())

    /**
     * Inspect a value of the state [S] with [f] `(S) -> T` without modifying the state.
     */
    fun <S, T> inspect(f: (S) -> T): State<S, T> = IndexedStateT.inspect(Id.applicative(), f)

    /**
     * Modify the state with [f] `(S) -> S` and return [Unit].
     */
    fun <S> modify(f: (S) -> S): State<S, Unit> = IndexedStateT.modify(Id.applicative(), f)

    /**
     * Set the state to [s] and return [Unit].
     */
    fun <SA, SB> set(s: SB): IndexedState<SA, SB, Unit> = IndexedStateT.set(Id.applicative(), s)

    /**
     * Alias for [IndexedStateT.Companion.functor]
     */
    fun <S> functor() = IndexedStateT.functor<IdHK, S>(Id.functor(), dummy = Unit)

    /**
     * Alias for[IndexedStateT.Companion.applicative]
     */
    fun <S> applicative() = IndexedStateT.applicative<IdHK, S>(Id.monad(), dummy = Unit)

    /**
     * Alias for [StateT.Companion.monad]
     */
    fun <S> monad() = IndexedStateT.monad<IdHK, S>(Id.monad(), dummy = Unit)
}

/**
 * Alias for [IndexedStateT.run] `StateT<IdHK, S, A>`
 *
 * @param initial state to start stateful computation.
 */
fun <SA, SB, A> IndexedState<SA, SB, A>.run(initial: SA): Tuple2<SB, A> = run(initial, Id.monad()).value()

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