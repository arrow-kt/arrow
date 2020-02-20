package arrow.optics.typeclasses

import arrow.optics.Fold
import arrow.optics.Iso
import arrow.optics.Lens
import arrow.optics.Optional
import arrow.optics.Prism
import arrow.optics.Setter
import arrow.optics.Traversal

/**
 * ank_macro_hierarchy(arrow.optics.typeclasses.Index)
 *
 * [Index] provides an [Optional] for a structure [S] to focus in an optional [A] at a given index [I].
 *
 * @param S source of [Optional]
 * @param I index
 * @param A focus of [Optional], [A] is supposed to be unique for a given pair [S] and [I].
 */
interface Index<S, I, A> {

  /**
   * Get [Optional] focus [A] for a structure [S] at index [i].
   *
   * @param i index [I] to focus into [S] and find focus [A]
   * @return [Optional] with a focus in [A] at given index [I].
   */
  fun index(i: I): Optional<S, A>

  /**
   * DSL to compose [Index] with a [Lens] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Lens] with a focus in [S]
   * @param i index [I] to focus into [S] and find focus [A]
   * @return [Optional] with a focus in [A] at given index [I].
   */
  fun <T> Lens<T, S>.index(i: I): Optional<T, A> = this.compose(this@Index.index(i))

  /**
   *  DSL to compose [Index] with an [Iso] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Iso] with a focus in [S]
   * @param i index [I] to focus into [S] and find focus [A]
   * @return [Optional] with a focus in [A] at given index [I].
   */
  fun <T> Iso<T, S>.index(i: I): Optional<T, A> = this.compose(this@Index.index(i))

  /**
   *  DSL to compose [Index] with a [Prism] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Prism] with a focus in [S]
   * @param i index [I] to focus into [S] and find focus [A]
   * @return [Optional] with a focus in [A] at given index [I].
   */
  fun <T> Prism<T, S>.index(i: I): Optional<T, A> = this.compose(this@Index.index(i))

  /**
   *  DSL to compose [Index] with an [Optional] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Optional] with a focus in [S]
   * @param i index [I] to focus into [S] and find focus [A]
   * @return [Optional] with a focus in [A] at given index [I].
   */
  fun <T> Optional<T, S>.index(i: I): Optional<T, A> = this.compose(this@Index.index(i))

  /**
   * DSL to compose [Index] with a [Setter] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Setter] with a focus in [S]
   * @param i index [I] to focus into [S] and find focus [A]
   * @return [Setter] with a focus in [A] at given index [I].
   */
  fun <T> Setter<T, S>.index(i: I): Setter<T, A> = this.compose(this@Index.index(i))

  /**
   * DSL to compose [Index] with a [Traversal] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Traversal] with a focus in [S]
   * @param i index [I] to focus into [S] and find focus [A]
   * @return [Traversal] with a focus in [A] at given index [I].
   */
  fun <T> Traversal<T, S>.index(i: I): Traversal<T, A> = this.compose(this@Index.index(i))

  /**
   * DSL to compose [Index] with a [Fold] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Fold] with a focus in [S]
   * @param i index [I] to focus into [S] and find focus [A]
   * @return [Fold] with a focus in [A] at given index [I].
   */
  fun <T> Fold<T, S>.index(i: I): Fold<T, A> = this.compose(this@Index.index(i))

  /**
   * DSL to compose [Index] with a [Lens] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Lens] with a focus in [S]
   * @param i index [I] to focus into [S] and find focus [A]
   * @return [Optional] with a focus in [A] at given index [I].
   */
  operator fun <T> Lens<T, S>.get(i: I): Optional<T, A> = this.compose(this@Index.index(i))

  /**
   *  DSL to compose [Index] with an [Iso] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Iso] with a focus in [S]
   * @param i index [I] to focus into [S] and find focus [A]
   * @return [Optional] with a focus in [A] at given index [I].
   */
  operator fun <T> Iso<T, S>.get(i: I): Optional<T, A> = this.compose(this@Index.index(i))

  /**
   *  DSL to compose [Index] with a [Prism] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Prism] with a focus in [S]
   * @param i index [I] to focus into [S] and find focus [A]
   * @return [Optional] with a focus in [A] at given index [I].
   */
  operator fun <T> Prism<T, S>.get(i: I): Optional<T, A> = this.compose(this@Index.index(i))

  /**
   *  DSL to compose [Index] with an [Optional] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Optional] with a focus in [S]
   * @param i index [I] to focus into [S] and find focus [A]
   * @return [Optional] with a focus in [A] at given index [I].
   */
  operator fun <T> Optional<T, S>.get(i: I): Optional<T, A> = this.compose(this@Index.index(i))

  /**
   * DSL to compose [Index] with a [Setter] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Setter] with a focus in [S]
   * @param i index [I] to focus into [S] and find focus [A]
   * @return [Setter] with a focus in [A] at given index [I].
   */
  operator fun <T> Setter<T, S>.get(i: I): Setter<T, A> = this.compose(this@Index.index(i))

  /**
   * DSL to compose [Index] with a [Traversal] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Traversal] with a focus in [S]
   * @param i index [I] to focus into [S] and find focus [A]
   * @return [Traversal] with a focus in [A] at given index [I].
   */
  operator fun <T> Traversal<T, S>.get(i: I): Traversal<T, A> = this.compose(this@Index.index(i))

  /**
   * DSL to compose [Index] with a [Fold] for a structure [S] to focus in on [A] at given index [I].
   *
   * @receiver [Fold] with a focus in [S]
   * @param i index [I] to focus into [S] and find focus [A]
   * @return [Fold] with a focus in [A] at given index [I].
   */
  operator fun <T> Fold<T, S>.get(i: I): Fold<T, A> = this.compose(this@Index.index(i))

  companion object {

    /**
     * Lift an instance of [Index] using an [Iso].
     *
     * @param ID [Index] instance to provide a [Optional] to focus into [S] at [I]
     * @param iso [Iso] that defines an isomorphism between a type [S] and [A]
     * @return [Index] for a structure [S] to focus in an optional [A] at a given index [I]
     */
    fun <S, A, I, B> fromIso(ID: Index<A, I, B>, iso: Iso<S, A>): Index<S, I, B> = object : Index<S, I, B> {
      override fun index(i: I): Optional<S, B> = iso compose ID.index(i)
    }
  }
}
