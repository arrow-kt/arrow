package kategory.optics

import kategory.NonEmptyList

/**
 * [Lens] to operate on the head of a [NonEmptyList]
 */
fun <T> nelHead(): Lens<NonEmptyList<T>, T> = Lens(
        get = { it.head },
        set = { newHead -> { nel -> NonEmptyList(newHead, nel.tail) } }
)