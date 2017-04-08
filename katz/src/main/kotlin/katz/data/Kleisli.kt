package katz

typealias ReaderT<F, D, A> = Kleisli<F, D, A>

class Kleisli<F, D, A>(val MF: Monad<F>, val run: (D) -> HK<F, A>) {

    fun <B> map(f: (A) -> B): Kleisli<F, D, B> = Kleisli(MF, { a -> MF.map(run(a), f) })

    fun <B> flatMap(f: (A) -> Kleisli<F, D, B>): Kleisli<F, D, B> =
            Kleisli(MF, { d ->
                MF.flatMap(run(d)) { a -> f(a).run(d) }
            })

    inline fun <reified F, D, A> Kleisli<F, D, Kleisli<F, D, A>>.flatten(): Kleisli<F, D, A> =
            flatMap({ it })

    fun <B> zip(o: Kleisli<F, D, B>): Kleisli<F, D, Pair<A, B>> =
            flatMap({ a ->
                o.map({ b -> Pair(a, b) })
            })

    fun <DD> local(f: (DD) -> D): Kleisli<F, DD, A> = Kleisli(MF, { dd -> run(f(dd)) })

    companion object {

        inline operator fun <reified F, D, A> invoke(MF: Monad<F> = monad<F>(), noinline run: (D) -> HK<F, A>): Kleisli<F, D, A> = Kleisli(MF, run)

        inline fun <reified F, D, A> pure(MF: Monad<F> = monad<F>(), x: A): Kleisli<F, D, A> = Kleisli(MF, { _ -> MF.pure(x) })

        inline fun <reified F, D> ask(MF: Monad<F> = monad<F>()): Kleisli<F, D, D> = Kleisli(MF, { MF.pure(it) })
    }

}