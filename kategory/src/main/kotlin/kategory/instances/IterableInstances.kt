package kategory

tailrec fun <A, B> collectLoop(a: A, l : List<PartialFunction<A, B>>, acc: Option<B>): Option<B> =
    when {
        l.isEmpty() || acc.isDefined -> acc
        else -> {
            val head = l[0]
            val tail = l.drop(1)
            val result =
                    if (Try({head.isDefinedAt(a)}).fold({false}, {true})) {
                        Try({ head(a) }).fold({ e -> Option.None }, { b -> Option.Some(b) })
                    } else { Option.None }
            collectLoop(a, tail, result)
        }
    }

@Suppress("UNCHECKED_CAST")
fun <A: Any, B> Iterable<A>.collect(vararg cases: PartialFunction<*, B>): List<B> {
    return flatMap { a ->
        val c = cases.toList() as List<PartialFunction<A, B>>
        collectLoop(a, c, Option.None).fold({ emptyList<B>() }, { listOf(it)})
    }
}
