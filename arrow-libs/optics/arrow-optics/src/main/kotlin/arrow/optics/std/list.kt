package arrow.optics

import arrow.core.ListExtensions
import arrow.core.ListK
import arrow.core.NonEmptyList
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.Tuple2
import arrow.core.identity
import arrow.core.k
import arrow.optics.typeclasses.Cons
import arrow.optics.typeclasses.Index
import arrow.optics.typeclasses.Snoc

/**
 * [Optional] to safely operate on the head of a list
 */
@Deprecated(
  "ListK is being deprecated, use the function defined for List instead.",
  ReplaceWith(
    "Optional.listHead<A>()",
    "arrow.optics.Optional"
  ),
  DeprecationLevel.WARNING
)
fun <A> ListK.Companion.head(): Optional<List<A>, A> = Optional(
  getOption = { Option.fromNullable(it.firstOrNull()) },
  set = { list, newHead -> list.mapIndexed { index, value -> if (index == 0) newHead else value } }
)

/**
 * [Optional] to safely operate on the tail of a list
 */
@Deprecated(
  "ListK is being deprecated, use the function defined for List instead.",
  ReplaceWith(
    "Optional.listTail<A>()",
    "arrow.optics.Optional"
  ),
  DeprecationLevel.WARNING
)
fun <A> ListK.Companion.tail(): Optional<List<A>, List<A>> = Optional(
  getOption = { if (it.isEmpty()) None else Some(it.drop(1)) },
  set = { list, newTail ->
    list.firstOrNull()?.let {
      listOf(it) + newTail
    } ?: emptyList()
  }
)

/**
 * [PIso] that defines equality between a [List] and [Option] [NonEmptyList]
 */
@Deprecated(
  "ListK is being deprecated, use the function defined for List instead.",
  ReplaceWith(
    "Iso.listToPOptionNel<A, B>()",
    "arrow.optics.Iso"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> ListK.Companion.toPOptionNel(): PIso<List<A>, List<B>, Option<NonEmptyList<A>>, Option<NonEmptyList<B>>> =
  PIso(
    get = { aas -> if (aas.isEmpty()) None else Some(NonEmptyList(aas.first(), aas.drop(1))) },
    reverseGet = { optNel -> optNel.fold({ emptyList() }, NonEmptyList<B>::all) }
  )

/**
 * [Iso] that defines equality between a [List] and [Option] [NonEmptyList]
 */
@Deprecated(
  "ListK is being deprecated, use the function defined for List instead.",
  ReplaceWith(
    "Iso.listToOptionNel<A>()",
    "arrow.optics.Iso"
  ),
  DeprecationLevel.WARNING
)
fun <A> ListK.Companion.toOptionNel(): Iso<List<A>, Option<NonEmptyList<A>>> = toPOptionNel()

/**
 * [PIso] that defines the equality between a [List] and a [ListK]
 */
@Deprecated(
  "ListK is being deprecated, and this function will be removed in 0.13.0.",
  level = DeprecationLevel.WARNING
)
fun <A, B> ListExtensions.toPListK(): PIso<List<A>, List<B>, ListK<A>, ListK<B>> = PIso(
  get = List<A>::k,
  reverseGet = ::identity
)

/**
 * [Iso] that defines the equality between a [List] and a [ListK]
 */
@Deprecated(
  "ListK is being deprecated, and this function will be removed in 0.13.0.",
  level = DeprecationLevel.WARNING
)
fun <A> ListExtensions.toListK(): Iso<List<A>, ListK<A>> = toPListK()

operator fun <A, T> PLens<T, T, List<A>, List<A>>.get(i: Int): POptional<T, T, A, A> =
  Index.list<A>().run { this@get.get(i) }

infix fun <A> A.cons(tail: List<A>): List<A> =
  Cons.list<A>().run { this@cons.cons(tail) }

fun <A> List<A>.uncons(): Option<Tuple2<A, List<A>>> =
  Cons.list<A>().run { this@uncons.uncons() }

infix fun <A> List<A>.snoc(last: A): List<A> =
  Snoc.list<A>().run { this@snoc.snoc(last) }

fun <A> List<A>.unsnoc(): Option<Tuple2<List<A>, A>> =
  Snoc.list<A>().run { this@unsnoc.unsnoc() }
