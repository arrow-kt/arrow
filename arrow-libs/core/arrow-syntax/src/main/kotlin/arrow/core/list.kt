package arrow.core

/**
 * Returns a list containing all elements except the first element
 */
fun <T> List<T>.tail(): List<T> = this.drop(1)

infix fun <T> T.prependTo(list: List<T>): List<T> = listOf(this) + list

fun <T> List<Option<T>>.flatten(): List<T> = flatMap { it.fold(::emptyList, ::listOf) }
