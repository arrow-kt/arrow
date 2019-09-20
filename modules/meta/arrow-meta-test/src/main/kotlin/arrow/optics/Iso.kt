package arrow.optics

/**
 * [Iso] is a type alias for [PIso] which fixes the type arguments
 * and restricts the [PIso] to monomorphic updates.
 */
typealias Iso<S, A> = PIso<S, S, A, A>

/**
 * An [Iso] is a loss less invertible optic that defines an isomorphism between a type [S] and [A]
 * i.e. a data class and its properties represented by TupleN
 *
 * A (polymorphic) [PIso] is useful when setting or modifying a value for a constructed type
 * i.e. PIso<Option<Int>, Option<String>, Int?, String?>
 *
 * An [PIso] is also a valid [PLens], [PPrism]
 *
 * @param S the source of a [PIso]
 * @param T the modified source of a [PIso]
 * @param A the focus of a [PIso]
 * @param B the modified target of a [PIso]
 */
interface PIso<S, T, A, B> {

  /**
   * Get the focus of a [PIso]
   */
  fun get(s: S): A

  /**
   * Get the modified focus of a [PIso]
   */
  fun reverseGet(b: B): T

  companion object {

    /**
     * Invoke operator overload to create a [PIso] of type `S` with target `A`.
     * Can also be used to construct [Iso]
     */
    operator fun <S, T, A, B> invoke(get: (S) -> (A), reverseGet: (B) -> T) = object : PIso<S, T, A, B> {

      override fun get(s: S): A = get(s)

      override fun reverseGet(b: B): T = reverseGet(b)
    }
  }

  /**
   * Reverse a [PIso]: the source becomes the target and the target becomes the source
   */
  fun reverse(): PIso<B, A, T, S> = PIso(this::reverseGet, this::get)

  /**
   * Set polymorphically the focus of a [PIso] with a value
   */
  fun set(b: B): T = reverseGet(b)

  /**
   * Compose a [PIso] with a [PLens]
   */
  infix fun <C, D> compose(other: PLens<A, B, C, D>): PLens<S, T, C, D> = asLens() compose other

  /**
   * Plus operator overload to compose lenses
   */
  // operator fun <C, D> plus(other: PIso<A, B, C, D>): PIso<S, T, C, D> = compose(other)

  operator fun <C, D> plus(other: PLens<A, B, C, D>): PLens<S, T, C, D> = compose(other)

  /**
   * View a [PIso] as a [PLens]
   */
  fun asLens(): PLens<S, T, A, B> = PLens(this::get) { _, b -> set(b) }

  /**
   * Check if the focus satisfies the predicate
   */
  fun exist(s: S, p: (A) -> Boolean): Boolean = p(get(s))

  /**
   * Modify polymorphically the focus of a [PIso] with a function
   */
  fun modify(s: S, f: (A) -> B): T = reverseGet(f(get(s)))

  /**
   * Modify polymorphically the focus of a [PIso] with a function
   */
  fun lift(f: (A) -> B): (S) -> T = { s -> reverseGet(f(get(s))) }
}
