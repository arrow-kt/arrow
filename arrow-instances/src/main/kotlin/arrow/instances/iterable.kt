package arrow

import arrow.PartialFunction
import arrow.orElse

fun <A : Any, B> Iterable<A>.collect(vararg cases: PartialFunction<A, B>): List<B> =
        flatMap { value: A ->
            val f: PartialFunction<A, B> = cases.reduce({ a, b -> a.orElse(b) })
            if (f.isDefinedAt(value)) listOf(f(value))
            else emptyList()
        }
