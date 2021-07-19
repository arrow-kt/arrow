package arrow.optics

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.identity
import arrow.typeclasses.Monoid
import kotlin.jvm.JvmStatic

/**
 * [Lens] is a type alias for [PLens] which fixes the type arguments
 * and restricts the [PLens] to monomorphic updates.
 */
public typealias Lens<S, A> = PLens<S, S, A, A>

/**
 * A [Lens] (or Functional Reference) is an optic that can focus into a structure for
 * getting, setting or modifying the focus (target).
 *
 * A (polymorphic) [PLens] is useful when setting or modifying a value for a constructed type
 * i.e. PLens<Pair<Double, Int>, Pair<String, Int>, Double, String>
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
public interface PLens<S, T, A, B> : Getter<S, A>, POptional<S, T, A, B>, PSetter<S, T, A, B>, Fold<S, A>,
  PTraversal<S, T, A, B>, PEvery<S, T, A, B> {

  override fun get(source: S): A

  override fun set(source: S, focus: B): T

  override fun getOrModify(source: S): Either<T, A> =
    Either.Right(get(source))

  override fun <R> foldMap(M: Monoid<R>, source: S, map: (focus: A) -> R): R =
    map(get(source))

  /**
   * Join two [PLens] with the same focus in [A]
   */
  public infix fun <S1, T1> choice(other: PLens<S1, T1, A, B>): PLens<Either<S, S1>, Either<T, T1>, A, B> = PLens(
    { ss -> ss.fold(this::get, other::get) },
    { ss, b -> ss.bimap({ s -> set(s, b) }, { s -> other.set(s, b) }) }
  )

  /**
   * Pair two disjoint [PLens]
   */
  public infix fun <S1, T1, A1, B1> split(other: PLens<S1, T1, A1, B1>): PLens<Pair<S, S1>, Pair<T, T1>, Pair<A, A1>, Pair<B, B1>> =
    PLens(
      { (s, c) -> get(s) to other.get(c) },
      { (s, s1), (b, b1) -> set(s, b) to other.set(s1, b1) }
    )

  /**
   * Create a product of the [PLens] and a type [C]
   */
  override fun <C> first(): PLens<Pair<S, C>, Pair<T, C>, Pair<A, C>, Pair<B, C>> = PLens(
    { (s, c) -> get(s) to c },
    { (s, _), (b, c) -> set(s, b) to c }
  )

  /**
   * Create a product of a type [C] and the [PLens]
   */
  override fun <C> second(): PLens<Pair<C, S>, Pair<C, T>, Pair<C, A>, Pair<C, B>> = PLens(
    { (c, s) -> c to get(s) },
    { (_, s), (c, b) -> c to set(s, b) }
  )

  /**
   * Compose a [PLens] with another [PLens]
   */
  public infix fun <C, D> compose(other: PLens<in A, out B, out C, in D>): PLens<S, T, C, D> = Lens(
    { a -> other.get(get(a)) },
    { s, c -> set(s, other.set(get(s), c)) }
  )

  public operator fun <C, D> plus(other: PLens<in A, out B, out C, in D>): PLens<S, T, C, D> =
    this compose other

  public companion object {

    public fun <S> id(): PIso<S, S, S, S> = PIso.id<S>()

    /**
     * [PLens] that takes either [S] or [S] and strips the choice of [S].
     */
    public fun <S> codiagonal(): Lens<Either<S, S>, S> = Lens(
      get = { it.fold(::identity, ::identity) },
      set = { s, b -> s.bimap({ b }, { b }) }
    )

    /**
     * Invoke operator overload to create a [PLens] of type `S` with target `A`.
     * Can also be used to construct [Lens]
     */
    public operator fun <S, T, A, B> invoke(get: (S) -> A, set: (S, B) -> T): PLens<S, T, A, B> =
      object : PLens<S, T, A, B> {
        override fun get(s: S): A = get(s)
        override fun set(s: S, b: B): T = set(s, b)
      }

    /**
     * [Lens] to operate on the head of a [NonEmptyList]
     */
    @JvmStatic
    public fun <A> nonEmptyListHead(): Lens<NonEmptyList<A>, A> =
      Lens(
        get = NonEmptyList<A>::head,
        set = { nel, newHead -> NonEmptyList(newHead, nel.tail) }
      )

    /**
     * [Lens] to operate on the tail of a [NonEmptyList]
     */
    @JvmStatic
    public fun <A> nonEmptyListTail(): Lens<NonEmptyList<A>, List<A>> =
      Lens(
        get = NonEmptyList<A>::tail,
        set = { nel, newTail -> NonEmptyList(nel.head, newTail) }
      )

    /**
     * [PLens] to focus into the first value of a [Pair]
     */
    @JvmStatic
    public fun <A, B, R> pairPFirst(): PLens<Pair<A, B>, Pair<R, B>, A, R> =
      PLens(
        get = { it.first },
        set = { (_, b), r -> r to b }
      )

    /**
     * [Lens] to focus into the first value of a [Pair]
     */
    @JvmStatic
    public fun <A, B> pairFirst(): Lens<Pair<A, B>, A> =
      pairPFirst()

    /**
     * [PLens] to focus into the second value of a [Pair]
     */
    @JvmStatic
    public fun <A, B, R> pairPSecond(): PLens<Pair<A, B>, Pair<A, R>, B, R> =
      PLens(
        get = { it.second },
        set = { (a, _), r -> a to r }
      )

    /**
     * [Lens] to focus into the second value of a [Pair]
     */
    @JvmStatic
    public fun <A, B> pairSecond(): Lens<Pair<A, B>, B> =
      pairPSecond()

    /**
     * [PLens] to focus into the first value of a [Triple]
     */
    @JvmStatic
    public fun <A, B, C, R> triplePFirst(): PLens<Triple<A, B, C>, Triple<R, B, C>, A, R> =
      PLens(
        get = { it.first },
        set = { (_, b, c), r -> Triple(r, b, c) }
      )

    /**
     * [Lens] to focus into the first value of a [Triple]
     */
    @JvmStatic
    public fun <A, B, C> tripleFirst(): Lens<Triple<A, B, C>, A> =
      triplePFirst()

    /**
     * [PLens] to focus into the second value of a [Triple]
     */
    @JvmStatic
    public fun <A, B, C, R> triplePSecond(): PLens<Triple<A, B, C>, Triple<A, R, C>, B, R> =
      PLens(
        get = { it.second },
        set = { (a, _, c), r -> Triple(a, r, c) }
      )

    /**
     * [Lens] to focus into the second value of a [Triple]
     */
    @JvmStatic
    public fun <A, B, C> tripleSecond(): Lens<Triple<A, B, C>, B> =
      triplePSecond()

    /**
     * [PLens] to focus into the third value of a [Triple]
     */
    @JvmStatic
    public fun <A, B, C, R> triplePThird(): PLens<Triple<A, B, C>, Triple<A, B, R>, C, R> =
      PLens(
        get = { it.third },
        set = { (a, b, _), r -> Triple(a, b, r) }
      )

    /**
     * [Lens] to focus into the third value of a [Triple]
     */
    @JvmStatic
    public fun <A, B, C> tripleThird(): Lens<Triple<A, B, C>, C> =
      triplePThird()
  }
}
