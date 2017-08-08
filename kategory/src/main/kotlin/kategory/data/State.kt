package kategory

object State {
    operator fun <S, A> invoke(run: (S) -> Tuple2<S, A>, MF: Monad<Id.F> = Id): StateT<Id.F, S, A> = StateT(MF, Id(run.andThen { Id(it) }))
}

fun <S, A> ((S) -> Tuple2<S, A>).state() : StateT<Id.F, S, A> = State(this, Id)