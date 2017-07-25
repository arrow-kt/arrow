package kategory

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