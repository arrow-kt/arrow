package arrow.optics

import arrow.data.NonEmptyList

/**
 * [Lens] to operate on the head of a [NonEmptyList]
 */
fun <A> NonEmptyList.Companion.head(): Lens<NonEmptyList<A>, A> = Lens(
  get = NonEmptyList<A>::head,
  set = { nel, newHead -> NonEmptyList(newHead, nel.tail) }
)

/**
 * [Lens] to operate on the tail of a [NonEmptyList]
 */
fun <A> NonEmptyList.Companion.tail(): Lens<NonEmptyList<A>, List<A>> = Lens(
  get = NonEmptyList<A>::tail,
  set = { nel, newTail -> NonEmptyList(nel.head, newTail) }
)
