package arrow

/**
 * Alias that represents stateful computation of the form `(S) -> Tuple2<S, A>`.
 */
typealias StateFun<S, A> = StateTFun<IdHK, S, A>

/**
 * Alias that represents wrapped stateful computation in context `Id`.
 */
typealias StateFunKind<S, A> = StateTFunKind<IdHK, S, A>

/**
 * Alias for StateHK
 */
typealias StateHK = StateTHK

/**
 * Alias for StateKind
 */
typealias StateKind<S, A> = StateTKind<IdHK, S, A>

/**
 * Alias to partially apply type parameters [S] to [State]
 */
typealias StateKindPartial<S> = StateTKindPartial<IdHK, S>

/**
 * `State<S, A>` is a stateful computation that yields a value of type `A`.
 *
 * @param S the state we are preforming computation upon.
 * @param A current value of computation.
 */
typealias State<S, A> = StateT<IdHK, S, A>

/**
 * Constructor for State.
 * State<S, A> is an alias for IndexedStateT<IdHK, S, S, A>
 *
 * @param run the stateful function to wrap with [State].
 */
@Suppress("FunctionName")
fun <S, A> State(run: (S) -> Tuple2<S, A>): State<S, A> = StateT(Id(run.andThen { Id(it) }))

/**
 * Syntax for constructing a `StateT<IdHK, S, A>` from a function `(S) -> Tuple2<S, A>`
 */
fun <S, A> StateFun<S, A>.toState(): State<S, A> = State(this)

/**
 * Syntax for constructing a `StateT<IdHK, S, A>` from a function `(S) -> Tuple2<S, A>`
 */
fun <S, A> StateFunKind<S, A>.toState(): State<S, A> = State(this)

/**
 * Alias for [StateT.run] `StateT<IdHK, S, A>`
 *
 * @param initial state to start stateful computation.
 */
fun <S, A> StateT<IdHK, S, A>.run(initial: S): Tuple2<S, A> = run(initial, Id.monad()).value()

/**
 * Alias for [StateT.runA] `StateT<IdHK, S, A>`
 *
 * @param initial state to start stateful computation.
 */
fun <S, A> StateT<IdHK, S, A>.runA(initial: S): A = run(initial).b

/**
 * Alias for [StateT.runS] `StateT<IdHK, S, A>`
 *
 * @param initial state to start stateful computation.
 */
fun <S, A> StateT<IdHK, S, A>.runS(initial: S): S = run(initial).a

/**
 * Alias for StateId to make working with `StateT<IdHK, S, A>` more elegant.
 */
@Suppress("FunctionName")
fun State() = StateApi

object StateApi {

    /**
     * Return input without modifying it.
     */
    fun <S> get(): State<S, S> = StateT.get(Id.applicative())

    /**
     * Inspect a value of the state [S] with [f] `(S) -> T` without modifying the state.
     *
     * @param f the function applied to inspect [T] from [S].
     */
    fun <S, T> inspect(f: (S) -> T): State<S, T> = StateT.inspect(Id.applicative(), f)

    /**
     * Modify the state with [f] `(S) -> S` and return [Unit].
     *
     * @param f the modify function to apply.
     */
    fun <S> modify(f: (S) -> S): State<S, Unit> = StateT.modify(Id.applicative(), f)

    /**
     * Set the state to [s] and return [Unit].
     *
     * @param s value to set.
     */
    fun <S> set(s: S): State<S, Unit> = StateT.set(Id.applicative(), s)

    /**
     * Alias for[StateT.Companion.applicative]
     */
    fun <S> applicative() = StateT.applicative<IdHK, S>(Id.monad(), dummy = Unit)

    /**
     * Alias for [StateT.Companion.functor]
     */
    fun <S> functor() = StateT.functor<IdHK, S>(Id.functor(), dummy = Unit)

    /**
     * Alias for [StateT.Companion.monad]
     */
    fun <S> monad() = StateT.monad<IdHK, S>(Id.monad(), dummy = Unit)

}