package kategory

typealias Function1F<P> = HK<Function1.F, P>

fun <P, R> ((P) -> R).k(): Function1<P, R> =
        Function1(this)

@Suppress("UNCHECKED_CAST")
fun <R> Function1F<R>.ev(): FunctionInject<R> =
        this as FunctionInject<R>

// We don't we want an inherited class to avoid equivalence issues, so a simple HK wrapper will do
data class Function1<in A, out R>(val f: (A) -> R) : FunctionInject<R>, Function1F<R> {
    @Suppress("UNCHECKED_CAST")
    override operator fun <P> invoke(p: P): R =
            f(p as A)

    class F private constructor()

    companion object {
        fun <P> monad() = object : Function1MonadReader<P> {}

        fun <P> monadReader() = object : Function1MonadReader<P> {}
    }
}

interface Function1MonadReader<P> : MonadReader<Function1.F, P> {

    override fun ask(): HK<Function1.F, P> =
            { a: P -> a }.k()

    override fun <A> local(f: (P) -> P, fa: HK<Function1.F, A>): Function1<P, A> =
            f.andThen { fa.ev().invoke(it) }.k()

    override fun <A> pure(a: A): Function1<P, A> =
            { _: P -> a }.k()

    override fun <A, B> map(fa: HK<Function1.F, A>, f: (A) -> B): HK<Function1.F, B> =
            f.compose { b: B -> fa.ev().invoke(b) }.k()

    override fun <A, B> flatMap(fa: HK<Function1.F, A>, f: (A) -> HK<Function1.F, B>): Function1<P, B> =
            Function1 { p -> f(fa.ev().invoke(p)).ev().invoke(p) }

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<Function1.F, Either<A, B>>): Function1<P, B> =
            Function1 { p ->
                tailrec fun loop(thisA: A): B =
                        f(thisA).ev().invoke(p).fold({ loop(it) }, { it })

                loop(a)
            }

    fun monad(): Monad<Function1.F> = this

    fun monadReader(): MonadReader<Function1.F, P> = this
}