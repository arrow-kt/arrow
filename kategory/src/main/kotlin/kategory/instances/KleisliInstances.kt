package kategory

interface KleisliInstances<F, D> :
        Functor<KleisliFD<F, D>>,
        Applicative<KleisliFD<F, D>>,
        Monad<KleisliFD<F, D>>,
        MonadReader<KleisliFD<F, D>, D> {

    fun MF(): Monad<F>

    override fun ask(): Kleisli<F, D, D> =
            Kleisli(MF(), { MF().pure(it) })

    override fun <A> local(f: (D) -> D, fa: HK<KleisliFD<F, D>, A>): Kleisli<F, D, A> =
            fa.ev().local(f)

    override fun <A, B> flatMap(fa: HK<KleisliFD<F, D>, A>, f: (A) -> HK<KleisliFD<F, D>, B>): Kleisli<F, D, B> =
            fa.ev().flatMap(f.andThen { it.ev() })

    override fun <A, B> map(fa: HK<KleisliFD<F, D>, A>, f: (A) -> B): Kleisli<F, D, B> =
            fa.ev().map(f)

    override fun <A, B> product(fa: HK<KleisliFD<F, D>, A>, fb: HK<KleisliFD<F, D>, B>): Kleisli<F, D, Tuple2<A, B>> =
            Kleisli(MF(), { MF().product(fa.ev().run(it), fb.ev().run(it)) })

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<KleisliFD<F, D>, Either<A, B>>): Kleisli<F, D, B> =
            Kleisli(MF(), { b -> MF().tailRecM(a, { f(it).ev().run(b) }) })

    override fun <A> pure(a: A): Kleisli<F, D, A> =
            Kleisli(MF(), { MF().pure(a) })
}

interface KleisliMonadError<F, D, E> : MonadError<KleisliFD<F, D>, E>, KleisliInstances<F, D> {

    fun MFE(): MonadError<F, E>

    override fun <A> handleErrorWith(fa: HK<KleisliFD<F, D>, A>, f: (E) -> HK<KleisliFD<F, D>, A>): Kleisli<F, D, A> =
            Kleisli(MFE(), {
                MFE().handleErrorWith(fa.ev().run(it), { e: E -> f(e).ev().run(it) })
            })

    override fun <A> raiseError(e: E): Kleisli<F, D, A> =
            Kleisli(MFE(), { MFE().raiseError(e) })

}

