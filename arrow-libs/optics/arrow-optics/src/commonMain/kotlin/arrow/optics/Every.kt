package arrow.optics

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.Option
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9
import arrow.core.fold
import kotlin.jvm.JvmStatic

public object Every {
  /**
   * [Traversal] for [List] that focuses in each [A] of the source [List].
   */
  @JvmStatic
  public fun <A> list(): Traversal<List<A>, A> = object : Traversal<List<A>, A> {
    override fun <R> foldMap(initial: R, combine: (R, R) -> R, source: List<A>, map: (focus: A) -> R): R =
      source.fold(initial) { acc, a -> combine(acc, map(a)) }

    override fun modify(source: List<A>, map: (focus: A) -> A): List<A> =
      source.map(map)
  }

  /**
   * [Traversal] for [Either] that has focus in each [Either.Right].
   *
   * @receiver [PTraversal.Companion] to make it statically available.
   * @return [Traversal] with source [Either] and focus every [Either.Right] of the source.
   */
  @JvmStatic
  public fun <L, A> either(): Traversal<Either<L, A>, A> = object : Traversal<Either<L, A>, A> {
    override fun <R> foldMap(initial: R, combine: (R, R) -> R, source: Either<L, A>, map: (focus: A) -> R): R =
      source.fold({ initial }) { combine(initial, map(it)) }

    override fun modify(source: Either<L, A>, map: (focus: A) -> A): Either<L, A> =
      source.map(map)
  }

  @JvmStatic
  public fun <K, V> map(): Traversal<Map<K, V>, V> = object : Traversal<Map<K, V>, V> {
    override fun <R> foldMap(initial: R, combine: (R, R) -> R, source: Map<K, V>, map: (focus: V) -> R): R =
      source.fold(initial) { acc, (_, v) ->
        combine(acc, map(v))
      }

    override fun modify(source: Map<K, V>, map: (focus: V) -> V): Map<K, V> =
      source.mapValues { (_, v) -> map(v) }
  }

  /**
   * [Traversal] for [NonEmptyList] that has focus in each [A].
   *
   * @receiver [PTraversal.Companion] to make it statically available.
   * @return [Traversal] with source [NonEmptyList] and focus every [A] of the source.
   */
  @JvmStatic
  public fun <A> nonEmptyList(): Traversal<NonEmptyList<A>, A> = object : Traversal<NonEmptyList<A>, A> {
    override fun <R> foldMap(initial: R, combine: (R, R) -> R, source: NonEmptyList<A>, map: (focus: A) -> R): R =
      source.fold(initial) { acc, a ->
        combine(acc, map(a))
      }

    override fun modify(source: NonEmptyList<A>, map: (focus: A) -> A): NonEmptyList<A> =
      source.map(map)
  }

  /**
   * [Traversal] for [Option] that has focus in each [arrow.core.Some].
   *
   * @receiver [PTraversal.Companion] to make it statically available.
   * @return [Traversal] with source [Option] and focus in every [arrow.core.Some] of the source.
   */
  @JvmStatic
  public fun <A> option(): Traversal<Option<A>, A> = object : Traversal<Option<A>, A> {
    override fun <R> foldMap(initial: R, combine: (R, R) -> R, source: Option<A>, map: (focus: A) -> R): R =
      source.fold({ initial }) { combine(initial, map(it)) }

    override fun modify(source: Option<A>, map: (focus: A) -> A): Option<A> =
      source.map(map)
  }

  @JvmStatic
  public fun <A> sequence(): Traversal<Sequence<A>, A> = object : Traversal<Sequence<A>, A> {
    override fun <R> foldMap(initial: R, combine: (R, R) -> R, source: Sequence<A>, map: (focus: A) -> R): R =
      source.fold(initial) { acc, a ->
        combine(acc, map(a))
      }

    override fun modify(source: Sequence<A>, map: (focus: A) -> A): Sequence<A> =
      source.map(map)
  }

  /**
   * [Traversal] for [String] that focuses in each [Char] of the source [String].
   *
   * @receiver [PTraversal.Companion] to make it statically available.
   * @return [Traversal] with source [String] and foci every [Char] in the source.
   */
  @JvmStatic
  public fun string(): Traversal<String, Char> = object : Traversal<String, Char> {
    override fun <R> foldMap(initial: R, combine: (R, R) -> R, source: String, map: (focus: Char) -> R): R =
      source.fold(initial) { acc, a ->
        combine(acc, map(a))
      }

    override fun modify(source: String, map: (focus: Char) -> Char): String =
      source.map(map).joinToString(separator = "")
  }

  /**
   * [Traversal] to focus into the first and second value of a [Pair]
   */
  @JvmStatic
  public fun <A> pair(): Traversal<Pair<A, A>, A> =
    object : Traversal<Pair<A, A>, A> {
      override fun modify(source: Pair<A, A>, map: (focus: A) -> A): Pair<A, A> =
        Pair(map(source.first), map(source.second))

      override fun <R> foldMap(initial: R, combine: (R, R) -> R, source: Pair<A, A>, map: (focus: A) -> R): R =
        combine(map(source.first), map(source.second))
    }

  /**
   * [Traversal] to focus into the first, second and third value of a [Triple]
   */
  @JvmStatic
  public fun <A> triple(): Traversal<Triple<A, A, A>, A> =
    object : Traversal<Triple<A, A, A>, A> {
      override fun modify(source: Triple<A, A, A>, map: (focus: A) -> A): Triple<A, A, A> =
        Triple(map(source.first), map(source.second), map(source.third))

      override fun <R> foldMap(initial: R, combine: (R, R) -> R, source: Triple<A, A, A>, map: (focus: A) -> R): R =
        combine(combine(map(source.first), map(source.second)), map(source.third))
    }

  /**
   * [Traversal] to focus into the first, second, third and fourth value of a [arrow.core.Tuple4]
   */
  @JvmStatic
  public fun <A> tuple4(): Traversal<Tuple4<A, A, A, A>, A> =
    object : Traversal<Tuple4<A, A, A, A>, A> {
      override fun <R> foldMap(initial: R, combine: (R, R) -> R, source: Tuple4<A, A, A, A>, map: (focus: A) -> R): R =
        combine(combine(combine(map(source.first), map(source.second)), map(source.third)), map(source.fourth))

      override fun modify(source: Tuple4<A, A, A, A>, map: (focus: A) -> A): Tuple4<A, A, A, A> =
        Tuple4(map(source.first), map(source.second), map(source.third), map(source.fourth))
    }

  /**
   * [PTraversal] to focus into the first, second, third, fourth and fifth value of a [arrow.core.Tuple5]
   */
  @JvmStatic
  public fun <A> tuple5(): Traversal<Tuple5<A, A, A, A, A>, A> =
    object : Traversal<Tuple5<A, A, A, A, A>, A> {
      override fun <R> foldMap(
        initial: R,
        combine: (R, R) -> R,
        source: Tuple5<A, A, A, A, A>,
        map: (focus: A) -> R
      ): R =
        combine(
          combine(combine(combine(map(source.first), map(source.second)), map(source.third)), map(source.fourth)),
          map(source.fifth)
        )

      override fun modify(source: Tuple5<A, A, A, A, A>, map: (focus: A) -> A): Tuple5<A, A, A, A, A> =
        Tuple5(map(source.first), map(source.second), map(source.third), map(source.fourth), map(source.fifth))
    }

  /**
   * [Traversal] to focus into the first, second, third, fourth, fifth and sixth value of a [arrow.core.Tuple6]
   */
  @JvmStatic
  public fun <A> tuple6(): Traversal<Tuple6<A, A, A, A, A, A>, A> =
    object : Traversal<Tuple6<A, A, A, A, A, A>, A> {
      override fun <R> foldMap(
        initial: R,
        combine: (R, R) -> R,
        source: Tuple6<A, A, A, A, A, A>,
        map: (focus: A) -> R
      ): R =
        combine(
          combine(
            combine(
              combine(combine(map(source.first), map(source.second)), map(source.third)),
              map(source.fourth)
            ), map(source.fifth)
          ), map(source.sixth)
        )

      override fun modify(source: Tuple6<A, A, A, A, A, A>, map: (focus: A) -> A): Tuple6<A, A, A, A, A, A> =
        Tuple6(
          map(source.first),
          map(source.second),
          map(source.third),
          map(source.fourth),
          map(source.fifth),
          map(source.sixth)
        )
    }

  /**
   * [Traversal] to focus into the first, second, third, fourth, fifth, sixth and seventh value of a [arrow.core.Tuple7]
   */
  @JvmStatic
  public fun <A> tuple7(): Traversal<Tuple7<A, A, A, A, A, A, A>, A> =
    object : Traversal<Tuple7<A, A, A, A, A, A, A>, A> {
      override fun <R> foldMap(
        initial: R,
        combine: (R, R) -> R,
        source: Tuple7<A, A, A, A, A, A, A>,
        map: (focus: A) -> R
      ): R =
        combine(
          combine(
            combine(
              combine(
                combine(combine(map(source.first), map(source.second)), map(source.third)),
                map(source.fourth)
              ), map(source.fifth)
            ), map(source.sixth)
          ), map(source.seventh)
        )

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
    }

  /**
   * [Traversal] to focus into the first, second, third, fourth, fifth, sixth, seventh and eight value of a [arrow.core.Tuple8]
   */
  @JvmStatic
  public fun <A> tuple8(): Traversal<Tuple8<A, A, A, A, A, A, A, A>, A> =
    object : Traversal<Tuple8<A, A, A, A, A, A, A, A>, A> {
      override fun <R> foldMap(
        initial: R,
        combine: (R, R) -> R,
        source: Tuple8<A, A, A, A, A, A, A, A>,
        map: (focus: A) -> R
      ): R =
        combine(
          combine(
            combine(
              combine(
                combine(
                  combine(combine(map(source.first), map(source.second)), map(source.third)),
                  map(source.fourth)
                ), map(source.fifth)
              ), map(source.sixth)
            ), map(source.seventh)
          ), map(source.eighth)
        )

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
    }

  /**
   * [Traversal] to focus into the first, second, third, fourth, fifth, sixth, seventh, eight and ninth value of a [arrow.core.Tuple9]
   */
  @JvmStatic
  public fun <A> tuple9(): Traversal<Tuple9<A, A, A, A, A, A, A, A, A>, A> =
    object : Traversal<Tuple9<A, A, A, A, A, A, A, A, A>, A> {
      override fun <R> foldMap(
        initial: R,
        combine: (R, R) -> R,
        source: Tuple9<A, A, A, A, A, A, A, A, A>,
        map: (focus: A) -> R
      ): R =
        combine(
          combine(
            combine(
              combine(
                combine(
                  combine(
                    combine(combine(map(source.first), map(source.second)), map(source.third)),
                    map(source.fourth)
                  ), map(source.fifth)
                ), map(source.sixth)
              ), map(source.seventh)
            ), map(source.eighth)
          ), map(source.ninth)
        )

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
    }
}
