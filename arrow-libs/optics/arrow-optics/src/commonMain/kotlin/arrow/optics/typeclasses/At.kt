package arrow.optics.typeclasses

import arrow.core.None
import arrow.core.Option
import arrow.optics.Fold
import arrow.optics.Getter
import arrow.optics.Iso
import arrow.optics.Lens
import arrow.optics.Optional
import arrow.optics.PLens
import arrow.optics.Prism
import arrow.optics.Setter
import arrow.optics.Traversal
import kotlin.jvm.JvmStatic

/**
 * [At] provides a [Lens] for a structure [S] to focus in [A] at a given index [I].
 *
 * @param S source of [Lens]
 * @param I index that uniquely identifies the focus of the [Lens]
 * @param A focus that is supposed to be unique for a given pair [S] and [I].
 */
public fun interface At<S, I, A> {

  /**
   * Get a [Lens] for a structure [S] with focus in [A] at index [i].
   *
   * @param i index [I] to zoom into [S] and find focus [A]
   * @return [Lens] with a focus in [A] at given index [I].
   */
  public fun at(i: I): Lens<S, A>

  /**
   * DSL to compose [At] with a [Lens] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Lens] with a focus in [S]
   * @param i index [I] to zoom into [S] and find focus [A]
   * @return [Lens] with a focus in [A] at given index [I].
   */
  public fun <T> Lens<T, S>.at(i: I): Lens<T, A> =
    this@at.compose(this@At.at(i))

  /**
   *  DSL to compose [At] with an [Iso] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Iso] with a focus in [S]
   * @param i index [I] to zoom into [S] and find focus [A]
   * @return [Lens] with a focus in [A] at given index [I].
   */
  public fun <T> Iso<T, S>.at(i: I): Lens<T, A> =
    this.compose(this@At.at(i))

  /**
   *  DSL to compose [At] with a [Prism] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Prism] with a focus in [S]
   * @param i index [I] to zoom into [S] and find focus [A]
   * @return [Optional] with a focus in [A] at given index [I].
   */
  public fun <T> Prism<T, S>.at(i: I): Optional<T, A> = this.compose(this@At.at(i))

  /**
   *  DSL to compose [At] with an [Optional] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Optional] with a focus in [S]
   * @param i index [I] to zoom into [S] and find focus [A]
   * @return [Optional] with a focus in [A] at given index [I].
   */
  public fun <T> Optional<T, S>.at(i: I): Optional<T, A> = this.compose(this@At.at(i))

  /**
   * DSL to compose [At] with a [Getter] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Getter] with a focus in [S]
   * @param i index [I] to zoom into [S] and find focus [A]
   * @return [Getter] with a focus in [A] at given index [I].
   */
  public fun <T> Getter<T, S>.at(i: I): Getter<T, A> = this.compose(this@At.at(i))

  /**
   * DSL to compose [At] with a [Setter] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Setter] with a focus in [S]
   * @param i index [I] to zoom into [S] and find focus [A]
   * @return [Setter] with a focus in [A] at given index [I].
   */
  public fun <T> Setter<T, S>.at(i: I): Setter<T, A> = this.compose(this@At.at(i))

  /**
   * DSL to compose [At] with a [Traversal] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Traversal] with a focus in [S]
   * @param i index [I] to zoom into [S] and find focus [A]
   * @return [Traversal] with a focus in [A] at given index [I].
   */
  public fun <T> Traversal<T, S>.at(i: I): Traversal<T, A> = this.compose(this@At.at(i))

  /**
   * DSL to compose [At] with a [Fold] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Fold] with a focus in [S]
   * @param i index [I] to zoom into [S] and find focus [A]
   * @return [Fold] with a focus in [A] at given index [I].
   */
  public fun <T> Fold<T, S>.at(i: I): Fold<T, A> = this.compose(this@At.at(i))

  public companion object {

    /**
     * Lift an instance of [At] using an [Iso].
     *
     * @param AT [At] that can provide [Lens] for a structure [U] with a focus in [A] with given index [I].
     * @param iso [Iso] that defines an isomorphism between [S] and [U]
     * @return [At] to provide [Lens] for structure [S] with focus in [A] at given index [I]
     */
    public fun <S, U, I, A> fromIso(AT: At<U, I, A>, iso: Iso<S, U>): At<S, I, A> =
      At { i -> iso compose AT.at(i) }

    @JvmStatic
    public fun <K, V> map(): At<Map<K, V>, K, Option<V>> =
      At { i ->
        PLens(
          get = { Option.fromNullable(it[i]) },
          set = { map, optV ->
            optV.fold({
              (map - i)
            }, {
              (map + (i to it))
            })
          }
        )
      }

    /**
     * [At] instance definition for [Set].
     */
    @JvmStatic
    public fun <A> set(): At<Set<A>, A, Boolean> =
      At { i ->
        PLens(
          get = { it.contains(i) },
          set = { s, b -> (if (b) s + i else s - i) }
        )
      }
  }
}

/**
 * Delete a value associated with a key in a Map-like container
 *
 * @receiver [At] to provide a [Lens] where an [Option] focus can be found at index [I] for a structure [S].
 * @param s [S] structure to zoom into and find focus [A].
 * @param i index [I] to zoom into [S] and find focus [A]
 * @return [S] where focus [A] was removed at index [I]
 */
public fun <S, I, A> At<S, I, Option<A>>.remove(s: S, i: I): S = at(i).set(s, None)

/**
 * Lift deletion of a value associated with a key in a Map-like container
 *
 * @receiver [At] to provide a [Lens] where an [Option] focus can be found at index [I] for a structure [S].
 * @param i index [I] to zoom into [S] and find focus [A]
 * @return function that takes [S] and returns a new [S] where focus [A] was removed at index [I]
 */
public fun <S, I, A> At<S, I, Option<A>>.remove(i: I): (S) -> S = at(i).lift { None }
