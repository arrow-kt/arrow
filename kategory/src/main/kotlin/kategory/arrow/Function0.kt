package kategory

fun <A> (() -> A).k(): Function0<A> =
        Function0(this)

fun <A> HK<Function0.F, A>.ev(): () -> A =
        (this as Function0<A>).f

operator fun <A> HK<Function0.F, A>.invoke(): A =
        (this as Function0<A>).f()

// We don't want an inherited class to avoid equivalence issues, so a simple HK wrapper will do
data class Function0<out A>(internal val f: () -> A) : HK<Function0.F, A> {
    class F private constructor()

    companion object : Bimonad<Function0.F>, GlobalInstance<Bimonad<Function0.F>>() {

        override fun <A, B> flatMap(fa: HK<Function0.F, A>, f: (A) -> HK<Function0.F, B>): Function0<B> =
                Function0(f(fa()).ev())

        override fun <A, B> coflatMap(fa: HK<Function0.F, A>, f: (HK<Function0.F, A>) -> B): Function0<B> =
                { f(fa) }.k()

        override fun <A> pure(a: A): Function0<A> =
                { a }.k()

        override fun <A> extract(fa: HK<Function0.F, A>): A =
                fa()

        override fun <A, B> map(fa: HK<F, A>, f: (A) -> B): Function0<B> =
                pure(f(fa()))

        tailrec fun <A, B> loop(a: A, f: (A) -> HK<F, Either<A, B>>) : B {
            val fa = f(a).ev()()
            return when(fa) {
                is Either.Right<A, B> -> fa.b
                is Either.Left<A, B> -> loop(fa.a, f)
            }
        }

        override fun <A, B> tailRecM(a: A, f: (A) -> HK<F, Either<A, B>>): Function0<B> =
                { loop(a, f) }.k()

        fun bimonad(): Bimonad<Function0.F> = this

        fun monad(): Monad<Function0.F> = this

        fun comonad(): Comonad<Function0.F> = this
    }
}