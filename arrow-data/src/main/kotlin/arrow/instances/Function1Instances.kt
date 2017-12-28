package arrow

@instance(Function1::class)
interface Function1FunctorInstance<I> : Functor<Function1KindPartial<I>> {
    override fun <A, B> map(fa: Function1Kind<I, A>, f: (A) -> B): Function1<I, B> =
            fa.ev().map(f)
}

@instance(Function1::class)
interface Function1ApplicativeInstance<I> : Function1FunctorInstance<I>, Applicative<Function1KindPartial<I>> {

    override fun <A> pure(a: A): Function1<I, A> =
            Function1.pure(a)

    override fun <A, B> map(fa: Function1Kind<I, A>, f: (A) -> B): Function1<I, B> =
            fa.ev().map(f)

    override fun <A, B> ap(fa: Function1Kind<I, A>, ff: Function1Kind<I, (A) -> B>): Function1<I, B> =
            fa.ev().ap(ff)
}

@instance(Function1::class)
interface Function1MonadInstance<I> : Function1ApplicativeInstance<I>, Monad<Function1KindPartial<I>> {

    override fun <A, B> map(fa: Function1Kind<I, A>, f: (A) -> B): Function1<I, B> =
            fa.ev().map(f)

    override fun <A, B> ap(fa: Function1Kind<I, A>, ff: Function1Kind<I, (A) -> B>): Function1<I, B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: Function1Kind<I, A>, f: (A) -> Function1Kind<I, B>): Function1<I, B> =
            fa.ev().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: (A) -> Function1Kind<I, Either<A, B>>): Function1<I, B> =
            Function1.tailRecM(a, f)
}

@instance(Function1::class)
interface Function1MonadReaderInstance<I> : Function1MonadInstance<I>, MonadReader<Function1KindPartial<I>, I> {

    override fun ask(): Function1<I, I> = Function1.ask()

    override fun <A> local(f: (I) -> I, fa: Function1Kind<I, A>): Function1<I, A> = fa.ev().local(f)
}
