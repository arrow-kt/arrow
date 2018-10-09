package arrow.typeclasses

class ForMonoid private constructor() { companion object }
typealias MonoidOf<A> = arrow.Kind<ForMonoid, A>
inline fun <A> MonoidOf<A>.fix(): Monoid<A> = this as Monoid<A>

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
  fun combineAll(vararg elems: A): A = elems.asList().combineAll()

  companion object
}