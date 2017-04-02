package katz

class Kleisli<F, D, A>(val run: (D) -> HK<F, A>) {

    companion object Factory {

        fun <F, D, A> pure(x: A, f: Applicative<F>): Kleisli<F, D, A> = Kleisli { _ -> f.pure(x) }

        fun <F, D> ask(f: Applicative<F>): Kleisli<F, D, D> = Kleisli { f.pure(it) }
    }

    fun <B> map(ft: Functor<F>, f: (A) -> B): Kleisli<F, D, B> = Kleisli { a -> ft.map(run(a), f) }

    fun <B> flatMap(m: Monad<F>, f: (A) -> Kleisli<F, D, B>): Kleisli<F, D, B> =
            Kleisli { d ->
                m.flatMap(run(d)) { a -> f(a).run(d) }
            }

    fun <B> zip(m: Monad<F>, o: Kleisli<F, D, B>): Kleisli<F, D, Pair<A, B>> =
            this.flatMap(m) { a ->
                o.map(m) { b -> Pair(a, b) }
            }

    fun <DD> local(f: (DD) -> D): Kleisli<F, DD, A> = Kleisli { dd -> run(f(dd)) }
}

fun <F, D, A> Kleisli<F, D, Kleisli<F, D, A>>.flatten(m: Monad<F>): Kleisli<F, D, A> =
        flatMap(m) { it }

typealias ReaderT<F, D, A> = Kleisli<F, D, A>
