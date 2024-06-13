package arrow.optics.dsl

import arrow.optics.DelicateOptic
import arrow.optics.Optional
import arrow.optics.Prism
import arrow.optics.Traversal
import arrow.optics.filter

/**
 * Focuses on those values for which the [predicate] is true.
 *
 * Warning: any modification to the value this optic is
 * applied to should keep the status of the [predicate].
 */
@DelicateOptic
public fun <S, A> Traversal<S, A>.filter(
  predicate: (A) -> Boolean
): Traversal<S, A> = this compose Prism.filter(predicate)

/**
 * Focuses on the value only if the [predicate] is true.
 *
 * Warning: any modification to the value this optic is
 * applied to should keep the status of the [predicate].
 */
@DelicateOptic
public fun <S, A> Prism<S, A>.filter(
  predicate: (A) -> Boolean
): Prism<S, A> = this compose Prism.filter(predicate)

/**
 * Focuses on the value only if the [predicate] is true.
 *
 * Warning: any modification to the value this optic is
 * applied to should keep the status of the [predicate].
 */
@DelicateOptic
public fun <S, A> Optional<S, A>.filter(
  predicate: (A) -> Boolean
): Optional<S, A> = this compose Prism.filter(predicate)
