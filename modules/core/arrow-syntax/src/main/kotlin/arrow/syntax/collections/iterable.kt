package arrow.syntax.collections

import arrow.core.Option
import arrow.core.andThen
import arrow.core.getOrElse

@Deprecated(message = "`collect` is now part of the FunctorFilter interface and generalized to all filterable data types")
fun <A : Any, B> Iterable<A>.collect(vararg cases: (A) -> Option<B>): List<B> =
  flatMap { value: A ->
    val f: (A) -> Option<B> = cases.reduce { f: (A) -> Option<B>, g: (A) -> Option<B> ->
      f.andThen { optionB -> optionB.getOrElse { g(value) } } as (A) -> Option<B>
    }
    f(value).map { listOf(it) }.getOrElse { emptyList() }
  }
