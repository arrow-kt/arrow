package arrow.syntax.collections

import arrow.core.Option
import arrow.core.Predicate
import arrow.core.andThen
import arrow.core.getOrElse
import arrow.core.toOption

@Deprecated(message = "`firstOption` is now part of the Foldable interface and generalized to all foldable data types")
fun <T> Iterable<T>.firstOption(): Option<T> = firstOrNull().toOption()

@Deprecated(message = "`firstOption` is now part of the Foldable interface and generalized to all foldable data types")
fun <T> Iterable<T>.firstOption(predicate: Predicate<T>): Option<T> = firstOrNull(predicate).toOption()

@Deprecated(message = "`collect` is now part of the FunctorFilter interface and generalized to all filterable data types")
fun <A : Any, B> Iterable<A>.collect(vararg cases: (A) -> Option<B>): List<B> =
  flatMap { value: A ->
    val f: (A) -> Option<B> = cases.reduce { f: (A) -> Option<B>, g: (A) -> Option<B> ->
      f.andThen { optionB -> optionB.getOrElse { g(value) } } as (A) -> Option<B>
    }
    f(value).map { listOf(it) }.getOrElse { emptyList() }
  }
