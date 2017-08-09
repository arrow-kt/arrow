package kategory

interface Function1Instances<I> :
        Functor<Function1F<I>>,
        Applicative<Function1F<I>>,
        Monad<Function1F<I>>,
        MonadReader<Function1F<I>, I> {

    override fun ask(): Function1<I, I> = { a: I -> a }.k()

    override fun <A> local(f: (I) -> I, fa: Function1Kind<I, A>): Function1<I, A> = f.andThen { fa(it) }.k()

    override fun <A> pure(a: A): Function1<I, A> = { _: I -> a }.k()

    override fun <A, B> map(fa: Function1Kind<I, A>, f: (A) -> B): Function1<I, B> = f.compose { a: I -> fa(a) }.k()

    override fun <A, B> flatMap(fa: Function1Kind<I, A>, f: (A) -> Function1Kind<I, B>): Function1<I, B> = { p: I -> f(fa(p))(p) }.k()

    tailrec private fun <A, B> step(a: A, t: I, fn: (A) -> Function1Kind<I, Either<A, B>>): B {
        val af = fn(a)(t)
        return when (af) {
            is Either.Right<A, B> -> af.b
            is Either.Left<A, B> -> step(af.a, t, fn)
        }
    }

    override fun <A, B> tailRecM(a: A, f: (A) -> Function1Kind<I, Either<A, B>>): Function1<I, B> = { t: I -> step(a, t, f) }.k()
}

