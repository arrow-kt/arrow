package arrow.optics.dsl

import arrow.optics.DelicateOptic
import arrow.optics.Optional
import arrow.optics.Traversal
import arrow.optics.filter

/**
 * Focuses on those values for which the [predicate] is true.
 *
 * See [Optional.filter] for further description of the
 * caution one should be with this optic.
 */
@DelicateOptic
public fun <S, A> Traversal<S, A>.filter(
  predicate: (A) -> Boolean
): Traversal<S, A> = this compose Optional.filter(predicate)

/**
 * Focuses on the value only if the [predicate] is true.
 *
 * See [Optional.filter] for further description of the
 * caution one should be with this optic.
 */
@DelicateOptic
public fun <S, A> Optional<S, A>.filter(
  predicate: (A) -> Boolean
): Optional<S, A> = this compose Optional.filter(predicate)
