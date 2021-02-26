package arrow.optics

import arrow.core.Either
import arrow.core.compose
import arrow.core.identity
import arrow.typeclasses.Monoid

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
interface PIso<S, T, A, B> : PPrism<S, T, A, B>, PLens<S, T, A, B>, Getter<S, A>, POptional<S, T, A, B>, PSetter<S, T, A, B>, Fold<S, A>, PTraversal<S, T, A, B>, PEvery<S, T, A, B> {

  /**
   * Get the focus of a [PIso]
   */
  override fun get(source: S): A

  /**
   * Get the modified focus of a [PIso]
   */
  override fun reverseGet(focus: B): T

  override fun getOrModify(source: S): Either<T, A> =
    Either.Right(get(source))

  override fun set(source: S, focus: B): T =
    set(focus)

  /**
   * Modify polymorphically the focus of a [PIso] with a function
   */
  override fun modify(source: S, map: (focus: A) -> B): T =
    reverseGet(map(get(source)))

  override fun <R> foldMap(M: Monoid<R>, source: S, map: (A) -> R): R =
    map(get(source))

  /**
   * Reverse a [PIso]: the source becomes the target and the target becomes the source
   */
  fun reverse(): PIso<B, A, T, S> =
    PIso(this::reverseGet, this::get)

  /**
   * Set polymorphically the focus of a [PIso] with a value
   */
  fun set(b: B): T =
    reverseGet(b)

  /**
   * Pair two disjoint [PIso]
   */
  infix fun <S1, T1, A1, B1> split(other: PIso<S1, T1, A1, B1>): PIso<Pair<S, S1>, Pair<T, T1>, Pair<A, A1>, Pair<B, B1>> =
    PIso(
      { (a, c) -> get(a) to other.get(c) },
      { (b, d) -> reverseGet(b) to other.reverseGet(d) }
    )

  /**
   * Create a pair of the [PIso] and a type [C]
   */
  override fun <C> first(): PIso<Pair<S, C>, Pair<T, C>, Pair<A, C>, Pair<B, C>> = Iso(
    { (a, c) -> get(a) to c },
    { (b, c) -> reverseGet(b) to c }
  )

  /**
   * Create a pair of a type [C] and the [PIso]
   */
  override fun <C> second(): PIso<Pair<C, S>, Pair<C, T>, Pair<C, A>, Pair<C, B>> = PIso(
    { (c, a) -> c to get(a) },
    { (c, b) -> c to reverseGet(b) }
  )

  /**
   * Create a sum of the [PIso] and a type [C]
   */
  override fun <C> left(): PIso<Either<S, C>, Either<T, C>, Either<A, C>, Either<B, C>> = PIso(
    { it.bimap(this::get, ::identity) },
    { it.bimap(this::reverseGet, ::identity) }
  )

  /**
   * Create a sum of a type [C] and the [PIso]
   */
  override fun <C> right(): PIso<Either<C, S>, Either<C, T>, Either<C, A>, Either<C, B>> = PIso(
    { it.bimap(::identity, this::get) },
    { it.bimap(::identity, this::reverseGet) }
  )

  /**
   * Compose a [PIso] with a [PIso]
   */
  infix fun <C, D> compose(other: PIso<A, B, C, D>): PIso<S, T, C, D> = PIso(
    other::get compose this::get,
    this::reverseGet compose other::reverseGet
  )

  operator fun <C, D> plus(other: PIso<A, B, C, D>): PIso<S, T, C, D> =
    this compose other

  companion object {

    /**
     * create an [PIso] between any type and itself.
     * Id is the zero element of optics composition, for any optic o of type O (e.g. PLens, Prism, POptional, ...):
     * o compose Iso.id == o
     */
    fun <S> id(): Iso<S, S> =
      Iso(::identity, ::identity)

    /**
     * Invoke operator overload to create a [PIso] of type `S` with target `A`.
     * Can also be used to construct [Iso]
     */
    operator fun <S, T, A, B> invoke(get: (S) -> (A), reverseGet: (B) -> T) =
      object : PIso<S, T, A, B> {
        override fun get(source: S): A = get(source)
        override fun reverseGet(focus: B): T = reverseGet(focus)
      }
  }
}
