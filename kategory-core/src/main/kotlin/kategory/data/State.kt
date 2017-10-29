package kategory

object State {
    operator fun <S, A> invoke(run: (S) -> Tuple2<S, A>): StateT<IdHK, S, A> = StateT(Id(run.andThen { Id(it) }))
    fun <S> get(): StateT<IdHK, S, S> = State { s -> s toT s }
    fun <S, T> gets(f: (S) -> T): StateT<IdHK, S, T> = State { s -> s toT f(s) }
    fun <S> modify(f: (S) -> S): StateT<IdHK, S, Unit> = State { f(it) toT Unit }
    fun <S> set(s: S): StateT<IdHK, S, Unit> = State { _ -> s toT Unit }
    fun <S> put(s: S): StateT<IdHK, S, Unit> = State { _ -> s toT Unit }

    fun <S> functor() = StateT.functor<IdHK, S>(Id.functor(), dummy = Unit)
    fun <S> applicative() = StateT.applicative<IdHK, S>(Id.monad(), dummy = Unit)
    fun <S> monad() = StateT.monad<IdHK, S>(Id.monad(), dummy = Unit)
}

fun <S, A> ((S) -> Tuple2<S, A>).state(): StateT<IdHK, S, A> = State(this)

fun <S, A> StateT<IdHK, S, A>.run(initial: S): Tuple2<S, A> = run(initial, Id.monad()).value()

fun <S, A> StateT<IdHK, S, A>.runA(s: S): A = run(s).b

fun <S, A> StateT<IdHK, S, A>.runS(s: S): S = run(s).a