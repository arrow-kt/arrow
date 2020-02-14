package arrow.mtl

import arrow.Kind
import arrow.core.AndThen
import arrow.core.Either
import arrow.core.ForId
import arrow.core.Id
import arrow.core.Tuple2
import arrow.core.andThen
import arrow.core.extensions.id.comonad.extract
import arrow.core.extensions.id.monad.monad
import arrow.core.identity
import arrow.core.toT
import arrow.core.value
import arrow.typeclasses.Monad
import arrow.typeclasses.internal.IdBimonad

/**
 * Alias that represents stateful computation of the form `(S) -> Tuple2<S, A>`.
 */
typealias StateFun<S, A> = StateTFun<ForId, S, A>

/**
 * Alias for StateHK
 */
typealias ForState = ForStateT

/**
 * Alias for StateKind
 */
typealias StateOf<S, A> = StateTOf<ForId, S, A>

/**
 * Alias to partially apply type parameters [S] to [State]
 */
typealias StatePartialOf<S> = StateTPartialOf<ForId, S>

/**
 * `State<S, A>` is a stateful computation that yields a value of type `A`.
 *
 * @param S the state we are performing computation upon.
 * @param A current value of computation.
 */
typealias State<S, A> = StateT<ForId, S, A>

fun <S, A> State<S, A>.run(initial: S): Tuple2<S, A> = runF(initial).extract()

/**
 * Constructor for State.
 * State<S, A> is an alias for IndexedStateT<ForId, S, S, A>
 *
 * @param run the stateful function to wrap with [State].
 */
fun <S, A> State(run: (S) -> Tuple2<S, A>): State<S, A> = StateT(AndThen(run).andThen { Id(it) })

/**
 * Syntax for constructing a `StateT<ForId, S, A>` from a function `(S) -> Tuple2<S, A>`
 */
fun <S, A> StateFun<S, A>.toState(): State<S, A> = State(this)

fun <S, T, P1, R> State<S, T>.map(sx: State<S, P1>, f: (T, P1) -> R): State<S, R> =
  flatMap(IdBimonad) { t -> sx.map { x -> f(t, x) } }.fix()

fun <S, T, R> State<S, T>.map(f: (T) -> R): State<S, R> = flatMap(IdBimonad) { t -> StateApi.just<S, R>(f(t)) }.fix()

fun <S, A, B> State<S, A>.flatMap(f: (A) -> State<S, B>): State<S, B> =
  State(AndThen(runF).andThenF(
    AndThen(f).compose { (_, a): Tuple2<S, A> -> a }.flatMap { st -> AndThen(st.runF).compose { (s, _): Tuple2<S, A> -> s } }
      .compose { it.value() }
  ))

/**
 * Alias for [StateT.runA] `StateT<ForId, S, A>`
 *
 * @param initial state to start stateful computation.
 */
fun <S, A> StateT<ForId, S, A>.runA(initial: S): A = run(initial).b

/**
 * Alias for [StateT.runS] `StateT<ForId, S, A>`
 *
 * @param initial state to start stateful computation.
 */
fun <S, A> StateT<ForId, S, A>.runS(initial: S): S = run(initial).a

/**
 * Alias for StateId to make working with `StateT<ForId, S, A>` more elegant.
 */
fun State() = StateApi

object StateApi {

  fun <S, T> just(t: T): State<S, T> = StateT.just(IdBimonad, t)

  /**
   * Return input without modifying it.
   */
  fun <S> get(): State<S, S> = StateT.get(IdBimonad)

  /**
   * Inspect a value of the state [S] with [f] `(S) -> T` without modifying the state.
   *
   * @param f the function applied to inspect [T] from [S].
   */
  fun <S, T> inspect(f: (S) -> T): State<S, T> = StateT.inspect(IdBimonad, f)

  /**
   * Modify the state with [f] `(S) -> S` and return [Unit].
   *
   * @param f the modify function to apply.
   */
  fun <S> modify(f: (S) -> S): State<S, Unit> = StateT.modify(IdBimonad, f)

  /**
   * Set the state to [s] and return [Unit].
   *
   * @param s value to set.
   */
  fun <S> set(s: S): State<S, Unit> = StateT.set(IdBimonad, s)

  fun <S> monad(): Monad<StatePartialOf<S>> = object : Monad<StatePartialOf<S>> {
    // Copypasta because kotlin keeps wanting to call this above the other version and there is no easy way to alias it as it is in the same file
    override fun <A, B> Kind<StatePartialOf<S>, A>.flatMap(f: (A) -> Kind<StatePartialOf<S>, B>): Kind<StatePartialOf<S>, B> =
      StateT(AndThen(fix().runF).andThenF(
        AndThen(f).compose { (_, a): Tuple2<S, A> -> a }.flatMap { st -> AndThen(st.fix().runF).compose { (s, _): Tuple2<S, A> -> s } }
          .compose { it.value() }
      ))

    override fun <A> just(a: A): Kind<StatePartialOf<S>, A> = State { s -> s toT a }

    override fun <A, B> tailRecM(a: A, f: (A) -> Kind<StatePartialOf<S>, Either<A, B>>): Kind<StatePartialOf<S>, B> =
      StateT.tailRecM(Id.monad(), a, f)
  }
}

fun <R, S, T> List<T>.stateTraverse(f: (T) -> State<S, R>): State<S, List<R>> = foldRight(StateApi.just(emptyList())) { i: T, accumulator: State<S, List<R>> ->
  f(i).map(accumulator, ({ head: R, tail: List<R> ->
    listOf(head) + tail
  }))
}

fun <S, T> List<State<S, T>>.stateSequential(): State<S, List<T>> = stateTraverse(::identity)
