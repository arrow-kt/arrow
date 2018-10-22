package arrow.data

import arrow.core.*
import arrow.typeclasses.internal.IdBimonad

/**
 * Alias that represents stateful computation of the form `(S) -> Tuple2<S, A>`.
 */
typealias IndexedStateFun<SA, SB, A> = IndexedStateTFun<ForId, SA, SB, A>

/**
 * Alias that represents wrapped stateful computation in context `Id`.
 */
typealias IndexedStateFunOf<SA, SB, A> = IndexedStateTFunOf<ForId, SA, SB, A>

/**
 * Alias for StateHK
 */
typealias IndexedForState = ForIndexedStateT

/**
 * Alias for StateKind
 */
typealias IndexedStateOf<SA, SB, A> = IndexedStateTOf<ForId, SA, SB, A>

/**
 * Alias to partially apply type parameters [S] to [State]
 */
typealias IndexedStatePartialOf<SA, SB> = IndexedStateTPartialOf<ForId, SA, SB>

typealias IndexedState<SA, SB, A> = IndexedStateT<ForId, SA, SB, A>

/**
 * Constructor for State.
 * State<S, A> is an alias for IndexedStateT<ForId, S, S, A>
 *
 * @param run the stateful function to wrap with [State].
 */
@Suppress("FunctionName")
fun <SA, SB, A> IndexedState(run: (SA) -> Tuple2<SB, A>): IndexedState<SA, SB, A> =
  IndexedStateT(Id(run.andThen { Id(it) }))


/**
 * Syntax for constructing a `StateT<ForId, S, A>` from a function `(S) -> Tuple2<S, A>`
 */
fun <SA, SB, A> IndexedStateFun<SA, SB, A>.toIndexedState(): IndexedState<SA, SB, A> = IndexedStateT(IdBimonad, this)

/**
 * Syntax for constructing a `StateT<ForId, S, A>` from a function `(S) -> Tuple2<S, A>`
 */
fun <SA, SB, A> IndexedStateFunOf<SA, SB, A>.toIndexedState(): IndexedState<SA, SB, A> = IndexedState(this)

fun <SA, SB, T, R> IndexedState<SA, SB, T>.map(f: (T) -> R): IndexedState<SA, SB, R> =
  flatMap(IdBimonad) { t -> IndexedStateApi.just<SB, R>(f(t)) }.fix()

/**
 * Alias for [StateT.run] `StateT<ForId, S, A>`
 *
 * @param initial state to start stateful computation.
 */
fun <SA, SB, A> IndexedStateT<ForId, SA, SB, A>.run(initial: SA): Tuple2<SB, A> = run(IdBimonad, initial).value()

/**
 * Alias for [StateT.runA] `StateT<ForId, S, A>`
 *
 * @param initial state to start stateful computation.
 */
fun <SA, SB, A> IndexedStateT<ForId, SA, SB, A>.runA(initial: SA): A = run(initial).b

/**
 * Alias for [StateT.runS] `StateT<ForId, S, A>`
 *
 * @param initial state to start stateful computation.
 */
fun <SA, SB, A> IndexedStateT<ForId, SA, SB, A>.runS(initial: SA): SB = run(initial).a

/**
 * Alias for StateId to make working with `StateT<ForId, S, A>` more elegant.
 */
@Suppress("FunctionName")
fun IndexedState() = IndexedStateApi

object IndexedStateApi {

  fun <S, T> just(t: T): IndexedState<S, S, T> = IndexedStateT.just(IdBimonad, t)

  /**
   * Return input without modifying it.
   */
  fun <S> get(): IndexedState<S, S, S> = IndexedStateT.get(IdBimonad)

  /**
   * Inspect a value of the state [S] with [f] `(S) -> T` without modifying the state.
   *
   * @param f the function applied to inspect [T] from [S].
   */
  fun <S, T> inspect(f: (S) -> T): IndexedState<S, S, T> = IndexedStateT.inspect(IdBimonad, f)

  /**
   * Modify the state with [f] `(S) -> S` and return [Unit].
   *
   * @param f the modify function to apply.
   */
  fun <S> modify(f: (S) -> S): IndexedState<S, S, Unit> = IndexedStateT.modify(IdBimonad, f)

  /**
   * Set the state to [s] and return [Unit].
   *
   * @param s value to set.
   */
  fun <S> set(s: S): IndexedState<S, S, Unit> = IndexedStateT.set(IdBimonad, s)
}
