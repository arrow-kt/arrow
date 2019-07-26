package arrow.optics

import arrow.core.ListExtensions
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.identity
import arrow.core.ListK
import arrow.core.NonEmptyList
import arrow.core.k

/**
 * [Optional] to safely operate on the head of a list
 */
fun <A> ListK.Companion.head(): Optional<List<A>, A> = Optional(
  getOption = { Option.fromNullable(it.firstOrNull()) },
  set = { list, newHead -> list.mapIndexed { index, value -> if (index == 0) newHead else value } }
)

/**
 * [Optional] to safely operate on the tail of a list
 */
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
fun <A, B> ListK.Companion.toPOptionNel(): PIso<List<A>, List<B>, Option<NonEmptyList<A>>, Option<NonEmptyList<B>>> = PIso(
  get = { aas -> if (aas.isEmpty()) None else Some(NonEmptyList(aas.first(), aas.drop(1))) },
  reverseGet = { optNel -> optNel.fold({ emptyList() }, NonEmptyList<B>::all) }
)

/**
 * [Iso] that defines equality between a [List] and [Option] [NonEmptyList]
 */
fun <A> ListK.Companion.toOptionNel(): Iso<List<A>, Option<NonEmptyList<A>>> = toPOptionNel()

/**
 * [PIso] that defines the equality between a [List] and a [ListK]
 */
fun <A, B> ListExtensions.toPListK(): PIso<List<A>, List<B>, ListK<A>, ListK<B>> = PIso(
  get = List<A>::k,
  reverseGet = ::identity
)

/**
 * [Iso] that defines the equality between a [List] and a [ListK]
 */
fun <A> ListExtensions.toListK(): Iso<List<A>, ListK<A>> = toPListK()
