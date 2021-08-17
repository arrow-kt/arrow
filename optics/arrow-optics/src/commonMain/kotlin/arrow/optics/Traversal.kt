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
public fun interface PTraversal<S, T, A, B> : PSetter<S, T, A, B> {

  override fun modify(source: S, map: (focus: A) -> B): T

  public fun <U, V> choice(other: PTraversal<U, V, A, B>): PTraversal<Either<S, U>, Either<T, V>, A, B> =
    PTraversal { s, f ->
      s.fold(
        { a -> Either.Left(this@PTraversal.modify(a, f)) },
        { u -> Either.Right(other.modify(u, f)) }
      )
    }

  /**
   * Compose a [PTraversal] with a [PTraversal]
   */
  public infix fun <C, D> compose(other: PTraversal<in A, out B, out C, in D>): PTraversal<S, T, C, D> =
    PTraversal { s, f -> this@PTraversal.modify(s) { b -> other.modify(b, f) } }

  public operator fun <C, D> plus(other: PTraversal<in A, out B, out C, in D>): PTraversal<S, T, C, D> =
    this compose other

  public companion object {
    public fun <S> id(): PTraversal<S, S, S, S> =
      PIso.id()

    public fun <S> codiagonal(): Traversal<Either<S, S>, S> =
      Traversal { s, f -> s.bimap(f, f) }

    /**
     * [PTraversal] that points to nothing
     */
    public fun <S, A> void(): Traversal<S, A> =
      POptional.void()

    /**
     * [PTraversal] constructor from multiple getters of the same source.
     */
    public operator fun <S, T, A, B> invoke(get1: (S) -> A, get2: (S) -> A, set: (B, B, S) -> T): PTraversal<S, T, A, B> =
      PTraversal { s, f -> set(f(get1(s)), f(get2(s)), s) }

    public operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      set: (B, B, B, S) -> T
    ): PTraversal<S, T, A, B> =
      PTraversal { s, f -> set(f(get1(s)), f(get2(s)), f(get3(s)), s) }

    public operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      set: (B, B, B, B, S) -> T
    ): PTraversal<S, T, A, B> =
      PTraversal { s, f -> set(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), s) }

    public operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      get5: (S) -> A,
      set: (B, B, B, B, B, S) -> T
    ): PTraversal<S, T, A, B> =
      PTraversal { s, f -> set(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), s) }

    public operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      get5: (S) -> A,
      get6: (S) -> A,
      set: (B, B, B, B, B, B, S) -> T
    ): PTraversal<S, T, A, B> =
      PTraversal { s, f -> set(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s)), s) }

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
      PTraversal { s, f -> set(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s)), f(get7(s)), s) }

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
      PTraversal { s, f ->
        set(
          f(get1(s)),
          f(get2(s)),
          f(get3(s)),
          f(get4(s)),
          f(get5(s)),
          f(get6(s)),
          f(get7(s)),
          f(get8(s)),
          s
        )
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
      PTraversal { s, f ->
        set(
          f(get1(s)),
          f(get2(s)),
          f(get3(s)),
          f(get4(s)),
          f(get5(s)),
          f(get6(s)),
          f(get7(s)),
          f(get8(s)),
          f(get9(s)),
          s
        )
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
      PTraversal { s, f ->
        set(
          f(get1(s)),
          f(get2(s)),
          f(get3(s)),
          f(get4(s)),
          f(get5(s)),
          f(get6(s)),
          f(get7(s)),
          f(get8(s)),
          f(get9(s)),
          f(get10(s)),
          s
        )
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
     * @receiver [Traversal.Companion] to make it statically available.
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
   * DSL to compose [Traversal] with a [Iso] for a structure [S] to see all its foci [A]
   *
   * @receiver [Iso] with a focus in [S]
   * @return [Traversal] with a focus in [A]
   */
  public val <U, V> PIso<U, V, S, T>.every: PTraversal<U, V, A, B>
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
   * DSL to compose [Traversal] with a [Setter] for a structure [S] to see all its foci [A]
   *
   * @receiver [Setter] with a focus in [S]
   * @return [Setter] with a focus in [A]
   */
  public val <U, V> PSetter<U, V, S, T>.every: PSetter<U, V, A, B>
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

  /**
   * DSL to compose [Traversal] with a [PEvery] for a structure [S] to see all its foci [A]
   *
   * @receiver [PEvery] with a focus in [S]
   * @return [PEvery] with a focus in [A]
   */
  public val <U, V> PEvery<U, V, S, T>.every: PTraversal<U, V, A, B>
    get() =
      this.compose(this@PTraversal)
}
