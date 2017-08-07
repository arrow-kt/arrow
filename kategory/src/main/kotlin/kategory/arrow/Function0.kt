package kategory

fun <A> (() -> A).k(): Function0<A> =
        Function0(this)

operator fun <A> HK<Function0HK, A>.invoke(): A =
        (this as Function0<A>).f()

// We don't want an inherited class to avoid equivalence issues, so a simple HK wrapper will do
@higherkind data class Function0<out A>(internal val f: () -> A) : Function0Kind<A> {

    companion object : Bimonad<Function0HK>, GlobalInstance<Bimonad<Function0HK>>() {

        override fun <A, B> flatMap(fa: HK<Function0HK, A>, f: (A) -> HK<Function0HK, B>): Function0<B> =
                Function0(f(fa()).ev().f)

        override fun <A, B> coflatMap(fa: HK<Function0HK, A>, f: (HK<Function0HK, A>) -> B): Function0<B> =
                { f(fa) }.k()

        override fun <A> pure(a: A): Function0<A> =
                { a }.k()

        override fun <A> extract(fa: HK<Function0HK, A>): A =
                fa()

        override fun <A, B> map(fa: HK<Function0HK, A>, f: (A) -> B): Function0<B> =
                pure(f(fa()))

        tailrec fun <A, B> loop(a: A, f: (A) -> HK<Function0HK, Either<A, B>>): B {
            val fa = f(a).ev()()
            return when (fa) {
                is Either.Right<A, B> -> fa.b
                is Either.Left<A, B> -> loop(fa.a, f)
            }
        }

        override fun <A, B> tailRecM(a: A, f: (A) -> HK<Function0HK, Either<A, B>>): Function0<B> =
                { loop(a, f) }.k()

        fun bimonad(): Bimonad<Function0HK> = this

        fun monad(): Monad<Function0HK> = this

        fun comonad(): Comonad<Function0HK> = this
    }
}