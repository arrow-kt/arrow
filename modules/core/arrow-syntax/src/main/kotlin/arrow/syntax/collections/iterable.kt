package arrow.syntax.collections

import arrow.core.PartialFunction
import arrow.core.orElse

@Deprecated("PartialFunction is an incomplete experiment due for removal. See https://github.com/arrow-kt/arrow/pull/1419#issue-273308228")
fun <A : Any, B> Iterable<A>.collect(vararg cases: PartialFunction<A, B>): List<B> =
  flatMap { value: A ->
    val f: PartialFunction<A, B> = cases.reduce { a, b -> a.orElse(b) }
    if (f.isDefinedAt(value)) listOf(f(value))
    else emptyList()
  }
