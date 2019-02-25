package arrow.syntax.unsafe

import arrow.core.Option

/**
 * Returns the [Option]'s value. This method should only be used in cases where the [Option] is not
 * expected to be empty.
 *
 * @throws NoSuchElementException if the [Option] is empty.
 */
fun <T> Option<T>.get(): T = fold({ throw NoSuchElementException("None.get") }, { it })
