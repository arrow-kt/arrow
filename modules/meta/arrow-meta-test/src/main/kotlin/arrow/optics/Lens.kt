package arrow.optics

/**
 * [Lens] is a type alias for [PLens] which fixes the type arguments
 * and restricts the [PLens] to monomorphic updates.
 */
typealias Lens<S, A> = PLens<S, S, A, A>

/**
 * A [Lens] (or Functional Reference) is an optic that can focus into a structure for
 * getting, setting or modifying the focus (target).
 *
 * A (polymorphic) [PLens] is useful when setting or modifying a value for a constructed type
 * i.e. PLens<Tuple2<Double, Int>, Tuple2<String, Int>, Double, String>
 *
 * A [PLens] can be seen as a pair of functions:
 * - `get: (S) -> A` meaning we can focus into an `S` and extract an `A`
 * - `set: (B) -> (S) -> T` meaning we can focus into an `S` and set a value `B` for a target `A` and obtain a modified source `T`
 *
 * @param S the source of a [PLens]
 * @param T the modified source of a [PLens]
 * @param A the focus of a [PLens]
 * @param B the modified focus of a [PLens]
 */
interface PLens<S, T, A, B> {

  fun get(s: S): A
  fun set(s: S, b: B): T

  companion object {
    /**
     * Invoke operator overload to create a [PLens] of type `S` with target `A`.
     * Can also be used to construct [Lens]
     */
    operator fun <S, T, A, B> invoke(get: (S) -> A, set: (S, B) -> T) = object : PLens<S, T, A, B> {
      override fun get(s: S): A = get(s)

      override fun set(s: S, b: B): T = set(s, b)
    }
  }

  /**
   * Compose a [PLens] with another [PLens]
   */
  infix fun <C, D> compose(l: PLens<A, B, C, D>): PLens<S, T, C, D> = Lens(
    { a -> l.get(get(a)) },
    { s, c -> set(s, l.set(get(s), c)) }
  )

  /**
   * Plus operator overload to compose lenses
   */
  operator fun <C, D> plus(other: PLens<A, B, C, D>): PLens<S, T, C, D> = compose(other)

  /**
   * Modify the focus of s [PLens] using s function `(A) -> B`
   */
  fun modify(s: S, f: (A) -> B): T = set(s, f(get(s)))

  /**
   * Lift a function [f]: `(A) -> B to the context of `S`: `(S) -> T`
   */
  fun lift(f: (A) -> B): (S) -> T = { s -> modify(s, f) }

  /**
   * Verify if the focus of a [PLens] satisfies the predicate
   */
  fun exist(s: S, p: (A) -> Boolean): Boolean = p(get(s))
}
