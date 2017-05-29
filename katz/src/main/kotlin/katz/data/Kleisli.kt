package katz

typealias KleisiTKind<F, A, B> = HK3<Kleisli.F, F, A, B>
typealias KleisiF<F> = HK<Kleisli.F, F>

typealias KleisiFun<F, D, A> = (D) -> HK<F, A>

typealias ReaderT<F, D, A> = Kleisli<F, D, A>

fun <F, D, A> KleisiTKind<F, D, A>.ev(): Kleisli<F, D, A> =
        this as Kleisli<F, D, A>

class Kleisli<F, D, A>(val MF: Monad<F>, val run: KleisiFun<F, D, A>) : KleisiTKind<F, D, A> {
    class F private constructor()

    fun <B> map(f: (A) -> B): Kleisli<F, D, B> =
            Kleisli(MF, { a -> MF.map(run(a), f) })

    fun <B> flatMap(f: (A) -> Kleisli<F, D, B>): Kleisli<F, D, B> =
            Kleisli(MF, { d ->
                MF.flatMap(run(d)) { a -> f(a).run(d) }
            })

    fun <B> zip(o: Kleisli<F, D, B>): Kleisli<F, D, Tuple2<A, B>> =
            flatMap({ a ->
                o.map({ b -> Tuple2(a, b) })
            })

    fun <DD> local(f: (DD) -> D): Kleisli<F, DD, A> =
            Kleisli(MF, { dd -> run(f(dd)) })

    fun <B> andThen(f: (A) -> HK<F, B>): Kleisli<F, D, B> =
            Kleisli(MF, { MF.flatMap(run(it), f) })

    fun <B> andThen(a: HK<F, B>): Kleisli<F, D, B> =
            andThen({ a })

    companion object {

        inline operator fun <reified F, D, A> invoke(noinline run: KleisiFun<F, D, A>, MF: Monad<F> = monad<F>()): Kleisli<F, D, A> =
                Kleisli(MF, run)

        @JvmStatic inline fun <reified F, D, A> pure(x: A, MF: Monad<F> = monad<F>()): Kleisli<F, D, A> =
                Kleisli(MF, { _ -> MF.pure(x) })

        @JvmStatic inline fun <reified F, D> ask(MF: Monad<F> = monad<F>()): Kleisli<F, D, D> =
                Kleisli(MF, { MF.pure(it) })
    }

}

inline fun <reified F, D, A> Kleisli<F, D, Kleisli<F, D, A>>.flatten(): Kleisli<F, D, A> =
        flatMap({ it })
