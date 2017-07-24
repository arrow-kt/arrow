package kategory

abstract class PartialFunction<in A, out B> : (A) -> B {
    abstract fun isDefinedAt(a: A): Boolean
}

fun <A, B> PartialFunction<A, B>.orElse(f: PartialFunction<A, B>): PartialFunction<A, B> {
    val fthis = this
    return object : PartialFunction<A, B>() {

        override fun isDefinedAt(a: A): Boolean =
                fthis.isDefinedAt(a) || f.isDefinedAt(a)

        override fun invoke(x: A): B =
                if (fthis.isDefinedAt(x)) {
                    fthis(x)
                } else {
                    f(x)
                }

    }
}

fun <A, B, C> PartialFunction<A, B>.andThen(f: (B) -> C): PartialFunction<A, C> {
    val fthis = this
    return object : PartialFunction<A, C>() {
        override fun isDefinedAt(a: A): Boolean = fthis.isDefinedAt(a)
        override fun invoke(a: A): C = f(fthis(a))
    }
}

fun <A : Any?, B> case(ff: Tuple2<(A) -> Boolean, (A) -> B>): PartialFunction<A, B> =
        object : PartialFunction<A, B>() {
            override fun isDefinedAt(a: A): Boolean = ff.a(a)
            override fun invoke(a: A): B = ff.b(a)
        }

inline fun <reified A> typeOf() : (A) -> Boolean = {
    Try({ it as A }).fold({ false }, { true })
}

infix fun <A, B> ((A) -> Boolean).then(f: (A) -> B): Tuple2<(A) -> Boolean, (A) -> B> = Tuple2(this, f)

fun <B> default(f: (Any) -> B): (Any) -> B = f

@Suppress("UNCHECKED_CAST")
class match<A>(val a: A) {
    operator fun <B> invoke(vararg cases: PartialFunction<*, B>, default: (A) -> B): B {
        val maybeB = collectLoop(a, cases.toList() as List<PartialFunction<A, B>>, Option.None)
        return maybeB.fold({default(a)}, {it})
    }
}

