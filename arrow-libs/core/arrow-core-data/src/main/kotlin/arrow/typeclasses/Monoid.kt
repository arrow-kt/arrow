package arrow.typeclasses

import arrow.KindDeprecation

@Deprecated(KindDeprecation)
class ForMonoid private constructor() { companion object }

@Deprecated(KindDeprecation)
typealias MonoidOf<A> = arrow.Kind<ForMonoid, A>

@Deprecated(KindDeprecation)
fun <A> MonoidOf<A>.fix(): Monoid<A> = this as Monoid<A>

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
