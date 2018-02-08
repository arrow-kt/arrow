package arrow.core

import arrow.higherkind

fun <A> IdOf<A>.value(): A = this.extract().value

@higherkind
data class Id<out A>(val value: A) : IdOf<A> {

    inline fun <B> map(f: (A) -> B): Id<B> = Id(f(value))

    inline fun <B> flatMap(f: (A) -> IdOf<B>): Id<B> = f(value).extract()

    fun <B> foldLeft(b: B, f: (B, A) -> B): B = f(b, this.extract().value)

    fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> = f(this.extract().value, lb)

    fun <B> coflatMap(f: (IdOf<A>) -> B): Id<B> = this.extract().map({ f(this) })

    fun extract(): A = this.extract().value

    fun <B> ap(ff: IdOf<(A) -> B>): Id<B> = ff.extract().flatMap { f -> map(f) }.extract()

    companion object {

        tailrec fun <A, B> tailRecM(a: A, f: (A) -> IdOf<Either<A, B>>): Id<B> {
            val x: Either<A, B> = f(a).extract().value
            return when (x) {
                is Either.Left<A, B> -> tailRecM(x.a, f)
                is Either.Right<A, B> -> Id(x.b)
            }
        }

        fun <A> pure(a: A): Id<A> = Id(a)

    }

}

