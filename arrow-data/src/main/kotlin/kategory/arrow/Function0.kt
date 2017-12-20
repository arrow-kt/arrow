package arrow

fun <A> (() -> A).k(): Function0<A> = Function0(this)

operator fun <A> Function0Kind<A>.invoke(): A = this.ev().f()

@higherkind
@deriving(
        Functor::class,
        Applicative::class,
        Monad::class,
        Comonad::class,
        Bimonad::class)
data class Function0<out A>(internal val f: () -> A) : Function0Kind<A> {

    fun <B> map(f: (A) -> B): Function0<B> = Function0.pure(f(this()))

    fun <B> flatMap(ff: (A) -> Function0Kind<B>): Function0<B> = ff(f()).ev()

    fun <B> coflatMap(f: (Function0Kind<A>) -> B): Function0<B> = { f(this) }.k()

    fun <B> ap(ff: Function0Kind<(A) -> B>): Function0<B> = ff.flatMap { f -> map(f) }.ev()

    fun extract(): A = f()

    companion object {

        fun <A> pure(a: A): Function0<A> = { a }.k()

        tailrec fun <A, B> loop(a: A, f: (A) -> HK<Function0HK, Either<A, B>>): B {
            val fa = f(a).ev()()
            return when (fa) {
                is Right<A, B> -> fa.b
                is Left<A, B> -> loop(fa.a, f)
            }
        }

        fun <A, B> tailRecM(a: A, f: (A) -> HK<Function0HK, Either<A, B>>): Function0<B> = { loop(a, f) }.k()

    }
}