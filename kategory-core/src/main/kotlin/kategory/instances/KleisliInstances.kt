package kategory

interface KleisliFunctorInstance<F, D>: Functor<KleisliKindPartial<F, D>> {

    fun FF(): Functor<F>

    override fun <A, B> map(fa: KleisliKind<F, D, A>, f: (A) -> B): Kleisli<F, D, B> = fa.ev().map(f, FF())
}

object KleisliFunctorInstanceImplicits {
    @JvmStatic
    fun <F, D> instance(FF: Functor<F>): KleisliFunctorInstance<F, D> = object : KleisliFunctorInstance<F, D> {
        override fun FF(): Functor<F> = FF
    }
}

interface KleisliApplicativeInstance<F, D> : KleisliFunctorInstance<F, D>, Applicative<KleisliKindPartial<F, D>> {

    fun AF(): Applicative<F>

    override fun <A> pure(a: A): Kleisli<F, D, A> = Kleisli({ AF().pure(a) })

    override fun <A, B> map(fa: KleisliKind<F, D, A>, f: (A) -> B): Kleisli<F, D, B> =
            fa.ev().map(f, AF())

    override fun <A, B> ap(fa: KleisliKind<F, D, A>, ff: KleisliKind<F, D,(A) -> B>): Kleisli<F, D, B> =
            fa.ev().ap(ff, AF())

    override fun <A, B> product(fa: KleisliKind<F, D, A>, fb: KleisliKind<F, D, B>): Kleisli<F, D, Tuple2<A, B>> =
            Kleisli({ AF().product(fa.ev().run(it), fb.ev().run(it)) })
}

object KleisliApplicativeInstanceImplicits {
    @JvmStatic
    fun <F, D> instance(AF: Applicative<F>): KleisliApplicativeInstance<F, D> = object : KleisliApplicativeInstance<F, D> {
        override fun FF(): Functor<F> = AF

        override fun AF(): Applicative<F> = AF
    }
}

interface KleisliMonadInstance<F, D> : KleisliApplicativeInstance<F, D>, Monad<KleisliKindPartial<F, D>> {

    fun MF(): Monad<F>

    override fun <A, B> flatMap(fa: KleisliKind<F, D, A>, f: (A) -> KleisliKind<F, D, B>): Kleisli<F, D, B> =
            fa.ev().flatMap(f.andThen { it.ev() }, MF())

    override fun <A, B> ap(fa: KleisliKind<F, D, A>, ff: KleisliKind<F, D,(A) -> B>): Kleisli<F, D, B> =
            fa.ev().ap(ff, AF())

    override fun <A, B> tailRecM(a: A, f: (A) -> KleisliKind<F, D, Either<A, B>>): Kleisli<F, D, B> =
            Kleisli.tailRecM(a, f, MF())

}

object KleisliMonadInstanceImplicits {
    @JvmStatic
    fun <F, D> instance(MF: Monad<F>): KleisliMonadInstance<F, D> = object : KleisliMonadInstance<F, D> {
        override fun FF(): Functor<F> = MF

        override fun AF(): Applicative<F> = MF

        override fun MF(): Monad<F> = MF
    }
}

interface KleisliMonadReaderInstance<F, D> : KleisliMonadInstance<F, D>, MonadReader<KleisliKindPartial<F, D>, D> {

    override fun ask(): Kleisli<F, D, D> = Kleisli({ MF().pure(it) })

    override fun <A> local(f: (D) -> D, fa: KleisliKind<F, D, A>): Kleisli<F, D, A> = fa.ev().local(f)

}

object KleisliMonadReaderInstanceImplicits {
    @JvmStatic
    fun <F, D> instance(MF: Monad<F>): KleisliMonadReaderInstance<F, D> = object : KleisliMonadReaderInstance<F, D> {
        override fun FF(): Functor<F> = MF

        override fun AF(): Applicative<F> = MF

        override fun MF(): Monad<F> = MF
    }
}

interface KleisliMonadErrorInstance<F, D, E> : MonadError<KleisliKindPartial<F, D>, E>, KleisliMonadInstance<F, D> {

    fun MFE(): MonadError<F, E>

    override fun <A> handleErrorWith(fa: KleisliKind<F, D, A>, f: (E) -> KleisliKind<F, D, A>): Kleisli<F, D, A> =
            fa.ev().handleErrorWith(f, MFE())

    override fun <A> raiseError(e: E): Kleisli<F, D, A> = Kleisli.raiseError(e, MFE())

}

object KleisliMonadErrorInstanceImplicits {
    @JvmStatic
    fun <F, D, E> instance(ME: MonadError<F, E>): KleisliMonadErrorInstance<F, D, E> = object : KleisliMonadErrorInstance<F, D, E> {
        override fun FF(): Functor<F> = ME

        override fun AF(): Applicative<F> = ME

        override fun MF(): Monad<F> = ME

        override fun MFE(): MonadError<F, E> = ME
    }
}
