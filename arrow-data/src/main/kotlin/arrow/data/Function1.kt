package arrow

fun <I, O> ((I) -> O).k(): Function1<I, O> = Function1(this)

operator fun <I, O> Function1Kind<I, O>.invoke(i: I): O = this.ev().f(i)

@higherkind class Function1<I, out O>(val f: (I) -> O) : Function1Kind<I, O> {

    fun <B> map(f: (O) -> B): Function1<I, B> = f.compose { a: I -> this.f(a) }.k()

    fun <B> flatMap(f: (O) -> Function1Kind<I, B>): Function1<I, B> = { p: I -> f(this.f(p))(p) }.k()

    fun <B> ap(ff: Function1Kind<I, (O) -> B>): Function1<I, B> = ff.ev().flatMap { f -> map(f) }.ev()

    fun local(f: (I) -> I): Function1<I, O> = f.andThen { this(it) }.k()

    companion object {

        fun <I> ask(): Function1<I, I> = { a: I -> a }.k()

        fun <I, A> pure(a: A): Function1<I, A> = { _: I -> a }.k()

        tailrec private fun <I, A, B> step(a: A, t: I, fn: (A) -> Function1Kind<I, Either<A, B>>): B {
            val af = fn(a)(t)
            return when (af) {
                is Right<A, B> -> af.b
                is Left<A, B> -> step(af.a, t, fn)
            }
        }

        fun <I, A, B> tailRecM(a: A, f: (A) -> Function1Kind<I, Either<A, B>>): Function1<I, B> = { t: I -> step(a, t, f) }.k()
    }
}
