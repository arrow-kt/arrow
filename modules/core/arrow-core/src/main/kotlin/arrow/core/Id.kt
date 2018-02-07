package arrow.core

import arrow.higherkind

fun <A> IdKind<A>.value(): A = this.reify().value

@higherkind
data class Id<out A>(val value: A) : IdKind<A> {

    inline fun <B> map(f: (A) -> B): Id<B> = Id(f(value))

    inline fun <B> flatMap(f: (A) -> IdKind<B>): Id<B> = f(value).reify()

    fun <B> foldLeft(b: B, f: (B, A) -> B): B = f(b, this.reify().value)

    fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> = f(this.reify().value, lb)

    fun <B> coflatMap(f: (IdKind<A>) -> B): Id<B> = this.reify().map({ f(this) })

    fun extract(): A = this.reify().value

    fun <B> ap(ff: IdKind<(A) -> B>): Id<B> = ff.reify().flatMap { f -> map(f) }.reify()

    companion object {

        tailrec fun <A, B> tailRecM(a: A, f: (A) -> IdKind<Either<A, B>>): Id<B> {
            val x: Either<A, B> = f(a).reify().value
            return when (x) {
                is Either.Left<A, B> -> tailRecM(x.a, f)
                is Either.Right<A, B> -> Id(x.b)
            }
        }

        fun <A> pure(a: A): Id<A> = Id(a)

    }

}

