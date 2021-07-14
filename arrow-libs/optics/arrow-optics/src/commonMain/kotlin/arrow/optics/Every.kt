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
import arrow.core.foldLeft
import arrow.core.foldMap
import arrow.typeclasses.Monoid
import kotlin.jvm.JvmStatic

public typealias Every<S, A> = PEvery<S, S, A, A>

/**
 * Composition of Fold and Traversal
 * It combines their powers
 */
public interface PEvery<S, T, A, B> : PTraversal<S, T, A, B>, Fold<S, A>, PSetter<S, T, A, B> {

  /**
   * Map each target to a type R and use a Monoid to fold the results
   */
  override fun <R> foldMap(M: Monoid<R>, source: S, map: (focus: A) -> R): R

  override fun modify(source: S, map: (focus: A) -> B): T

  /**
   * Compose a [PEvery] with a [PEvery]
   */
  public infix fun <C, D> compose(other: PEvery<in A, out B, out C, in D>): PEvery<S, T, C, D> =
    object : PEvery<S, T, C, D> {
      override fun <R> foldMap(M: Monoid<R>, source: S, map: (C) -> R): R =
        this@PEvery.foldMap(M, source) { c -> other.foldMap(M, c, map) }

      override fun modify(source: S, map: (focus: C) -> D): T =
        this@PEvery.modify(source) { b -> other.modify(b, map) }
    }

  public operator fun <C, D> plus(other: PEvery<in A, out B, out C, in D>): PEvery<S, T, C, D> =
    this compose other

  public companion object {
    public fun <S, A> from(T: Traversal<S, A>, F: Fold<S, A>): Every<S, A> =
      object : Every<S, A> {
        override fun <R> foldMap(M: Monoid<R>, source: S, map: (A) -> R): R = F.foldMap(M, source, map)
        override fun modify(source: S, map: (focus: A) -> A): S = T.modify(source, map)
      }

    /**
     * [Traversal] for [List] that focuses in each [A] of the source [List].
     */
    @JvmStatic
    public fun <A> list(): Every<List<A>, A> =
      object : Every<List<A>, A> {
        override fun modify(source: List<A>, map: (focus: A) -> A): List<A> =
          source.map(map)

        override fun <R> foldMap(M: Monoid<R>, source: List<A>, map: (focus: A) -> R): R =
          source.foldMap(M, map)
      }

    /**
     * [Traversal] for [Either] that has focus in each [Either.Right].
     *
     * @receiver [Traversal.Companion] to make it statically available.
     * @return [Traversal] with source [Either] and focus every [Either.Right] of the source.
     */
    @JvmStatic
    public fun <L, R> either(): Every<Either<L, R>, R> =
      object : Every<Either<L, R>, R> {
        override fun modify(source: Either<L, R>, map: (focus: R) -> R): Either<L, R> =
          source.map(map)

        override fun <A> foldMap(M: Monoid<A>, source: Either<L, R>, map: (focus: R) -> A): A =
          source.foldMap(M, map)
      }

    @JvmStatic
    public fun <K, V> map(): Every<Map<K, V>, V> =
      object : Every<Map<K, V>, V> {
        override fun modify(source: Map<K, V>, map: (focus: V) -> V): Map<K, V> =
          source.mapValues { (_, v) -> map(v) }

        override fun <R> foldMap(M: Monoid<R>, source: Map<K, V>, map: (focus: V) -> R): R =
          M.run {
            source.foldLeft(empty()) { acc, (_, v) ->
              acc.combine(map(v))
            }
          }
      }

    /**
     * [Traversal] for [NonEmptyList] that has focus in each [A].
     *
     * @receiver [PTraversal.Companion] to make it statically available.
     * @return [Traversal] with source [NonEmptyList] and focus every [A] of the source.
     */
    @JvmStatic
    public fun <A> nonEmptyList(): Every<NonEmptyList<A>, A> =
      object : Every<NonEmptyList<A>, A> {
        override fun modify(source: NonEmptyList<A>, map: (focus: A) -> A): NonEmptyList<A> =
          source.map(map)

        override fun <R> foldMap(M: Monoid<R>, source: NonEmptyList<A>, map: (focus: A) -> R): R =
          source.foldMap(M, map)
      }

    /**
     * [Traversal] for [Option] that has focus in each [arrow.core.Some].
     *
     * @receiver [PTraversal.Companion] to make it statically available.
     * @return [Traversal] with source [Option] and focus in every [arrow.core.Some] of the source.
     */
    @JvmStatic
    public fun <A> option(): Every<Option<A>, A> =
      object : Every<Option<A>, A> {
        override fun modify(source: Option<A>, map: (focus: A) -> A): Option<A> =
          source.map(map)

        override fun <R> foldMap(M: Monoid<R>, source: Option<A>, map: (focus: A) -> R): R =
          source.foldMap(M, map)
      }

    @JvmStatic
    public fun <A> sequence(): Every<Sequence<A>, A> =
      object : Every<Sequence<A>, A> {
        override fun modify(source: Sequence<A>, map: (focus: A) -> A): Sequence<A> =
          source.map(map)

        override fun <R> foldMap(M: Monoid<R>, source: Sequence<A>, map: (focus: A) -> R): R =
          M.run {
            source.fold(empty()) { acc, a ->
              acc.combine(map(a))
            }
          }
      }

    /**
     * [Traversal] for [String] that focuses in each [Char] of the source [String].
     *
     * @receiver [PTraversal.Companion] to make it statically available.
     * @return [Traversal] with source [String] and foci every [Char] in the source.
     */
    @JvmStatic
    public fun string(): Every<String, Char> =
      object : Every<String, Char> {
        override fun modify(source: String, map: (focus: Char) -> Char): String =
          source.map(map).joinToString(separator = "")

        override fun <R> foldMap(M: Monoid<R>, source: String, map: (focus: Char) -> R): R =
          M.run {
            source.fold(empty()) { acc, char -> acc.combine(map(char)) }
          }
      }

    /**
     * [Traversal] to focus into the first and second value of a [Pair]
     */
    @JvmStatic
    public fun <A> pair(): Every<Pair<A, A>, A> =
      object : Every<Pair<A, A>, A> {
        override fun modify(source: Pair<A, A>, map: (focus: A) -> A): Pair<A, A> =
          Pair(map(source.first), map(source.second))

        override fun <R> foldMap(M: Monoid<R>, source: Pair<A, A>, map: (focus: A) -> R): R =
          M.run {
            map(source.first).combine(map(source.second))
          }
      }

    /**
     * [Traversal] to focus into the first, second and third value of a [Triple]
     */
    @JvmStatic
    public fun <A> triple(): Every<Triple<A, A, A>, A> =
      object : Every<Triple<A, A, A>, A> {
        override fun modify(source: Triple<A, A, A>, map: (focus: A) -> A): Triple<A, A, A> =
          Triple(map(source.first), map(source.second), map(source.third))

        override fun <R> foldMap(M: Monoid<R>, source: Triple<A, A, A>, map: (focus: A) -> R): R =
          M.run {
            map(source.first)
              .combine(map(source.second))
              .combine(map(source.third))
          }
      }

    /**
     * [Traversal] to focus into the first, second, third and fourth value of a [arrow.core.Tuple4]
     */
    @JvmStatic
    public fun <A> tuple4(): Every<Tuple4<A, A, A, A>, A> =
      object : Every<Tuple4<A, A, A, A>, A> {
        override fun modify(source: Tuple4<A, A, A, A>, map: (focus: A) -> A): Tuple4<A, A, A, A> =
          Tuple4(map(source.first), map(source.second), map(source.third), map(source.fourth))

        override fun <R> foldMap(M: Monoid<R>, source: Tuple4<A, A, A, A>, map: (focus: A) -> R): R =
          M.run {
            map(source.first)
              .combine(map(source.second))
              .combine(map(source.third))
              .combine(map(source.fourth))
          }
      }

    /**
     * [PTraversal] to focus into the first, second, third, fourth and fifth value of a [arrow.core.Tuple5]
     */
    @JvmStatic
    public fun <A> tuple5(): Every<Tuple5<A, A, A, A, A>, A> =
      object : Every<Tuple5<A, A, A, A, A>, A> {
        override fun modify(source: Tuple5<A, A, A, A, A>, map: (focus: A) -> A): Tuple5<A, A, A, A, A> =
          Tuple5(map(source.first), map(source.second), map(source.third), map(source.fourth), map(source.fifth))

        override fun <R> foldMap(M: Monoid<R>, source: Tuple5<A, A, A, A, A>, map: (focus: A) -> R): R =
          M.run {
            map(source.first)
              .combine(map(source.second))
              .combine(map(source.third))
              .combine(map(source.fourth))
              .combine(map(source.fifth))
          }
      }

    /**
     * [Traversal] to focus into the first, second, third, fourth, fifth and sixth value of a [arrow.core.Tuple6]
     */
    @JvmStatic
    public fun <A> tuple6(): Every<Tuple6<A, A, A, A, A, A>, A> =
      object : Every<Tuple6<A, A, A, A, A, A>, A> {
        override fun modify(source: Tuple6<A, A, A, A, A, A>, map: (focus: A) -> A): Tuple6<A, A, A, A, A, A> =
          Tuple6(
            map(source.first),
            map(source.second),
            map(source.third),
            map(source.fourth),
            map(source.fifth),
            map(source.sixth)
          )

        override fun <R> foldMap(M: Monoid<R>, source: Tuple6<A, A, A, A, A, A>, map: (focus: A) -> R): R =
          M.run {
            map(source.first)
              .combine(map(source.second))
              .combine(map(source.third))
              .combine(map(source.fourth))
              .combine(map(source.fifth))
              .combine(map(source.sixth))
          }
      }

    /**
     * [Traversal] to focus into the first, second, third, fourth, fifth, sixth and seventh value of a [arrow.core.Tuple7]
     */
    @JvmStatic
    public fun <A> tuple7(): Every<Tuple7<A, A, A, A, A, A, A>, A> =
      object : Every<Tuple7<A, A, A, A, A, A, A>, A> {
        override fun modify(source: Tuple7<A, A, A, A, A, A, A>, map: (focus: A) -> A): Tuple7<A, A, A, A, A, A, A> =
          Tuple7(
            map(source.first),
            map(source.second),
            map(source.third),
            map(source.fourth),
            map(source.fifth),
            map(source.sixth),
            map(source.seventh)
          )

        override fun <R> foldMap(M: Monoid<R>, source: Tuple7<A, A, A, A, A, A, A>, map: (focus: A) -> R): R =
          M.run {
            map(source.first)
              .combine(map(source.second))
              .combine(map(source.third))
              .combine(map(source.fourth))
              .combine(map(source.fifth))
              .combine(map(source.sixth))
              .combine(map(source.seventh))
          }
      }

    /**
     * [Traversal] to focus into the first, second, third, fourth, fifth, sixth, seventh and eight value of a [arrow.core.Tuple8]
     */
    @JvmStatic
    public fun <A> tuple8(): Every<Tuple8<A, A, A, A, A, A, A, A>, A> =
      object : Every<Tuple8<A, A, A, A, A, A, A, A>, A> {
        override fun modify(
          source: Tuple8<A, A, A, A, A, A, A, A>,
          map: (focus: A) -> A
        ): Tuple8<A, A, A, A, A, A, A, A> =
          Tuple8(
            map(source.first),
            map(source.second),
            map(source.third),
            map(source.fourth),
            map(source.fifth),
            map(source.sixth),
            map(source.seventh),
            map(source.eighth)
          )

        override fun <R> foldMap(M: Monoid<R>, source: Tuple8<A, A, A, A, A, A, A, A>, map: (focus: A) -> R): R =
          M.run {
            map(source.first)
              .combine(map(source.second))
              .combine(map(source.third))
              .combine(map(source.fourth))
              .combine(map(source.fifth))
              .combine(map(source.sixth))
              .combine(map(source.seventh))
              .combine(map(source.eighth))
          }
      }

    /**
     * [Traversal] to focus into the first, second, third, fourth, fifth, sixth, seventh, eight and ninth value of a [arrow.core.Tuple9]
     */
    @JvmStatic
    public fun <A> tuple9(): Every<Tuple9<A, A, A, A, A, A, A, A, A>, A> =
      object : Every<Tuple9<A, A, A, A, A, A, A, A, A>, A> {
        override fun modify(
          source: Tuple9<A, A, A, A, A, A, A, A, A>,
          map: (focus: A) -> A
        ): Tuple9<A, A, A, A, A, A, A, A, A> =
          Tuple9(
            map(source.first),
            map(source.second),
            map(source.third),
            map(source.fourth),
            map(source.fifth),
            map(source.sixth),
            map(source.seventh),
            map(source.eighth),
            map(source.ninth)
          )

        override fun <R> foldMap(M: Monoid<R>, source: Tuple9<A, A, A, A, A, A, A, A, A>, map: (focus: A) -> R): R =
          M.run {
            map(source.first)
              .combine(map(source.second))
              .combine(map(source.third))
              .combine(map(source.fourth))
              .combine(map(source.fifth))
              .combine(map(source.sixth))
              .combine(map(source.seventh))
              .combine(map(source.eighth))
              .combine(map(source.ninth))
          }
      }

    /**
     * [Traversal] to focus into the first, second, third, fourth, fifth, sixth, seventh, eight, ninth and tenth value of a [arrow.core.Tuple10]
     */
    @JvmStatic
    public fun <A> tuple10(): Every<Tuple10<A, A, A, A, A, A, A, A, A, A>, A> =
      object : Every<Tuple10<A, A, A, A, A, A, A, A, A, A>, A> {
        override fun modify(
          source: Tuple10<A, A, A, A, A, A, A, A, A, A>,
          map: (focus: A) -> A
        ): Tuple10<A, A, A, A, A, A, A, A, A, A> =
          Tuple10(
            map(source.first),
            map(source.second),
            map(source.third),
            map(source.fourth),
            map(source.fifth),
            map(source.sixth),
            map(source.seventh),
            map(source.eighth),
            map(source.ninth),
            map(source.tenth)
          )

        override fun <R> foldMap(M: Monoid<R>, source: Tuple10<A, A, A, A, A, A, A, A, A, A>, map: (focus: A) -> R): R =
          M.run {
            map(source.first)
              .combine(map(source.second))
              .combine(map(source.third))
              .combine(map(source.fourth))
              .combine(map(source.fifth))
              .combine(map(source.sixth))
              .combine(map(source.seventh))
              .combine(map(source.eighth))
              .combine(map(source.ninth))
              .combine(map(source.tenth))
          }
      }
  }

  /**
   * DSL to compose [Every] with a [Lens] for a structure [S] to see all its foci [A]
   *
   * @receiver [Lens] with a focus in [S]
   * @return [Every] with a focus in [A]
   */
  override val <U, V> PLens<U, V, S, T>.every: PEvery<U, V, A, B>
    get() = this@every.compose(this@PEvery)

  /**
   * DSL to compose [Every] with a [Iso] for a structure [S] to see all its foci [A]
   *
   * @receiver [Iso] with a focus in [S]
   * @return [Every] with a focus in [A]
   */
  override val <U, V> PIso<U, V, S, T>.every: PEvery<U, V, A, B>
    get() = this@every.compose(this@PEvery)

  /**
   * DSL to compose [Every] with a [Prism] for a structure [S] to see all its foci [A]
   *
   * @receiver [Prism] with a focus in [S]
   * @return [Every] with a focus in [A]
   */
  override val <U, V> PPrism<U, V, S, T>.every: PEvery<U, V, A, B>
    get() = this.compose(this@PEvery)

  /**
   * DSL to compose [Every] with a [Optional] for a structure [S] to see all its foci [A]
   *
   * @receiver [Optional] with a focus in [S]
   * @return [Every] with a focus in [A]
   */
  override val <U, V> POptional<U, V, S, T>.every: PEvery<U, V, A, B>
    get() = this.compose(this@PEvery)

  /**
   * DSL to compose [Every] with a [Setter] for a structure [S] to see all its foci [A]
   *
   * @receiver [Setter] with a focus in [S]
   * @return [Setter] with a focus in [A]
   */
  override val <U, V> PSetter<U, V, S, T>.every: PSetter<U, V, A, B>
    get() = this.compose(this@PEvery)

  /**
   * DSL to compose [Every] with a [Traversal] for a structure [S] to see all its foci [A]
   *
   * @receiver [Traversal] with a focus in [S]
   * @return [Traversal] with a focus in [A]
   */
  override val <U, V> PTraversal<U, V, S, T>.every: PTraversal<U, V, A, B>
    get() = this.compose(this@PEvery)
}
