package arrow.optics

import arrow.core.NonEmptyList

/**
 * [Lens] to operate on the head of a [NonEmptyList]
 */
@Deprecated(
  "Use the nonEmptyListHead function exposed in the Lens' companion object",
  ReplaceWith(
    "Lens.nonEmptyListHead<A>()",
    "arrow.optics.Lens"
  ),
  DeprecationLevel.WARNING
)
fun <A> NonEmptyList.Companion.head(): Lens<NonEmptyList<A>, A> = Lens(
  get = NonEmptyList<A>::head,
  set = { nel, newHead -> NonEmptyList(newHead, nel.tail) }
)

/**
 * [Lens] to operate on the tail of a [NonEmptyList]
 */
@Deprecated(
  "Use the nonEmptyListTail function exposed in the Lens' companion object",
  ReplaceWith(
    "Lens.nonEmptyListTail<A>()",
    "arrow.optics.Lens"
  ),
  DeprecationLevel.WARNING
)
fun <A> NonEmptyList.Companion.tail(): Lens<NonEmptyList<A>, List<A>> = Lens(
  get = NonEmptyList<A>::tail,
  set = { nel, newTail -> NonEmptyList(nel.head, newTail) }
)
