package arrow.optics.typeclasses

import arrow.core.NonEmptyList
import arrow.core.left
import arrow.core.right
import arrow.core.toNonEmptyListOrNull
import arrow.optics.Optional
import arrow.optics.PLens
import arrow.optics.POptional
import arrow.optics.Traversal
import kotlin.jvm.JvmStatic

/**
 * [Index] provides an [Optional] for a structure [S] to focus in an optional [A] at a given index [I].
 *
 * @param S source of [Optional]
 * @param I index
 * @param A focus of [Optional], [A] is supposed to be unique for a given pair [S] and [I].
 */
public fun interface Index<S, I, A> {

  /**
   * Get [Optional] focus [A] for a structure [S] at index [i].
   *
   * @param i index [I] to focus into [S] and find focus [A]
   * @return [Optional] with a focus in [A] at given index [I].
   */
  public fun index(i: I): Optional<S, A>

  /**
   *  DSL to compose [Index] with an [Optional] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Optional] with a focus in [S]
   * @param i index [I] to focus into [S] and find focus [A]
   * @return [Optional] with a focus in [A] at given index [I].
   */
  public fun <T> Optional<T, S>.index(i: I): Optional<T, A> = this.compose(this@Index.index(i))

  /**
   * DSL to compose [Index] with a [Traversal] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Traversal] with a focus in [S]
   * @param i index [I] to focus into [S] and find focus [A]
   * @return [Traversal] with a focus in [A] at given index [I].
   */
  public fun <T> Traversal<T, S>.index(i: I): Traversal<T, A> = this.compose(this@Index.index(i))

  /**
   *  DSL to compose [Index] with an [Optional] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Optional] with a focus in [S]
   * @param i index [I] to focus into [S] and find focus [A]
   * @return [Optional] with a focus in [A] at given index [I].
   */
  public operator fun <T> Optional<T, S>.get(i: I): Optional<T, A> = this.compose(this@Index.index(i))

  /**
   * DSL to compose [Index] with a [Traversal] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Traversal] with a focus in [S]
   * @param i index [I] to focus into [S] and find focus [A]
   * @return [Traversal] with a focus in [A] at given index [I].
   */
  public operator fun <T> Traversal<T, S>.get(i: I): Traversal<T, A> = this.compose(this@Index.index(i))

  public companion object {

    /**
     * [Index] instance definition for [List].
     */
    @JvmStatic
    public fun <A> list(): Index<List<A>, Int, A> =
      Index { i ->
        POptional(
          getOrModify = { it.getOrNull(i)?.right() ?: it.left() },
          set = { l, a -> l.mapIndexed { index: Int, aa: A -> if (index == i) a else aa } }
        )
      }

    @JvmStatic
    public fun <K, V> map(): Index<Map<K, V>, K, V> =
      Index { i ->
        POptional(
          getOrModify = { it[i]?.right() ?: it.left() },
          set = { m, v -> m.mapValues { (k, vv) -> if (k == i) v else vv } }
        )
      }

    /**
     * [Index] instance definition for [NonEmptyList].
     */
    @JvmStatic
    public fun <A> nonEmptyList(): Index<NonEmptyList<A>, Int, A> =
      Index { i ->
        POptional(
          getOrModify = { l -> l.all.getOrNull(i)?.right() ?: l.left() },
          set = { l, a ->
              l.all.mapIndexed { index: Int, aa: A -> if (index == i) a else aa }
                      .toNonEmptyListOrNull()
                      ?: throw IndexOutOfBoundsException("Empty list doesn't contain element at index 0.")
          }
        )
      }

    @JvmStatic
    public fun <A> sequence(): Index<Sequence<A>, Int, A> =
      Index { i ->
        POptional(
          getOrModify = { it.elementAtOrNull(i)?.right() ?: it.left() },
          set = { s, a -> s.mapIndexed { index, aa -> if (index == i) a else aa } }
        )
      }

    /**
     * [Index] instance for [String].
     * It allows access to every [Char] in a [String] by its index's position.
     *
     * @receiver [Index.Companion] to make the instance statically available.
     * @return [Index] instance
     */
    @JvmStatic
    public fun string(): Index<String, Int, Char> =
      Index { i ->
        PLens.stringToList() compose list<Char>().index(i)
      }
  }
}
