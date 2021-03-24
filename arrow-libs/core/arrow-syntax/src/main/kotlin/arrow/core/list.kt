package arrow.core

/**
 * Returns a list containing all elements except the first element
 */
fun <T> Iterable<T>.tail(): List<T> =
  drop(1)

infix fun <T> T.prependTo(list: List<T>): List<T> =
  listOf(this) + list

fun <T> Iterable<Option<T>>.filterOption(): List<T> =
  flatMap { it.fold(::emptyList, ::listOf) }
