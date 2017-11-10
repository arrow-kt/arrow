package kategory

/**
 * Convenience object to make working with `StateT<IdHK, S, A>` more elegant.
 */
object State {

    /**
     * Construct a `StateT<IdHK, S, A>` from a function [run] `(S) -> Tuple2<S, A>`.
     */
    operator fun <S, A> invoke(run: (S) -> Tuple2<S, A>): StateT<IdHK, S, A> = StateT(Id(run.andThen { Id(it) }))

    /**
     * Lift a value of type `A` into `StateT<IdHK, S, A>`.
     */
    fun <S, A> lift(a: A): StateT<IdHK, S, A> = StateT.lift(Id.monad(), Id.pure(a))

    /**
     * Return input without modifying it.
     */
    fun <S> get(): StateT<IdHK, S, S> = State { s: S -> s toT s }

    /**
     * Inspect a value of the state [S] with [f] `(S) -> T` without modifying the state.
     */
    fun <S, T> inspect(f: (S) -> T): StateT<IdHK, S, T> = State { s: S -> s toT f(s) }

    /**
     * Modify the state with [f] `(S) -> S` and return [Unit].
     */
    fun <S> modify(f: (S) -> S): StateT<IdHK, S, Unit> = State { s: S -> f(s) toT Unit }

    /**
     * Set the state to [s] and return [Unit].
     */
    fun <S> set(s: S): StateT<IdHK, S, Unit> = State { _: S -> s toT Unit }

    /**
     * Alias for [StateT.Companion.functor]
     */
    fun <S> functor() = StateT.functor<IdHK, S>(Id.functor(), dummy = Unit)

    /**
     * Alias for[StateT.Companion.applicative]
     */
    fun <S> applicative() = StateT.applicative<IdHK, S>(Id.monad(), dummy = Unit)

    /**
     * Alias for [StateT.Companion.monad]
     */
    fun <S> monad() = StateT.monad<IdHK, S>(Id.monad(), dummy = Unit)
}

/**
 * Syntax for getting a `StateT<IdHK, S, A>` from a function `(S) -> Tuple2<S, A>`
 */
fun <S, A> ((S) -> Tuple2<S, A>).state(): StateT<IdHK, S, A> = State(this)

/**
 * Alias for [StateT.run] `StateT<IdHK, S, A>`
 */
fun <S, A> StateT<IdHK, S, A>.run(initial: S): Tuple2<S, A> = run(initial, Id.monad()).value()

/**
 * Alias for [StateT.runA] `StateT<IdHK, S, A>`
 */
fun <S, A> StateT<IdHK, S, A>.runA(s: S): A = run(s).b

/**
 * Alias for [StateT.runS] `StateT<IdHK, S, A>`
 */
fun <S, A> StateT<IdHK, S, A>.runS(s: S): S = run(s).a