package arrow.core

import arrow.higherkind

fun <A> IdOf<A>.value(): A = this.fix().value

@higherkind
data class Id<out A>(val value: A) : IdOf<A> {

    inline fun <B> map(f: (A) -> B): Id<B> = Id(f(value))

    inline fun <B> flatMap(f: (A) -> IdOf<B>): Id<B> = f(value).fix()

    fun <B> foldLeft(b: B, f: (B, A) -> B): B = f(b, this.fix().value)

    fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> = f(this.fix().value, lb)

    fun <B> coflatMap(f: (IdOf<A>) -> B): Id<B> = this.fix().map({ f(this) })

    fun extract(): A = this.fix().value

    fun <B> ap(ff: IdOf<(A) -> B>): Id<B> = ff.fix().flatMap { f -> map(f) }.fix()

    companion object {

        tailrec fun <A, B> tailRecM(a: A, f: (A) -> IdOf<Either<A, B>>): Id<B> {
            val x: Either<A, B> = f(a).fix().value
            return when (x) {
                is Either.Left<A, B> -> tailRecM(x.a, f)
                is Either.Right<A, B> -> Id(x.b)
            }
        }

        fun <A> pure(a: A): Id<A> = Id(a)
    }
}
