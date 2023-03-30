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
import kotlin.jvm.JvmStatic

public object Every {
  /**
   * [Traversal] for [List] that focuses in each [A] of the source [List].
   */
  @JvmStatic
  public fun <A> list(): Traversal<List<A>, A> = Traversal.list()

  /**
   * [Traversal] for [Either] that has focus in each [Either.Right].
   *
   * @receiver [PTraversal.Companion] to make it statically available.
   * @return [Traversal] with source [Either] and focus every [Either.Right] of the source.
   */
  @JvmStatic
  public fun <L, R> either(): Traversal<Either<L, R>, R> =
    Traversal.either()

  @JvmStatic
  public fun <K, V> map(): Traversal<Map<K, V>, V> =
    Traversal.map()

  /**
   * [Traversal] for [NonEmptyList] that has focus in each [A].
   *
   * @receiver [PTraversal.Companion] to make it statically available.
   * @return [Traversal] with source [NonEmptyList] and focus every [A] of the source.
   */
  @JvmStatic
  public fun <A> nonEmptyList(): Traversal<NonEmptyList<A>, A> =
    Traversal.nonEmptyList()

  /**
   * [Traversal] for [Option] that has focus in each [arrow.core.Some].
   *
   * @receiver [PTraversal.Companion] to make it statically available.
   * @return [Traversal] with source [Option] and focus in every [arrow.core.Some] of the source.
   */
  @JvmStatic
  public fun <A> option(): Traversal<Option<A>, A> =
    Traversal.option()

  @JvmStatic
  public fun <A> sequence(): Traversal<Sequence<A>, A> =
    Traversal.sequence()

  /**
   * [Traversal] for [String] that focuses in each [Char] of the source [String].
   *
   * @receiver [PTraversal.Companion] to make it statically available.
   * @return [Traversal] with source [String] and foci every [Char] in the source.
   */
  @JvmStatic
  public fun string(): Traversal<String, Char> =
    Traversal.string()

  /**
   * [Traversal] to focus into the first and second value of a [Pair]
   */
  @JvmStatic
  public fun <A> pair(): Traversal<Pair<A, A>, A> =
    Traversal.pair()

  /**
   * [Traversal] to focus into the first, second and third value of a [Triple]
   */
  @JvmStatic
  public fun <A> triple(): Traversal<Triple<A, A, A>, A> =
    Traversal.triple()

  /**
   * [Traversal] to focus into the first, second, third and fourth value of a [arrow.core.Tuple4]
   */
  @JvmStatic
  public fun <A> tuple4(): Traversal<Tuple4<A, A, A, A>, A> =
    Traversal.tuple4()

  /**
   * [PTraversal] to focus into the first, second, third, fourth and fifth value of a [arrow.core.Tuple5]
   */
  @JvmStatic
  public fun <A> tuple5(): Traversal<Tuple5<A, A, A, A, A>, A> =
    Traversal.tuple5()

  /**
   * [Traversal] to focus into the first, second, third, fourth, fifth and sixth value of a [arrow.core.Tuple6]
   */
  @JvmStatic
  public fun <A> tuple6(): Traversal<Tuple6<A, A, A, A, A, A>, A> =
    Traversal.tuple6()

  /**
   * [Traversal] to focus into the first, second, third, fourth, fifth, sixth and seventh value of a [arrow.core.Tuple7]
   */
  @JvmStatic
  public fun <A> tuple7(): Traversal<Tuple7<A, A, A, A, A, A, A>, A> =
    Traversal.tuple7()

  /**
   * [Traversal] to focus into the first, second, third, fourth, fifth, sixth, seventh and eight value of a [arrow.core.Tuple8]
   */
  @JvmStatic
  public fun <A> tuple8(): Traversal<Tuple8<A, A, A, A, A, A, A, A>, A> =
    Traversal.tuple8()

  /**
   * [Traversal] to focus into the first, second, third, fourth, fifth, sixth, seventh, eight and ninth value of a [arrow.core.Tuple9]
   */
  @JvmStatic
  public fun <A> tuple9(): Traversal<Tuple9<A, A, A, A, A, A, A, A, A>, A> =
    Traversal.tuple9()
}
