package kategory

@Suppress("UNCHECKED_CAST")
fun <A: Any, B> Iterable<A>.collect(vararg cases: PartialFunction<*, B>): List<B> {
    val f: PartialFunction<A, B> = cases.reduce { a, b -> a.orElse(b) } as PartialFunction<A, B>
    return filter(f::isDefinedAt).map(f)
}

