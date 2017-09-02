package kategory

object State {
    operator fun <S, A> invoke(run: (S) -> Tuple2<S, A>, MF: Monad<IdHK> = Id.monad()): StateT<IdHK, S, A> = StateT(MF, Id(run.andThen { Id(it) }))
}

fun <S, A> ((S) -> Tuple2<S, A>).state() : StateT<IdHK, S, A> = State(this, Id.monad())