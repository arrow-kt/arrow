package arrow.optics.typeclasses

import arrow.core.NonEmptyList
import arrow.core.left
import arrow.core.right
import arrow.optics.Fold
import arrow.optics.Iso
import arrow.optics.Lens
import arrow.optics.Optional
import arrow.optics.POptional
import arrow.optics.Prism
import arrow.optics.Setter
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
   * DSL to compose [Index] with a [Lens] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Lens] with a focus in [S]
   * @param i index [I] to focus into [S] and find focus [A]
   * @return [Optional] with a focus in [A] at given index [I].
   */
  public fun <T> Lens<T, S>.index(i: I): Optional<T, A> = this.compose(this@Index.index(i))

  /**
   *  DSL to compose [Index] with an [Iso] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Iso] with a focus in [S]
   * @param i index [I] to focus into [S] and find focus [A]
   * @return [Optional] with a focus in [A] at given index [I].
   */
  public fun <T> Iso<T, S>.index(i: I): Optional<T, A> = this.compose(this@Index.index(i))

  /**
   *  DSL to compose [Index] with a [Prism] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Prism] with a focus in [S]
   * @param i index [I] to focus into [S] and find focus [A]
   * @return [Optional] with a focus in [A] at given index [I].
   */
  public fun <T> Prism<T, S>.index(i: I): Optional<T, A> = this.compose(this@Index.index(i))

  /**
   *  DSL to compose [Index] with an [Optional] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Optional] with a focus in [S]
   * @param i index [I] to focus into [S] and find focus [A]
   * @return [Optional] with a focus in [A] at given index [I].
   */
  public fun <T> Optional<T, S>.index(i: I): Optional<T, A> = this.compose(this@Index.index(i))

  /**
   * DSL to compose [Index] with a [Setter] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Setter] with a focus in [S]
   * @param i index [I] to focus into [S] and find focus [A]
   * @return [Setter] with a focus in [A] at given index [I].
   */
  public fun <T> Setter<T, S>.index(i: I): Setter<T, A> = this.compose(this@Index.index(i))

  /**
   * DSL to compose [Index] with a [Traversal] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Traversal] with a focus in [S]
   * @param i index [I] to focus into [S] and find focus [A]
   * @return [Traversal] with a focus in [A] at given index [I].
   */
  public fun <T> Traversal<T, S>.index(i: I): Traversal<T, A> = this.compose(this@Index.index(i))

  /**
   * DSL to compose [Index] with a [Fold] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Fold] with a focus in [S]
   * @param i index [I] to focus into [S] and find focus [A]
   * @return [Fold] with a focus in [A] at given index [I].
   */
  public fun <T> Fold<T, S>.index(i: I): Fold<T, A> = this.compose(this@Index.index(i))

  /**
   * DSL to compose [Index] with a [Lens] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Lens] with a focus in [S]
   * @param i index [I] to focus into [S] and find focus [A]
   * @return [Optional] with a focus in [A] at given index [I].
   */
  public operator fun <T> Lens<T, S>.get(i: I): Optional<T, A> = this.compose(this@Index.index(i))

  /**
   *  DSL to compose [Index] with an [Iso] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Iso] with a focus in [S]
   * @param i index [I] to focus into [S] and find focus [A]
   * @return [Optional] with a focus in [A] at given index [I].
   */
  public operator fun <T> Iso<T, S>.get(i: I): Optional<T, A> = this.compose(this@Index.index(i))

  /**
   *  DSL to compose [Index] with a [Prism] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Prism] with a focus in [S]
   * @param i index [I] to focus into [S] and find focus [A]
   * @return [Optional] with a focus in [A] at given index [I].
   */
  public operator fun <T> Prism<T, S>.get(i: I): Optional<T, A> = this.compose(this@Index.index(i))

  /**
   *  DSL to compose [Index] with an [Optional] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Optional] with a focus in [S]
   * @param i index [I] to focus into [S] and find focus [A]
   * @return [Optional] with a focus in [A] at given index [I].
   */
  public operator fun <T> Optional<T, S>.get(i: I): Optional<T, A> = this.compose(this@Index.index(i))

  /**
   * DSL to compose [Index] with a [Setter] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Setter] with a focus in [S]
   * @param i index [I] to focus into [S] and find focus [A]
   * @return [Setter] with a focus in [A] at given index [I].
   */
  public operator fun <T> Setter<T, S>.get(i: I): Setter<T, A> = this.compose(this@Index.index(i))

  /**
   * DSL to compose [Index] with a [Traversal] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Traversal] with a focus in [S]
   * @param i index [I] to focus into [S] and find focus [A]
   * @return [Traversal] with a focus in [A] at given index [I].
   */
  public operator fun <T> Traversal<T, S>.get(i: I): Traversal<T, A> = this.compose(this@Index.index(i))

  /**
   * DSL to compose [Index] with a [Fold] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Fold] with a focus in [S]
   * @param i index [I] to focus into [S] and find focus [A]
   * @return [Fold] with a focus in [A] at given index [I].
   */
  public operator fun <T> Fold<T, S>.get(i: I): Fold<T, A> = this.compose(this@Index.index(i))

  public companion object {
    /**
     * Lift an instance of [Index] using an [Iso].
     *
     * @param ID [Index] instance to provide a [Optional] to focus into [S] at [I]
     * @param iso [Iso] that defines an isomorphism between a type [S] and [A]
     * @return [Index] for a structure [S] to focus in an optional [A] at a given index [I]
     */
    public fun <S, A, I, B> fromIso(ID: Index<A, I, B>, iso: Iso<S, A>): Index<S, I, B> =
      Index { i -> iso compose ID.index(i) }

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
            NonEmptyList.fromListUnsafe(
              l.all.mapIndexed { index: Int, aa: A -> if (index == i) a else aa }
            )
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
        Iso.stringToList() compose Index.list<Char>().index(i)
      }
  }
}
