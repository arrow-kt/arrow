package arrow.optics

import arrow.core.None
import arrow.core.Some

@RequiresOptIn(
  level = RequiresOptIn.Level.WARNING,
  message = "Delicate optic, please check the documentation for correct usage."
)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION)
public annotation class DelicateOptic

/**
 * Focuses on the value only if the [predicate] is true.
 *
 * Warning: any modification to the value this optic is
 * applied to should keep the status of the [predicate].
 */
@DelicateOptic
public fun <S> PPrism.Companion.filter(
  predicate: (S) -> Boolean
): Prism<S, S> = Prism(
  getOption = { if (predicate(it)) Some(it) else None },
  reverseGet = { it },
)

/**
 * Focuses on a sequence of lenses.
 *
 * Warning: using a lens more than once in the list
 * may give unexpected results upon modification.
 */
@DelicateOptic
public fun <S, A, B> PTraversal.Companion.fromLenses(
  lens1: PLens<S, S, A, B>, vararg lenses: PLens<S, S, A, B>
): PTraversal<S, S, A, B> = object : PTraversal<S, S, A, B> {
  override fun <R> foldMap(initial: R, combine: (R, R) -> R, source: S, map: (focus: A) -> R): R =
    lenses.fold(map(lens1.get(source))) { current, lens -> combine(current, map(lens.get(source))) }
  override fun modify(source: S, map: (focus: A) -> B): S =
    lenses.fold(lens1.modify(source, map)) { current, lens -> lens.modify(current, map) }
}
