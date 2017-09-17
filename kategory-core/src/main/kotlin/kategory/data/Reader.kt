package kategory

infix fun <A, B, C> ((B) -> C).compose(f: (A) -> B): (A) -> C = { a: A -> this(f(a)) }

infix fun <A, B, C> ((A) -> B).andThen(g: (B) -> C): (A) -> C = { a: A -> g(this(a)) }

fun <D, A> ((D) -> A).reader(): ReaderT<IdHK, D, A> = Reader(this)

fun <D, A> ReaderT<IdHK, D, A>.runId(d: D): A = this.run(d).value()

fun <D, A, B> ReaderT<IdHK, D, A>.map(f: (A) -> B): ReaderT<IdHK, D, B> = map(f, Id.functor())

fun <D, A, B> ReaderT<IdHK, D, A>.flatMap(f: (A) -> ReaderT<IdHK, D, B>): ReaderT<IdHK, D, B> = flatMap(f, Id.monad())

fun <D, A, B> ReaderT<IdHK, D, A>.ap(ff: KleisliKind<IdHK, D, (A) -> B>): ReaderT<IdHK, D, B> = ap(ff, Id.applicative())

fun <D, A, B> ReaderT<IdHK, D, A>.zip(o: ReaderT<IdHK, D, B>): ReaderT<IdHK, D, Tuple2<A, B>> = zip(o, Id.monad())

fun <D, A, C> ReaderT<IdHK, D, A>.andThen(f: ReaderT<IdHK, A, C>): ReaderT<IdHK, D, C> = andThen(f, Id.monad())

fun <D, A, B> ReaderT<IdHK, D, A>.andThen(f: (A) -> HK<IdHK, B>): ReaderT<IdHK, D, B> = andThen(f, Id.monad())

fun <D, A, B> ReaderT<IdHK, D, A>.andThen(a: HK<IdHK, B>): ReaderT<IdHK, D, B> = andThen(a, Id.monad())

object Reader {

    operator fun <D, A> invoke(run: (D) -> A): ReaderT<IdHK, D, A> = Kleisli(run.andThen { Id(it) })

    fun <D, A> pure(x: A, MF: Monad<IdHK> = Id.monad()): ReaderT<IdHK, D, A> = Kleisli.pure<IdHK, D, A>(x, MF)

    fun <D> ask(MF: Monad<IdHK> = Id.monad()): ReaderT<IdHK, D, D> = Kleisli.ask<IdHK, D>(MF)

}
