package arrow

@instance(Function0::class)
interface Function0FunctorInstance : arrow.Functor<Function0HK> {
    override fun <A, B> map(fa: arrow.Function0Kind<A>, f: kotlin.Function1<A, B>): arrow.Function0<B> =
            fa.ev().map(f)
}

@instance(Function0::class)
interface Function0ApplicativeInstance : arrow.Applicative<Function0HK> {
    override fun <A, B> ap(fa: arrow.Function0Kind<A>, ff: arrow.Function0Kind<kotlin.Function1<A, B>>): arrow.Function0<B> =
            fa.ev().ap(ff)

    override fun <A, B> map(fa: arrow.Function0Kind<A>, f: kotlin.Function1<A, B>): arrow.Function0<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): arrow.Function0<A> =
            arrow.Function0.pure(a)
}

@instance(Function0::class)
interface Function0MonadInstance : arrow.Monad<Function0HK> {
    override fun <A, B> ap(fa: arrow.Function0Kind<A>, ff: arrow.Function0Kind<kotlin.Function1<A, B>>): arrow.Function0<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: arrow.Function0Kind<A>, f: kotlin.Function1<A, arrow.Function0Kind<B>>): arrow.Function0<B> =
            fa.ev().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, arrow.Function0Kind<arrow.Either<A, B>>>): arrow.Function0<B> =
            arrow.Function0.tailRecM(a, f)

    override fun <A, B> map(fa: arrow.Function0Kind<A>, f: kotlin.Function1<A, B>): arrow.Function0<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): arrow.Function0<A> =
            arrow.Function0.pure(a)
}

@instance(Function0::class)
interface Function0ComonadInstance : arrow.Comonad<Function0HK> {
    override fun <A, B> coflatMap(fa: arrow.Function0Kind<A>, f: kotlin.Function1<arrow.Function0Kind<A>, B>): arrow.Function0<B> =
            fa.ev().coflatMap(f)

    override fun <A> extract(fa: arrow.Function0Kind<A>): A =
            fa.ev().extract()

    override fun <A, B> map(fa: arrow.Function0Kind<A>, f: kotlin.Function1<A, B>): arrow.Function0<B> =
            fa.ev().map(f)
}

@instance(Function0::class)
interface Function0BimonadInstance : arrow.Bimonad<Function0HK> {
    override fun <A, B> ap(fa: arrow.Function0Kind<A>, ff: arrow.Function0Kind<kotlin.Function1<A, B>>): arrow.Function0<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: arrow.Function0Kind<A>, f: kotlin.Function1<A, arrow.Function0Kind<B>>): arrow.Function0<B> =
            fa.ev().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, arrow.Function0Kind<arrow.Either<A, B>>>): arrow.Function0<B> =
            arrow.Function0.tailRecM(a, f)

    override fun <A, B> map(fa: arrow.Function0Kind<A>, f: kotlin.Function1<A, B>): arrow.Function0<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): arrow.Function0<A> =
            arrow.Function0.pure(a)

    override fun <A, B> coflatMap(fa: arrow.Function0Kind<A>, f: kotlin.Function1<arrow.Function0Kind<A>, B>): arrow.Function0<B> =
            fa.ev().coflatMap(f)

    override fun <A> extract(fa: arrow.Function0Kind<A>): A =
            fa.ev().extract()
}
