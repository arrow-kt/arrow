package arrow.typeclasses

class ForMonoid private constructor() { companion object }
typealias MonoidOf<A> = arrow.Kind<ForMonoid, A>
fun <A> MonoidOf<A>.fix(): Monoid<A> = this as Monoid<A>

/**
 * ank_macro_hierarchy(arrow.typeclasses.Monoid)
 */
interface Monoid<A> : Semigroup<A>, MonoidOf<A> {
  /**
   * A zero value for this A
   */
  fun empty(): A

  /**
   * Combine an [Collection] of [A] values.
   */
  fun Collection<A>.combineAll(): A =
    if (isEmpty()) empty() else reduce { a, b -> a.combine(b) }

  /**
   * Combine an array of [A] values.
   */
  fun combineAll(elems: List<A>): A = elems.combineAll()

  companion object
}