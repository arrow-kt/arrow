package katz

class Kleisli<F, D, A>(inline val run: (D) -> HK<F, A>) {

    companion object Factory {

        fun <F, D, A> pure(x: A): Kleisli<F, D, A> = Kleisli { _ -> instance<Applicative<F>>().pure(x) }

        fun <F, D> ask(): Kleisli<F, D, D> = Kleisli { instance<Applicative<F>>().pure(it) }
    }

    inline fun <B> map(noinline f: (A) -> B): Kleisli<F, D, B> = Kleisli { a -> instance<Functor<F>>().map(run(a), f) }

    inline fun <B> flatMap(crossinline f: (A) -> Kleisli<F, D, B>): Kleisli<F, D, B> =
            Kleisli { d ->
                instance<Monad<F>>().flatMap(run(d)) { a -> f(a).run(d) }
            }

    fun <B> zip(o: Kleisli<F, D, B>): Kleisli<F, D, Pair<A, B>> =
            this.flatMap { a ->
                o.map { b -> Pair(a, b) }
            }

    fun <DD> local(f: (DD) -> D): Kleisli<F, DD, A> = Kleisli { dd -> run(f(dd)) }
}

fun <F, D, A> Kleisli<F, D, Kleisli<F, D, A>>.flatten(): Kleisli<F, D, A> =
        flatMap { it }

typealias ReaderT<F, D, A> = Kleisli<F, D, A>
