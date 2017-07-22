package kategory

typealias Function1F<R> = HK<Function1.F, R>

fun <P, R> ((P) -> R).k(): Function1<P, R> =
        Function1(this)

@Suppress("UNCHECKED_CAST")
fun <P, R> Function1F<R>.ev(): (P) -> R =
        (this as Function1<P, R>).f

fun <P, R> Function1F<R>.invoke(p: P): R =
        this.ev<P, R>().invoke(p)

// We don't want an inherited class to avoid equivalence issues, so a simple HK wrapper will do
data class Function1<in A, out R>(val f: (A) -> R) : Function1F<R> {
    class F private constructor()

    operator fun invoke(a: A): R =
            f(a)

    companion object {
        fun <P> functor() = object : Function1Instances<P> {}

        fun <P> applicative() = object : Function1Instances<P> {}

        fun <P> monad() = object : Function1Instances<P> {}

        fun <P> monadReader() = object : Function1Instances<P> {}
    }
}