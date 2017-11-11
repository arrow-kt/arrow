package kategory

typealias IndexedStateFun<F, SA, SB, A> = IndexedStateTFun<IdHK, SA, SB, A>
typealias IndexedStateFunKind<F, SA, SB, A> = IndexedStateTFunKind<F, SA, SB, A>
typealias IndexedStateHK = IndexedStateTHK
typealias IndexedStateKindPartial<SA, SB> = IndexedStateTKindPartial<IdHK, SA, SB>
typealias IndexedState<SA, SB, A> = IndexedStateT<IdHK, SA, SB, A>
fun <SA, SB, A> IndexedState(run: (SA) -> Tuple2<SB, A>): IndexedState<SA, SB, A> = IndexedStateT(Id(run.andThen { Id(it) }))
fun <SA, SB, A> ((SA) -> Tuple2<SB, A>).toIndexedState(): IndexedState<SA, SB, A> = IndexedState(this)

typealias StateFun<F, S, A> = StateTFun<IdHK, S,A>
typealias StateFunKind<F, S, A> = StateTFunKind<F, S, A>
typealias StateHK = IndexedStateTHK
typealias StateKindPartial<S> = StateTKindPartial<IdHK, S>
typealias State<S, A> = IndexedState<S, S, A>
fun <S, A> State(run: (S) -> Tuple2<S, A>): State<S, A> = IndexedStateT(Id(run.andThen { Id(it) }))
fun <S, A> ((S) -> Tuple2<S, A>).toState(): State<S, A> = State(this)

fun State(): StateId = StateId

object StateId {
    fun <S> get(): State<S, S> = IndexedStateT.get(Id.applicative())
    fun <S, T> gets(f: (S) -> T): State<S, T> = IndexedStateT.gets(Id.applicative(), f)
    fun <S> modify(f: (S) -> S): State<S, Unit> = IndexedStateT.modify(Id.applicative(), f)
    fun <S> set(s: S): State<S, Unit> = IndexedStateT.set(Id.applicative(), s)

    fun <S> functor() = IndexedStateT.functor<IdHK, S>(Id.functor(), dummy = Unit)
    fun <S> applicative() = IndexedStateT.applicative<IdHK, S>(Id.monad(), dummy = Unit)
    fun <S> monad() = IndexedStateT.monad<IdHK, S>(Id.monad(), dummy = Unit)
}

fun <S, A> StateT<IdHK, S, A>.run(initial: S): Tuple2<S, A> = run(initial, Id.monad()).value()
fun <S, A> StateT<IdHK, S, A>.runA(s: S): A = run(s).b
fun <S, A> StateT<IdHK, S, A>.runS(s: S): S = run(s).a