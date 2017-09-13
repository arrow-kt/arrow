package kategory

typealias KleisliFun<F, D, A> = (D) -> HK<F, A>
typealias ReaderT<F, D, A> = Kleisli<F, D, A>

@higherkind
class Kleisli<F, D, A>(val run: KleisliFun<F, D, A>) : KleisliKind<F, D, A> {

    fun <B> ap(ff: KleisliKind<F, D, (A) -> B>, AF: Applicative<F>): Kleisli<F, D, B> =
            Kleisli({ AF.ap(run(it), ff.ev().run(it)) })

    fun <B> map(f: (A) -> B, FF: Functor<F>): Kleisli<F, D, B> = Kleisli({ a -> FF.map(run(a), f) })

    fun <B> flatMap(f: (A) -> Kleisli<F, D, B>, MF: Monad<F>): Kleisli<F, D, B> =
            Kleisli({ d ->
                MF.flatMap(run(d)) { a -> f(a).run(d) }
            })

    fun <B> zip(o: Kleisli<F, D, B>, MF: Monad<F>): Kleisli<F, D, Tuple2<A, B>> =
            flatMap({ a ->
                o.map({ b -> Tuple2(a, b) }, MF)
            }, MF)

    fun <DD> local(f: (DD) -> D): Kleisli<F, DD, A> = Kleisli({ dd -> run(f(dd)) })

    fun <C> andThen(f: Kleisli<F, A, C>, MF: Monad<F>): Kleisli<F, D, C> = andThen(f.run, MF)

    fun <B> andThen(f: (A) -> HK<F, B>, MF: Monad<F>): Kleisli<F, D, B> = Kleisli({ MF.flatMap(run(it), f) })

    fun <B> andThen(a: HK<F, B>, MF: Monad<F>): Kleisli<F, D, B> = andThen({ a }, MF)

    fun <E> handleErrorWith(f: (E) -> KleisliKind<F, D, A>, ME: MonadError<F, E>): Kleisli<F, D, A> =
            Kleisli({
                ME.handleErrorWith(run(it), { e: E -> f(e).ev().run(it) })
            })

    companion object {

        operator fun <F, D, A> invoke(run: KleisliFun<F, D, A>): Kleisli<F, D, A> = Kleisli(run)

        fun <F, D, A, B> tailRecM(a: A, f: (A) -> KleisliKind<F, D, Either<A, B>>, MF: Monad<F>): Kleisli<F, D, B> =
                Kleisli({ b -> MF.tailRecM(a, { f(it).ev().run(b) }) })

        @JvmStatic inline fun <reified F, D, A> pure(x: A, AF: Applicative<F> = applicative<F>()): Kleisli<F, D, A> = Kleisli({ _ -> AF.pure(x) })

        @JvmStatic inline fun <reified F, D> ask(AF: Applicative<F> = applicative<F>()): Kleisli<F, D, D> = Kleisli({ AF.pure(it) })

        fun <F, D, E, A> raiseError(e: E, ME: MonadError<F, E>): Kleisli<F, D, A> = Kleisli({ ME.raiseError(e) })

        inline fun <reified F, D> functor(FF: Functor<F> = functor<F>()): KleisliFunctorInstance<F, D> =
                KleisliFunctorInstanceImplicits.instance(FF)

        inline fun <reified F, D> applicative(AF: Applicative<F> = applicative<F>()): KleisliApplicativeInstance<F, D> =
                KleisliApplicativeInstanceImplicits.instance(AF)

        inline fun <reified F, D> monad(MF: Monad<F> = monad<F>()): KleisliMonadInstance<F, D> =
                KleisliMonadInstanceImplicits.instance(MF)

        inline fun <reified F, D> monadReader(MF: Monad<F> = monad<F>()): KleisliMonadReaderInstance<F, D> =
                KleisliMonadReaderInstanceImplicits.instance(MF)

        inline fun <reified F, D, reified E> monadError(ME: MonadError<F, E> = monadError<F, E>()): KleisliMonadErrorInstance<F, D, E> =
                KleisliMonadErrorInstanceImplicits.instance(ME)
    }

}

inline fun <reified F, D, A> Kleisli<F, D, Kleisli<F, D, A>>.flatten(MF: Monad<F>): Kleisli<F, D, A> = flatMap({ it }, MF)

fun <F, D, A> KleisliFun<F, D, A>.kleisli(): Kleisli<F, D, A> = Kleisli(this)
