package arrow.syntax.collections

import arrow.core.Option
import arrow.core.PartialFunction
import arrow.core.Predicate
import arrow.core.orElse
import arrow.core.toOption

@Deprecated(message = "`firstOption` is now part of the Foldable interface and generalized to all foldable data types")
fun <T> Iterable<T>.firstOption(): Option<T> = firstOrNull().toOption()

@Deprecated(message = "`firstOption` is now part of the Foldable interface and generalized to all foldable data types")
fun <T> Iterable<T>.firstOption(predicate: Predicate<T>): Option<T> = firstOrNull(predicate).toOption()

@Deprecated("PartialFunction is an incomplete experiment due for removal. See https://github.com/arrow-kt/arrow/pull/1419#issue-273308228")
fun <A : Any, B> Iterable<A>.collect(vararg cases: PartialFunction<A, B>): List<B> =
  flatMap { value: A ->
    val f: PartialFunction<A, B> = cases.reduce { a, b -> a.orElse(b) }
    if (f.isDefinedAt(value)) listOf(f(value))
    else emptyList()
  }
