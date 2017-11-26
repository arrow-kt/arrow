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
@Suppress("FunctionName")
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
@Suppress("FunctionName")
fun IndexedState(): IndexedStateApi = IndexedStateApiInstance

object IndexedStateApiInstance : IndexedStateApi {
    /**
     * Alias for [IndexedStateT.Companion.functor]
     */
    fun <SA, SB> functor() = IndexedStateT.functor<IdHK, SA, SB>(Id.functor(), dummy = Unit)
}

interface IndexedStateApi {

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
     * Alias for[IndexedStateT.Companion.applicative]
     */
    fun <S> applicative() = IndexedStateT.applicative<IdHK, S>(Id.monad(), dummy = Unit)

    /**
     * Alias for [StateT.Companion.monad]
     */
    fun <S> monad() = IndexedStateT.monad<IdHK, S>(Id.monad(), dummy = Unit)

}

/**
 * Map current value [A] to [B] given a function [f].
 *
 * @param f the function to apply.
 */
inline fun <SA, SB, A, B> IndexedState<SA, SB, A>.map(crossinline f: (A) -> B): IndexedState<SA, SB, B> =
        map(Id.monad()) { f(it) }

/**
 * Combine with another [IndexedState] of same state [S].
 *
 * @param sb other state with value of type `B`.
 * @param f the function to apply.
 */
inline fun <SA, SB, A, B, SC, Z> IndexedState<SA, SB, A>.map2(sb: IndexedStateKind<SB, SC, B>, crossinline fn: (A, B) -> Z): IndexedState<SA, SC, Z> =
        map2(Id.monad(), sb, fn)

/**
 * Apply a function `(S) -> B` that operates within the [IndexedState] context.
 *
 * @param ff function with the [IndexedState] context.
 */
fun <SA, SB, A, B, SC> IndexedState<SA, SB, A>.ap(ff: IndexedStateKind<SB, SC, (A) -> B>): IndexedState<SA, SC, B> =
        ap(Id.monad(), ff)

/**
 * Create a product of the value types of [IndexedState].
 *
 * @param sb other stateful computation.
 */
fun <SA, SB, A, B, SC> IndexedState<SA, SB, A>.product(sb: IndexedStateKind<SB, SC, B>): IndexedState<SA, SC, Tuple2<A, B>> =
        product(Id.monad(), sb)

/**
 * Map the value [A] to another [IndexedState] object for the same state [S] and flatten the structure.
 *
 * @param fas the function to apply.
 */
fun <SA, SB, A, B, SC> IndexedState<SA, SB, A>.flatMap(fas: (A) -> IndexedStateKind<SB, SC, B>): IndexedState<SA, SC, B> =
        flatMap(Id.monad(), fas)

/**
 * Change state to [S0] given that you provide a function that proofs you can transform [S0] back into [SA].
 *
 * @param f function that proofs that you can transform [S0] back into [SA].
 */
inline fun <SA, SB, A, S0> IndexedState<SA, SB, A>.contramap(crossinline f: (S0) -> SA): IndexedState<S0, SB, A> =
        contramap(Id.functor(), f)

/**
 * [Contramap] and [leftMap].
 *
 * @param f function that proofs that you can transform [S0] back into [SA].
 * @param g function to map resulting state [SB] to [SC].
 */
inline fun <SA, SB, A, S0, SC> IndexedState<SA, SB, A>.dimap(crossinline f: (S0) -> SA, crossinline g: (SB) -> SC): IndexedState<S0, SC, A> =
        dimap(Id.functor(), f, g)

/**
 * Bimap the value [A] and the state [SB].
 *
 * @param f function to map state [SB] to [SC].
 * @param g function to map value [A] to [B].
 */
inline fun <SA, SB, A, B, SC> IndexedState<SA, SB, A>.bimap(crossinline f: (SB) -> SC, crossinline g: (A) -> B): IndexedState<SA, SC, B> =
        bimap(f, g, Id.functor())

/**
 * Map the resulting state [SB] to a new state [SC].
 *
 * @param f function to map resulting state [SB] to [SC].
 */
inline fun <SA, SB, A, SC> IndexedState<SA, SB, A>.mapLeft(crossinline f: (SB) -> SC): IndexedState<SA, SC, A> =
        mapLeft(Id.functor(), f)

/**
 * Transform the product of state [S] and value [A] to an another product of state [S] and an arbitrary type [B].
 *
 * @param f the function to apply.
 */
inline fun <SA, SB, A, B, SC> IndexedState<SA, SB, A>.transform(crossinline f: (Tuple2<SB, A>) -> Tuple2<SC, B>): IndexedState<SA, SC, B> =
        transform(Id.functor(), f)

/**
 * Transform the state used to an arbitrary type [R].
 *
 * @param f function that can extract state [SA] from [R].
 * @param g function that can calculate new state [R].
 */
inline fun <SA, SB, A, R> IndexedState<SA, SB, A>.transformS(crossinline f: (R) -> SA, crossinline g: (Tuple2<R, SB>) -> R): IndexedState<R, R, A> =
        transformS(Id.functor(), f, g)

/**
 * Modify the state [SB].
 *
 * @param f function to modify state [SB] to [SC].
 */
inline fun <SA, SB, A, SC> IndexedState<SA, SB, A>.modify(crossinline f: (SB) -> SC): IndexedState<SA, SC, A> =
        modify(Id.functor(), f)

/**
 * Inspect a value from the input state, without modifying the state.
 *
 * @param f function to inspect value from the state [SB].
 */
inline fun <SA, SB, A, B> IndexedState<SA, SB, A>.inspect(crossinline f: (SB) -> B): IndexedState<SA, SB, B> =
        inspect(Id.functor(), f)

/**
 * Get the input state, without modifying the state.
 */
fun <SA, SB, A> IndexedState<SA, SB, A>.get(): IndexedState<SA, SB, SB> =
        inspect(Id.functor(), ::identity)
