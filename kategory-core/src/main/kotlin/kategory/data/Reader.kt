package kategory

infix fun <A, B, C> ((B) -> C).compose(f: (A) -> B): (A) -> C = { a: A -> this(f(a)) }

infix fun <A, B, C> ((A) -> B).andThen(g: (B) -> C): (A) -> C = { a: A -> g(this(a)) }

fun <D, A> ((D) -> A).reader(): ReaderT<IdHK, D, A> = Reader(this, Id.monad())

fun <D, A> ReaderT<IdHK, D, A>.runId(d: D): A = this.run(d).value()

object Reader {

    operator fun <D, A> invoke(run: (D) -> A, MF: Monad<IdHK> = Id.monad()): ReaderT<IdHK, D, A> = Kleisli(run.andThen { Id(it) }, MF)

    fun <D, A> pure(x: A, MF: Monad<IdHK> = Id.monad()): ReaderT<IdHK, D, A> = Kleisli.pure<IdHK, D, A>(x, MF)

    fun <D> ask(MF: Monad<IdHK> = Id.monad()): ReaderT<IdHK, D, D> = Kleisli.ask<IdHK, D>(MF)

}