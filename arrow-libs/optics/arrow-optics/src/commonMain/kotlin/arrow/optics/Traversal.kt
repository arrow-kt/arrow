package arrow.optics

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.Option
import arrow.core.Tuple10
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9
import arrow.core.identity
import arrow.typeclasses.Monoid
import kotlin.jvm.JvmStatic

/**
 * [Traversal] is a type alias for [PTraversal] which fixes the type arguments
 * and restricts the [PTraversal] to monomorphic updates.
 */
public typealias Traversal<S, A> = PTraversal<S, S, A, A>

/**
 * A [Traversal] is an optic that allows to see into a structure with 0 to N foci.
 *
 * [Traversal] is a generalisation of [kotlin.collections.map] and can be seen as a representation of modify.
 * all methods are written in terms of modify
 *
 * @param S the source of a [PTraversal]
 * @param T the modified source of a [PTraversal]
 * @param A the target of a [PTraversal]
 * @param B the modified target of a [PTraversal]
 */
public interface PTraversal<S, T, A, B> {

  /**
   * Map each target to a type R and use a Monoid to fold the results
   */
  public fun <R> foldMap(M: Monoid<R>, source: S, map: (focus: A) -> R): R

  /**
   * Modify polymorphically the focus of a [PTraversal] with a function [map].
   */
  public fun modify(source: S, map: (focus: A) -> B): T

  /**
   * Set polymorphically the focus of a [PTraversal] with a value [focus].
   */
  public fun set(source: S, focus: B): T =
    modify(source) { focus }

  /**
   * Lift a function [map]: `(A) -> B to the context of `S`: `(S) -> T`
   */
  public fun lift(map: (focus: A) -> B): (source: S) -> T =
    { s -> modify(s) { map(it) } }

  /**
   * Join two [PTraversal] with the same target
   */
  public fun <U, V> choice(other: PTraversal<U, V, A, B>): PTraversal<Either<S, U>, Either<T, V>, A, B> =
    object : PTraversal<Either<S, U>, Either<T, V>, A, B> {
      override fun <R> foldMap(M: Monoid<R>, source: Either<S, U>, map: (A) -> R): R =
        source.fold(
          { a -> this@PTraversal.foldMap(M, a, map) },
          { u -> other.foldMap(M, u, map) }
        )

      override fun modify(source: Either<S, U>, map: (focus: A) -> B): Either<T, V> =
        source.fold(
          { a -> Either.Left(this@PTraversal.modify(a, map)) },
          { u -> Either.Right(other.modify(u, map)) }
        )
    }

  /**
   * Compose a [PTraversal] with a [PTraversal]
   */
  public infix fun <C, D> compose(other: PTraversal<in A, out B, out C, in D>): PTraversal<S, T, C, D> =
    object : PTraversal<S, T, C, D> {
      override fun <R> foldMap(M: Monoid<R>, source: S, map: (C) -> R): R =
        this@PTraversal.foldMap(M, source) { c -> other.foldMap(M, c, map) }

      override fun modify(source: S, map: (focus: C) -> D): T =
        this@PTraversal.modify(source) { b -> other.modify(b, map) }
    }

  public operator fun <C, D> plus(other: PTraversal<in A, out B, out C, in D>): PTraversal<S, T, C, D> =
    this compose other

  /**
   * Calculate the number of targets
   */
  public fun size(source: S): Int =
    foldMap(Monoid.int(), source) { 1 }

  /**
   * Check if all targets satisfy the predicate
   */
  public fun all(source: S, predicate: (focus: A) -> Boolean): Boolean =
    foldMap(Monoid.boolean(), source, predicate)

  /**
   * Returns `true` if at least one focus matches the given [predicate].
   */
  public fun any(source: S, predicate: (focus: A) -> Boolean): Boolean =
    foldMap(Monoid.booleanOr(), source, predicate)

  /**
   * Check if there is no target
   */
  public fun isEmpty(source: S): Boolean =
    foldMap(Monoid.boolean(), source) { false }

  /**
   * Check if there is at least one target
   */
  public fun isNotEmpty(source: S): Boolean =
    !isEmpty(source)

  /**
   * Get the first target or null
   */
  @Suppress("UNCHECKED_CAST")
  public fun firstOrNull(source: S): A? {
    val fold = ::fold as (Monoid<Any?>, S) -> Any?
    val res = fold(object : Monoid<Any?> {
      override fun empty(): Any = EMPTY_VALUE
      override fun append(a: Any?, b: Any?): Any? =
        if (a === EMPTY_VALUE) b else a
    }, source)
    return EMPTY_VALUE.unbox(res)
  }

  /**
   * Get the last target or null
   */
  @Suppress("UNCHECKED_CAST")
  public fun lastOrNull(source: S): A? {
    val fold = ::fold as (Monoid<Any?>, S) -> Any?
    val res = fold(object : Monoid<Any?> {
      override fun empty(): Any = EMPTY_VALUE
      override fun append(a: Any?, b: Any?): Any? =
        if (b !== EMPTY_VALUE) b else a
    }, source)
    return EMPTY_VALUE.unbox(res)
  }

  /**
   * Fold using the given [Monoid] instance.
   */
  public fun fold(M: Monoid<@UnsafeVariance A>, source: S): A =
    foldMap(M, source, ::identity)

  /**
   * Alias for fold.
   */
  @Deprecated("use fold instead", ReplaceWith("fold(M, source)"))
  public fun combineAll(M: Monoid<@UnsafeVariance A>, source: S): A =
    fold(M, source)

  /**
   * Get all targets of the [Traversal]
   */
  public fun getAll(source: S): List<A> =
    foldMap(Monoid.list(), source, ::listOf)

  /**
   * Find the first element matching the predicate, if one exists.
   */
  public fun findOrNull(source: S, predicate: (focus: A) -> Boolean): A? {
    val res = foldMap(object : Monoid<Any?> {
      override fun empty(): Any = EMPTY_VALUE
      override fun append(a: Any?, b: Any?): Any? =
        if (a === EMPTY_VALUE) b else a
    }, source) { focus -> if (predicate(focus)) focus else EMPTY_VALUE }
    return EMPTY_VALUE.unbox(res)
  }

  /**
   * Check whether at least one element satisfies the predicate.
   *
   * If there are no elements, the result is false.
   */
  public fun exists(source: S, predicate: (focus: A) -> Boolean): Boolean {
    val res = foldMap(object : Monoid<Any?> {
      override fun empty(): Any = EMPTY_VALUE
      override fun append(a: Any?, b: Any?): Any? =
        if (a === EMPTY_VALUE) b else a
    }, source) { focus -> if (predicate(focus)) focus else EMPTY_VALUE }
    return res !== EMPTY_VALUE
  }

  public companion object {

    public fun <S> id(): Traversal<S, S> = PLens.id()

    public fun <S> codiagonal(): Traversal<Either<S, S>, S> =
      object : Traversal<Either<S, S>, S> {
        override fun <R> foldMap(M: Monoid<R>, source: Either<S, S>, map: (focus: S) -> R): R =
          source.fold(
            { a -> map(a) },
            { u -> map(u) }
          )

        override fun modify(source: Either<S, S>, map: (focus: S) -> S): Either<S, S> =
          source.bimap(map, map)
      }
      // Traversal { s, f -> s.bimap(f, f) }

    /**
     * [PTraversal] that points to nothing
     */
    public fun <S, A> void(): Traversal<S, A> =
      POptional.void()

    /**
     * [PTraversal] constructor from multiple getters of the same source.
     */
    public operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      set: (B, B, S) -> T
    ): PTraversal<S, T, A, B> =
      object : PTraversal<S, T, A, B> {
        override fun <R> foldMap(M: Monoid<R>, s: S, f: (focus: A) -> R): R =
          M.fold(listOf(f(get1(s)), f(get2(s))))
        override fun modify(s: S, f: (focus: A) -> B): T =
          set(f(get1(s)), f(get2(s)), s)
      }

    public operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      set: (B, B, B, S) -> T
    ): PTraversal<S, T, A, B> =
      object : PTraversal<S, T, A, B> {
        override fun <R> foldMap(M: Monoid<R>, s: S, f: (focus: A) -> R): R =
          M.fold(listOf(f(get1(s)), f(get2(s)), f(get3(s))))
        override fun modify(s: S, f: (focus: A) -> B): T =
          set(f(get1(s)), f(get2(s)), f(get3(s)), s)
      }

    public operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      set: (B, B, B, B, S) -> T
    ): PTraversal<S, T, A, B> =
      object : PTraversal<S, T, A, B> {
        override fun <R> foldMap(M: Monoid<R>, s: S, f: (focus: A) -> R): R =
          M.fold(listOf(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s))))
        override fun modify(s: S, f: (focus: A) -> B): T =
          set(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), s)
      }

    public operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      get5: (S) -> A,
      set: (B, B, B, B, B, S) -> T
    ): PTraversal<S, T, A, B> =
      object : PTraversal<S, T, A, B> {
        override fun <R> foldMap(M: Monoid<R>, s: S, f: (focus: A) -> R): R =
          M.fold(listOf(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s))))
        override fun modify(s: S, f: (focus: A) -> B): T =
          set(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), s)
      }

    public operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      get5: (S) -> A,
      get6: (S) -> A,
      set: (B, B, B, B, B, B, S) -> T
    ): PTraversal<S, T, A, B> =
      object : PTraversal<S, T, A, B> {
        override fun <R> foldMap(M: Monoid<R>, s: S, f: (focus: A) -> R): R =
          M.fold(listOf(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s))))
        override fun modify(s: S, f: (focus: A) -> B): T =
          set(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s)), s)
      }

    public operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      get5: (S) -> A,
      get6: (S) -> A,
      get7: (S) -> A,
      set: (B, B, B, B, B, B, B, S) -> T
    ): PTraversal<S, T, A, B> =
      object : PTraversal<S, T, A, B> {
        override fun <R> foldMap(M: Monoid<R>, s: S, f: (focus: A) -> R): R =
          M.fold(listOf(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s)), f(get7(s))))
        override fun modify(s: S, f: (focus: A) -> B): T =
          set(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s)), f(get7(s)), s)
      }

    public operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      get5: (S) -> A,
      get6: (S) -> A,
      get7: (S) -> A,
      get8: (S) -> A,
      set: (B, B, B, B, B, B, B, B, S) -> T
    ): PTraversal<S, T, A, B> =
      object : PTraversal<S, T, A, B> {
        override fun <R> foldMap(M: Monoid<R>, s: S, f: (focus: A) -> R): R =
          M.fold(listOf(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s)), f(get7(s)), f(get8(s))))
        override fun modify(s: S, f: (focus: A) -> B): T =
          set(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s)), f(get7(s)), f(get8(s)), s)
      }

    public operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      get5: (S) -> A,
      get6: (S) -> A,
      get7: (S) -> A,
      get8: (S) -> A,
      get9: (S) -> A,
      set: (B, B, B, B, B, B, B, B, B, S) -> T
    ): PTraversal<S, T, A, B> =
      object : PTraversal<S, T, A, B> {
        override fun <R> foldMap(M: Monoid<R>, s: S, f: (focus: A) -> R): R =
          M.fold(listOf(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s)), f(get7(s)), f(get8(s)), f(get9(s))))
        override fun modify(s: S, f: (focus: A) -> B): T =
          set(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s)), f(get7(s)), f(get8(s)), f(get9(s)), s)
      }

    public operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      get5: (S) -> A,
      get6: (S) -> A,
      get7: (S) -> A,
      get8: (S) -> A,
      get9: (S) -> A,
      get10: (S) -> A,
      set: (B, B, B, B, B, B, B, B, B, B, S) -> T
    ): PTraversal<S, T, A, B> =
      object : PTraversal<S, T, A, B> {
        override fun <R> foldMap(M: Monoid<R>, s: S, f: (focus: A) -> R): R =
          M.fold(listOf(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s)), f(get7(s)), f(get8(s)), f(get9(s)), f(get10(s))))
        override fun modify(s: S, f: (focus: A) -> B): T =
          set(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s)), f(get7(s)), f(get8(s)), f(get9(s)), f(get10(s)), s)
      }

    /**
     * [Traversal] for [List] that focuses in each [A] of the source [List].
     */
    @JvmStatic
    public fun <A> list(): Traversal<List<A>, A> =
      Every.list()

    /**
     * [Traversal] for [Either] that has focus in each [Either.Right].
     *
     * @receiver [PTraversal.Companion] to make it statically available.
     * @return [Traversal] with source [Either] and focus every [Either.Right] of the source.
     */
    @JvmStatic
    public fun <L, R> either(): Traversal<Either<L, R>, R> =
      Every.either()

    @JvmStatic
    public fun <K, V> map(): Traversal<Map<K, V>, V> =
      Every.map()

    /**
     * [Traversal] for [NonEmptyList] that has focus in each [A].
     *
     * @receiver [PTraversal.Companion] to make it statically available.
     * @return [Traversal] with source [NonEmptyList] and focus every [A] of the source.
     */
    @JvmStatic
    public fun <A> nonEmptyList(): Traversal<NonEmptyList<A>, A> =
      Every.nonEmptyList()

    /**
     * [Traversal] for [Option] that has focus in each [arrow.core.Some].
     *
     * @receiver [PTraversal.Companion] to make it statically available.
     * @return [Traversal] with source [Option] and focus in every [arrow.core.Some] of the source.
     */
    @JvmStatic
    public fun <A> option(): Traversal<Option<A>, A> =
      Every.option()

    @JvmStatic
    public fun <A> sequence(): Traversal<Sequence<A>, A> =
      Every.sequence()

    /**
     * [Traversal] for [String] that focuses in each [Char] of the source [String].
     *
     * @receiver [PTraversal.Companion] to make it statically available.
     * @return [Traversal] with source [String] and foci every [Char] in the source.
     */
    @JvmStatic
    public fun string(): Traversal<String, Char> =
      Every.string()

    /**
     * [PTraversal] to focus into the first and second value of a [Pair]
     */
    @JvmStatic
    public fun <A, B> pPair(): PTraversal<Pair<A, A>, Pair<B, B>, A, B> =
      PTraversal(
        get1 = { it.first },
        get2 = { it.second },
        set = { a, b, _ -> a to b }
      )

    /**
     * [Traversal] to focus into the first and second value of a [Pair]
     */
    @JvmStatic
    public fun <A> pair(): Traversal<Pair<A, A>, A> =
      pPair()

    /**
     * [PTraversal] to focus into the first, second and third value of a [Triple]
     */
    @JvmStatic
    public fun <A, B> pTriple(): PTraversal<Triple<A, A, A>, Triple<B, B, B>, A, B> =
      PTraversal(
        get1 = { it.first },
        get2 = { it.second },
        get3 = { it.third },
        set = { a, b, c, _ -> Triple(a, b, c) }
      )

    /**
     * [Traversal] to focus into the first, second and third value of a [Triple]
     */
    @JvmStatic
    public fun <A> triple(): Traversal<Triple<A, A, A>, A> =
      pTriple()

    /**
     * [PTraversal] to focus into the first, second, third and fourth value of a [arrow.core.Tuple4]
     */
    @JvmStatic
    public fun <A, B> pTuple4(): PTraversal<Tuple4<A, A, A, A>, Tuple4<B, B, B, B>, A, B> =
      PTraversal(
        get1 = { it.first },
        get2 = { it.second },
        get3 = { it.third },
        get4 = { it.fourth },
        set = { a, b, c, d, _ -> Tuple4(a, b, c, d) }
      )

    /**
     * [Traversal] to focus into the first, second, third and fourth value of a [arrow.core.Tuple4]
     */
    @JvmStatic
    public fun <A> tuple4(): Traversal<Tuple4<A, A, A, A>, A> =
      pTuple4()

    /**
     * [PTraversal] to focus into the first, second, third, fourth and fifth value of a [arrow.core.Tuple5]
     */
    @JvmStatic
    public fun <A, B> pTuple5(): PTraversal<Tuple5<A, A, A, A, A>, Tuple5<B, B, B, B, B>, A, B> =
      PTraversal(
        get1 = { it.first },
        get2 = { it.second },
        get3 = { it.third },
        get4 = { it.fourth },
        get5 = { it.fifth },
        set = { a, b, c, d, e, _ -> Tuple5(a, b, c, d, e) }
      )

    /**
     * [Traversal] to focus into the first, second, third, fourth and fifth value of a [arrow.core.Tuple5]
     */
    @JvmStatic
    public fun <A> tuple5(): Traversal<Tuple5<A, A, A, A, A>, A> =
      pTuple5()

    /**
     * [PTraversal] to focus into the first, second, third, fourth, fifth and sixth value of a [arrow.core.Tuple6]
     */
    @JvmStatic
    public fun <A, B> pTuple6(): PTraversal<Tuple6<A, A, A, A, A, A>, Tuple6<B, B, B, B, B, B>, A, B> =
      PTraversal(
        get1 = { it.first },
        get2 = { it.second },
        get3 = { it.third },
        get4 = { it.fourth },
        get5 = { it.fifth },
        get6 = { it.sixth },
        set = { a, b, c, d, e, f, _ -> Tuple6(a, b, c, d, e, f) }
      )

    /**
     * [Traversal] to focus into the first, second, third, fourth, fifth and sixth value of a [arrow.core.Tuple6]
     */
    @JvmStatic
    public fun <A> tuple6(): Traversal<Tuple6<A, A, A, A, A, A>, A> =
      pTuple6()

    /**
     * [PTraversal] to focus into the first, second, third, fourth, fifth, sixth and seventh value of a [arrow.core.Tuple7]
     */
    @JvmStatic
    public fun <A, B> pTuple7(): PTraversal<Tuple7<A, A, A, A, A, A, A>, Tuple7<B, B, B, B, B, B, B>, A, B> =
      PTraversal(
        get1 = { it.first },
        get2 = { it.second },
        get3 = { it.third },
        get4 = { it.fourth },
        get5 = { it.fifth },
        get6 = { it.sixth },
        get7 = { it.seventh },
        set = { a, b, c, d, e, f, g, _ -> Tuple7(a, b, c, d, e, f, g) }
      )

    /**
     * [Traversal] to focus into the first, second, third, fourth, fifth, sixth and seventh value of a [arrow.core.Tuple7]
     */
    @JvmStatic
    public fun <A> tuple7(): Traversal<Tuple7<A, A, A, A, A, A, A>, A> =
      pTuple7()

    /**
     * [PTraversal] to focus into the first, second, third, fourth, fifth, sixth, seventh and eight value of a [arrow.core.Tuple8]
     */
    @JvmStatic
    public fun <A, B> pTuple8(): PTraversal<Tuple8<A, A, A, A, A, A, A, A>, Tuple8<B, B, B, B, B, B, B, B>, A, B> =
      PTraversal(
        get1 = { it.first },
        get2 = { it.second },
        get3 = { it.third },
        get4 = { it.fourth },
        get5 = { it.fifth },
        get6 = { it.sixth },
        get7 = { it.seventh },
        get8 = { it.eighth },
        set = { a, b, c, d, e, f, g, h, _ -> Tuple8(a, b, c, d, e, f, g, h) }
      )

    /**
     * [Traversal] to focus into the first, second, third, fourth, fifth, sixth, seventh and eight value of a [arrow.core.Tuple8]
     */
    @JvmStatic
    public fun <A> tuple8(): Traversal<Tuple8<A, A, A, A, A, A, A, A>, A> =
      pTuple8()

    /**
     * [PTraversal] to focus into the first, second, third, fourth, fifth, sixth, seventh, eight and ninth value of a [arrow.core.Tuple9]
     */
    @JvmStatic
    public fun <A, B> pTuple9(): PTraversal<Tuple9<A, A, A, A, A, A, A, A, A>, Tuple9<B, B, B, B, B, B, B, B, B>, A, B> =
      PTraversal(
        get1 = { it.first },
        get2 = { it.second },
        get3 = { it.third },
        get4 = { it.fourth },
        get5 = { it.fifth },
        get6 = { it.sixth },
        get7 = { it.seventh },
        get8 = { it.eighth },
        get9 = { it.ninth },
        set = { a, b, c, d, e, f, g, h, i, _ -> Tuple9(a, b, c, d, e, f, g, h, i) }
      )

    /**
     * [Traversal] to focus into the first, second, third, fourth, fifth, sixth, seventh, eight and ninth value of a [arrow.core.Tuple9]
     */
    @JvmStatic
    public fun <A> tuple9(): Traversal<Tuple9<A, A, A, A, A, A, A, A, A>, A> =
      pTuple9()

    /**
     * [PTraversal] to focus into the first, second, third, fourth, fifth, sixth, seventh, eight, ninth and tenth value of a [arrow.core.Tuple10]
     */
    @JvmStatic
    public fun <A, B> pTuple10(): PTraversal<Tuple10<A, A, A, A, A, A, A, A, A, A>, Tuple10<B, B, B, B, B, B, B, B, B, B>, A, B> =
      PTraversal(
        get1 = { it.first },
        get2 = { it.second },
        get3 = { it.third },
        get4 = { it.fourth },
        get5 = { it.fifth },
        get6 = { it.sixth },
        get7 = { it.seventh },
        get8 = { it.eighth },
        get9 = { it.ninth },
        get10 = { it.tenth },
        set = { a, b, c, d, e, f, g, h, i, j, _ -> Tuple10(a, b, c, d, e, f, g, h, i, j) }
      )

    /**
     * [Traversal] to focus into the first, second, third, fourth, fifth, sixth, seventh, eight, ninth and tenth value of a [arrow.core.Tuple10]
     */
    @JvmStatic
    public fun <A> tuple10(): Traversal<Tuple10<A, A, A, A, A, A, A, A, A, A>, A> =
      pTuple10()
  }

  /**
   * DSL to compose [Traversal] with a [Lens] for a structure [S] to see all its foci [A]
   *
   * @receiver [Lens] with a focus in [S]
   * @return [Traversal] with a focus in [A]
   */
  public val <U, V> PLens<U, V, S, T>.every: PTraversal<U, V, A, B>
    get() =
      this@every.compose(this@PTraversal)

  /**
   * DSL to compose [Traversal] with a [Prism] for a structure [S] to see all its foci [A]
   *
   * @receiver [Prism] with a focus in [S]
   * @return [Traversal] with a focus in [A]
   */
  public val <U, V> PPrism<U, V, S, T>.every: PTraversal<U, V, A, B>
    get() =
      this.compose(this@PTraversal)

  /**
   * DSL to compose [Traversal] with a [Optional] for a structure [S] to see all its foci [A]
   *
   * @receiver [Optional] with a focus in [S]
   * @return [Traversal] with a focus in [A]
   */
  public val <U, V> POptional<U, V, S, T>.every: PTraversal<U, V, A, B>
    get() =
      this.compose(this@PTraversal)

  /**
   * DSL to compose [Traversal] with a [Traversal] for a structure [S] to see all its foci [A]
   *
   * @receiver [Traversal] with a focus in [S]
   * @return [Traversal] with a focus in [A]
   */
  public val <U, V> PTraversal<U, V, S, T>.every: PTraversal<U, V, A, B>
    get() =
      this.compose(this@PTraversal)

}
