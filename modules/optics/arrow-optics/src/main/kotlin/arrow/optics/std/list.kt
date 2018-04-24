package arrow.optics

import arrow.core.*
import arrow.data.ListK
import arrow.data.NonEmptyList
import arrow.data.fix
import arrow.data.k

/**
 * [Optional] to safely operate on the head of a list
 */
fun <A> listHead(): Optional<List<A>, A> = Optional(
  partialFunction = case({ list: List<A> -> list.isNotEmpty() }
    toT { list: List<A> -> list.first() }),
  set = { newHead -> { list -> list.mapIndexed { index, value -> if (index == 0) newHead else value } } }
)

/**
 * [Optional] to safely operate on the tail of a list
 */
fun <A> listTail(): Optional<List<A>, List<A>> = Optional(
  partialFunction = case({ list: List<A> -> list.isNotEmpty() }
    toT { list: List<A> -> list.drop(1) }),
  set = { newTail -> { list -> (list.firstOrNull()?.let(::listOf) ?: emptyList()) + newTail } }
)

/**
 * [PIso] that defines equality between a [List] and [Option] [NonEmptyList]
 */
fun <A, B> pListToOptionNel(): PIso<List<A>, List<B>, Option<NonEmptyList<A>>, Option<NonEmptyList<B>>> = PIso(
  get = { aas -> if (aas.isEmpty()) None else Some(NonEmptyList(aas.first(), aas.drop(1))) },
  reverseGet = { optNel -> optNel.fix().fold({ emptyList() }, { it.fix().all }) }
)

/**
 * [Iso] that defines equality between a [List] and [Option] [NonEmptyList]
 */
fun <A> listToOptionNel(): Iso<List<A>, Option<NonEmptyList<A>>> = pListToOptionNel()

/**
 * [PIso] that defines the equality between a [List] and a [ListK]
 */
fun <A, B> pListToListK(): PIso<List<A>, List<B>, ListK<A>, ListK<B>> = PIso(
  get = { it.k() },
  reverseGet = { it.fix().list }
)

/**
 * [Iso] that defines the equality between a [List] and a [ListK]
 */
fun <A> listToListK(): Iso<List<A>, ListK<A>> = pListToListK()
