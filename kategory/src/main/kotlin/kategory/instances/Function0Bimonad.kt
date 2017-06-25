package kategory

fun <A> (() -> A).k(): HK<Function0.F, A> =
        Function0(this)

fun <A> HK<Function0.F, A>.ev(): () -> A =
        (this as Function0<A>).f

// We don't we want an inherited class to avoid equivalence issues, so a simple HK wrapper will do
data class Function0<out A>(private val _f: () -> A) : HK<Function0.F, A> {

    private val memoized: A by lazy(_f)

    internal val f = { memoized }

    class F private constructor()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Function0<*>

        if (memoized != other.memoized) return false

        return true
    }

    override fun hashCode(): Int =
            memoized?.hashCode() ?: 0

    companion object : Bimonad<Function0.F>, GlobalInstance<Bimonad<Function0.F>>() {

        override fun <A, B> flatMap(fa: HK<Function0.F, A>, f: (A) -> HK<Function0.F, B>): HK<Function0.F, B> =
                f(fa.ev().invoke())

        override fun <A, B> coflatMap(fa: HK<Function0.F, A>, f: (HK<Function0.F, A>) -> B): HK<Function0.F, B> =
                { f(fa) }.k()

        override fun <A> pure(a: A): HK<Function0.F, A> =
                { a }.k()

        override fun <A> extract(fa: HK<Function0.F, A>): A =
                fa.ev().invoke()

        override fun <A, B> map(fa: HK<F, A>, f: (A) -> B): HK<F, B> =
                pure(f(fa.ev().invoke()))

        override fun <A, B> tailRecM(a: A, f: (A) -> HK<F, Either<A, B>>): HK<F, B> =
                f(a).ev().invoke().let { either ->
                    when (either) {
                        is Either.Left -> tailRecM(either.a, f)
                        is Either.Right -> ({ either.b }).k()
                    }
                }
    }
}