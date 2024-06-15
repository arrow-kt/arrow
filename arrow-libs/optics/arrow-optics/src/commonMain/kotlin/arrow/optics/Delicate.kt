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
 * Warning: when using [modify], the transformation should
 * not alter whether the predicate holds on the value.
 *
 * Otherwise, this optic does not satisfy the rule that
 * applying two modifications in a row is equivalent to
 * applying those two modifications at once.
 *
 * ```
 * val p = Optional.filter<Int> { it % 2 == 0 }  // focus on even numbers
 * val n = 2  // an even number
 *
 * p.modify(p.modify(n) { it + 1 }) { it + 1 }
 * //       ---------------------- = 3
 * // ---------------------------------------- = null
 *
 * p.modify(n) { it + 2 }
 * // ------------------- = 4
 * ```
 */
@DelicateOptic
public fun <S> POptional.Companion.filter(
  predicate: (S) -> Boolean
): Optional<S, S> = Optional(
  getOption = { if (predicate(it)) Some(it) else None },
  set = { s, x -> if (predicate(s)) x else s }
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
