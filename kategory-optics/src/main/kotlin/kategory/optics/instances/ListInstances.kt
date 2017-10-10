package kategory.optics

import kategory.case
import kategory.toT

/**
 * [Optional] to safely operate on the head of a list
 */
fun <T> listHead(): Optional<List<T>, T> = Optional(
        partialFunction = case({ list: List<T> -> list.isNotEmpty() }
                toT { list: List<T> -> list.first() }),
        set = { newHead -> { list -> list.mapIndexed { index, value -> if (index == 0) newHead else value } } }
)

/**
 * [Optional] to safely operate on the tail of a list
 */
fun <T> listTail(): Optional<List<T>, List<T>> = Optional(
        partialFunction = case({ list: List<T> -> list.isNotEmpty() }
                toT { list: List<T> -> list.drop(1) }),
        set = { newTail -> { list -> (list.firstOrNull()?.let(::listOf) ?: emptyList()) + newTail } }
)