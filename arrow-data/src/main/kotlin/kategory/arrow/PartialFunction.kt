package arrow

abstract class PartialFunction<in A, out B> : (A) -> B {
    abstract fun isDefinedAt(a: A): Boolean
}

fun <A, B> PartialFunction<A, B>.orElse(f: PartialFunction<A, B>): PartialFunction<A, B> =
        object : PartialFunction<A, B>() {
            override fun isDefinedAt(a: A): Boolean =
                    this@orElse.isDefinedAt(a) || f.isDefinedAt(a)

            override fun invoke(x: A): B =
                    if (this@orElse.isDefinedAt(x)) {
                        this@orElse(x)
                    } else {
                        f(x)
                    }

        }

/**
 * Turns this partial function into a plain function returning an Option result.
 */
fun <A, B> PartialFunction<A, B>.lift(): (A) -> Option<B> = Lifted(this)

/**
 * Applies this partial function to the given argument when it is contained in the function domain.
 * Applies fallback function where this partial function is not defined.
 */
fun <A, B : B1, A1 : A, B1> PartialFunction<A, B>.applyOrElse(x: A1, default: (A1) -> B1): B1 =
        if (isDefinedAt(x)) invoke(x) else default(x)

fun <A, B, C> PartialFunction<A, B>.andThen(f: (B) -> C): PartialFunction<A, C> =
        object : PartialFunction<A, C>() {
            override fun isDefinedAt(a: A): Boolean = this@andThen.isDefinedAt(a)
            override fun invoke(a: A): C = f(this@andThen(a))
        }

fun <A, B> case(ff: Tuple2<(A) -> Boolean, (A) -> B>): PartialFunction<A, B> =
        object : PartialFunction<A, B>() {
            override fun isDefinedAt(a: A): Boolean = ff.a(a)
            override fun invoke(a: A): B = ff.b(a)
        }

infix fun <A, B> ((A) -> Boolean).then(f: (A) -> B): Tuple2<(A) -> Boolean, (A) -> B> = Tuple2(this, f)

private class Lifted<A, B>(val pf: PartialFunction<A, B>) : (A) -> Option<B> {
    override fun invoke(x: A): Option<B> = pf.andThen { Some(it) }.applyOrElse(x, { None })
}
