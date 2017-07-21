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
        fun <P> functor() = object : Function1Instances<P> {}

        fun <P> applicative() = object : Function1Instances<P> {}

        fun <P> monad() = object : Function1Instances<P> {}

        fun <P> monadReader() = object : Function1Instances<P> {}
    }
}