package arrow

@instance(Kleisli::class)
interface KleisliFunctorInstance<F, D> : Functor<KleisliKindPartial<F, D>> {

    fun FF(): Functor<F>

    override fun <A, B> map(fa: KleisliKind<F, D, A>, f: (A) -> B): Kleisli<F, D, B> = fa.ev().map(f, FF())
}

@instance(Kleisli::class)
interface KleisliApplicativeInstance<F, D> : KleisliFunctorInstance<F, D>, Applicative<KleisliKindPartial<F, D>> {

    override fun FF(): Applicative<F>

    override fun <A> pure(a: A): Kleisli<F, D, A> = Kleisli({ FF().pure(a) })

    override fun <A, B> map(fa: KleisliKind<F, D, A>, f: (A) -> B): Kleisli<F, D, B> =
            fa.ev().map(f, FF())

    override fun <A, B> ap(fa: KleisliKind<F, D, A>, ff: KleisliKind<F, D, (A) -> B>): Kleisli<F, D, B> =
            fa.ev().ap(ff, FF())

    override fun <A, B> product(fa: KleisliKind<F, D, A>, fb: KleisliKind<F, D, B>): Kleisli<F, D, Tuple2<A, B>> =
            Kleisli({ FF().product(fa.ev().run(it), fb.ev().run(it)) })
}

@instance(Kleisli::class)
interface KleisliMonadInstance<F, D> : KleisliApplicativeInstance<F, D>, Monad<KleisliKindPartial<F, D>> {

    override fun FF(): Monad<F>

    override fun <A, B> flatMap(fa: KleisliKind<F, D, A>, f: (A) -> KleisliKind<F, D, B>): Kleisli<F, D, B> =
            fa.ev().flatMap(f.andThen { it.ev() }, FF())

    override fun <A, B> ap(fa: KleisliKind<F, D, A>, ff: KleisliKind<F, D, (A) -> B>): Kleisli<F, D, B> =
            fa.ev().ap(ff, FF())

    override fun <A, B> tailRecM(a: A, f: (A) -> KleisliKind<F, D, Either<A, B>>): Kleisli<F, D, B> =
            Kleisli.tailRecM(a, f, FF())

}

@instance(Kleisli::class)
interface KleisliMonadReaderInstance<F, D> : KleisliMonadInstance<F, D>, MonadReader<KleisliKindPartial<F, D>, D> {

    override fun ask(): Kleisli<F, D, D> = Kleisli({ FF().pure(it) })

    override fun <A> local(f: (D) -> D, fa: KleisliKind<F, D, A>): Kleisli<F, D, A> = fa.ev().local(f)

}

@instance(Kleisli::class)
interface KleisliMonadErrorInstance<F, D, E> : MonadError<KleisliKindPartial<F, D>, E>, KleisliMonadInstance<F, D> {

    override fun FF(): MonadError<F, E>

    override fun <A> handleErrorWith(fa: KleisliKind<F, D, A>, f: (E) -> KleisliKind<F, D, A>): Kleisli<F, D, A> =
            fa.ev().handleErrorWith(f, FF())

    override fun <A> raiseError(e: E): Kleisli<F, D, A> = Kleisli.raiseError(e, FF())

}
