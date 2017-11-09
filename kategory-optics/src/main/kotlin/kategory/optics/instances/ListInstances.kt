package kategory.optics

import kategory.ListKW
import kategory.NonEmptyList
import kategory.Option
import kategory.case
import kategory.k
import kategory.none
import kategory.some
import kategory.toT

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
        get = { aas -> if (aas.isEmpty()) none() else NonEmptyList(aas.first(), aas.drop(1)).some() },
        reverseGet = { optNel -> optNel.fold({ emptyList() }, { it.all }) }
)

/**
 * [Iso] that defines equality between a [List] and [Option] [NonEmptyList]
 */
fun <A> listToOptionNel(): Iso<List<A>, Option<NonEmptyList<A>>> = pListToOptionNel()

/**
 * [PIso] that defines the equality between a [List] and a [ListKW]
 */
fun <A, B> pListToListKW(): PIso<List<A>, List<B>, ListKW<A>, ListKW<B>> = PIso(
        get = { it.k() },
        reverseGet = { it.list }
)

/**
 * [Iso] that defines the equality between a [List] and a [ListKW]
 */
fun <A> listToListKW(): Iso<List<A>, ListKW<A>> = pListToListKW()