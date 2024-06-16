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
 * This optics The optic is perfectly OK when used to get
 * values using `getOrNull` or `getAll`; but requires
 * some caution using `modify` with it.
 *
 * ⚠️ Warning: when using `modify` with this optic,
 * the transformation should not alter the values that are
 * taken into account by the predicate. For example, it is
 * fine to `filter` by `name` and then increase the `age`,
 * but not to `filter` by `name` and then capitalize the `name`.
 *
 * In general terms, this optic does not satisfy the rule that
 * applying two modifications in a row is equivalent to
 * applying those two modifications at once. The following
 * example shows that increasing by one twice is not equivalent
 * to increasing by two.
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
 *
 * The reader interested in a (deep) discussion about why
 * this rule is important may consult the blog post
 * [_Finding (correct) lens laws_](https://oleg.fi/gists/posts/2018-12-12-find-correct-laws.html)
 * by Oleg Genrus.
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
