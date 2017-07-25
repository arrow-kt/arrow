package kategory

fun <A: Any, B> Iterable<A>.collect(vararg cases: PartialFunction<A, B>): List<B> =
    flatMap { a ->
        val f: PartialFunction<A, B> = cases.reduce({ a, b -> a.orElse(b)})
        if (f.isDefinedAt(a)) listOf(f(a))
        else emptyList()
    }
