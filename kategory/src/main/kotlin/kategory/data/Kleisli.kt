package kategory

typealias KleisliTKind<F, A, B> = HK3<Kleisli.F, F, A, B>
typealias KleisliF<F> = HK<Kleisli.F, F>
typealias KleisliFD<F, D> = HK2<Kleisli.F, F, D>
typealias KleisliFun<F, D, A> = (D) -> HK<F, A>
typealias ReaderT<F, D, A> = Kleisli<F, D, A>

fun <F, D, A> KleisliTKind<F, D, A>.ev(): Kleisli<F, D, A> =
        this as Kleisli<F, D, A>

class Kleisli<F, D, A>(val MF: Monad<F>, val run: KleisliFun<F, D, A>) : KleisliTKind<F, D, A> {
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

    infix fun <C> andThen(f: Kleisli<F, A, C>): Kleisli<F, D, C> =
            andThen(f.run)

    infix fun <B> andThen(f: (A) -> HK<F, B>): Kleisli<F, D, B> =
            Kleisli(MF, { MF.flatMap(run(it), f) })

    infix fun <B> andThen(a: HK<F, B>): Kleisli<F, D, B> =
            andThen({ a })

    companion object {

        inline operator fun <reified F, D, A> invoke(noinline run: KleisliFun<F, D, A>, MF: Monad<F> = monad<F>()): Kleisli<F, D, A> =
                Kleisli(MF, run)

        @JvmStatic inline fun <reified F, D, A> pure(x: A, MF: Monad<F> = monad<F>()): Kleisli<F, D, A> =
                Kleisli(MF, { _ -> MF.pure(x) })

        @JvmStatic inline fun <reified F, D> ask(MF: Monad<F> = monad<F>()): Kleisli<F, D, D> =
                Kleisli(MF, { MF.pure(it) })
    }

}

inline fun <reified F, D, A> Kleisli<F, D, Kleisli<F, D, A>>.flatten(): Kleisli<F, D, A> =
        flatMap({ it })

class KleisliInstances<F, D, E>(val FME: MonadError<F, E>) : KleisliMonadReader<F, D>, KleisliMonadError<F, D, E> {
    override fun FM(): Monad<F> = FME

    override fun FME(): MonadError<F, E> = FME
}

interface KleisliMonadReader<F, D> : MonadReader<KleisliFD<F, D>, D>, KleisliMonad<F, D> {

    override fun FM(): Monad<F>

    override fun ask(): Kleisli<F, D, D> =
            Kleisli(FM(), { FM().pure(it) })

    override fun <A> local(f: (D) -> D, fa: HK<KleisliFD<F, D>, A>): Kleisli<F, D, A> =
            fa.ev().local(f)
}

interface KleisliMonad<F, D> : Monad<KleisliFD<F, D>> {

    fun FM(): Monad<F>

    override fun <A, B> flatMap(fa: HK<KleisliFD<F, D>, A>, f: (A) -> HK<KleisliFD<F, D>, B>): Kleisli<F, D, B> =
            fa.ev().flatMap(f.andThen { it.ev() })

    override fun <A, B> map(fa: HK<KleisliFD<F, D>, A>, f: (A) -> B): Kleisli<F, D, B> =
            fa.ev().map(f)

    override fun <A, B> product(fa: HK<KleisliFD<F, D>, A>, fb: HK<KleisliFD<F, D>, B>): Kleisli<F, D, Tuple2<A, B>> =
            Kleisli(FM(), { FM().product(fa.ev().run(it), fb.ev().run(it)) })

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<KleisliFD<F, D>, Either<A, B>>): Kleisli<F, D, B> =
            Kleisli(FM(), { b -> FM().tailRecM(a, { f(it).ev().run(b) }) })

    override fun <A> pure(a: A): Kleisli<F, D, A> =
            Kleisli(FM(), { FM().pure(a) })
}

interface KleisliMonadError<F, D, E> : MonadError<KleisliFD<F, D>, E>, KleisliMonad<F, D> {

    fun FME(): MonadError<F, E>

    override fun <A> handleErrorWith(fa: HK<KleisliFD<F, D>, A>, f: (E) -> HK<KleisliFD<F, D>, A>): Kleisli<F, D, A> =
            Kleisli(FME(), {
                FME().handleErrorWith(fa.ev().run(it), { e: E -> f(e).ev().run(it) })
            })

    override fun <A> raiseError(e: E): Kleisli<F, D, A> =
            Kleisli(FME(), { FME().raiseError(e) })

}