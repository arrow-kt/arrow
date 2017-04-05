package katz

class Kleisli<F, D, A> (val run: (D) -> HK<F, A>, val MF: Monad<F>) {

    fun <B> map(f: (A) -> B): Kleisli<F, D, B> = Kleisli({ a -> MF.map(run(a), f) }, MF)

    fun <B> flatMap(f: (A) -> Kleisli<F, D, B>): Kleisli<F, D, B> =
            Kleisli({ d ->
                MF.flatMap(run(d)) { a -> f(a).run(d) }
            }, MF)

    fun <B> zip(o: Kleisli<F, D, B>): Kleisli<F, D, Pair<A, B>> =
            this.flatMap { a ->
                o.map { b -> Pair(a, b) }
            }

    fun <DD> local(f: (DD) -> D): Kleisli<F, DD, A> = Kleisli({ dd -> run(f(dd)) }, MF)
}

object KleisliC {

    inline operator fun <reified F, D, A> invoke(noinline run: (D) -> HK<F, A>, MF : Monad<F> = instance<Monad<F>>()): Kleisli<F, D, A> = Kleisli(run, MF)

    inline fun <reified F, D, A> pure(x: A, MF: Monad<F> = monad<F>()): Kleisli<F, D, A> {
        return KleisliC({ _ -> MF.pure(x) })
    }

    inline fun <reified F, D> ask(AP : Applicative<F> = instance<Applicative<F>>()): Kleisli<F, D, D> = KleisliC({ AP.pure(it) })
}

fun <F, D, A> Kleisli<F, D, Kleisli<F, D, A>>.flatten(): Kleisli<F, D, A> =
        flatMap { it }

typealias ReaderT<F, D, A> = Kleisli<F, D, A>
