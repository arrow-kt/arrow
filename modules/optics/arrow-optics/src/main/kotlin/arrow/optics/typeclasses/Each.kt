package arrow.optics.typeclasses

import arrow.Kind
import arrow.optics.Fold
import arrow.optics.Iso
import arrow.optics.Lens
import arrow.optics.Optional
import arrow.optics.Prism
import arrow.optics.Setter
import arrow.optics.Traversal
import arrow.typeclasses.Traverse

/**
 * ank_macro_hierarchy(arrow.optics.typeclasses.Each)
 *
 * [Each] provides a [Traversal] that can focus into a structure [S] to see all its foci [A]
 *
 * @param S source of the [Traversal]
 * @param A focus of [Traversal]
 */
interface Each<S, A> {

  /**
   * Provide a [Traversal] for a structure [S] with focus in [A]
   *
   * @return [Traversal] provided by [Each] instance
   */
  fun each(): Traversal<S, A>

  /**
   * DSL to compose [Each] with a [Lens] for a structure [S] to see all its foci [A]
   *
   * @receiver [Lens] with a focus in [S]
   * @return [Traversal] with a focus in [A]
   */
  val <T> Lens<T, S>.every: Traversal<T, A> get() = this.compose(each())

  /**
   * DSL to compose [Each] with a [Iso] for a structure [S] to see all its foci [A]
   *
   * @receiver [Iso] with a focus in [S]
   * @return [Traversal] with a focus in [A]
   */
  val <T> Iso<T, S>.every: Traversal<T, A> get() = this.compose(each())

  /**
   * DSL to compose [Each] with a [Prism] for a structure [S] to see all its foci [A]
   *
   * @receiver [Prism] with a focus in [S]
   * @return [Traversal] with a focus in [A]
   */
  val <T> Prism<T, S>.every: Traversal<T, A> get() = this.compose(each())

  /**
   * DSL to compose [Each] with a [Optional] for a structure [S] to see all its foci [A]
   *
   * @receiver [Optional] with a focus in [S]
   * @return [Traversal] with a focus in [A]
   */
  val <T> Optional<T, S>.every: Traversal<T, A> get() = this.compose(each())

  /**
   * DSL to compose [Each] with a [Setter] for a structure [S] to see all its foci [A]
   *
   * @receiver [Setter] with a focus in [S]
   * @return [Setter] with a focus in [A]
   */
  val <T> Setter<T, S>.every: Setter<T, A> get() = this.compose(each())

  /**
   * DSL to compose [Each] with a [Traversal] for a structure [S] to see all its foci [A]
   *
   * @receiver [Traversal] with a focus in [S]
   * @return [Traversal] with a focus in [A]
   */
  val <T> Traversal<T, S>.every: Traversal<T, A> get() = this.compose(each())

  /**
   * DSL to compose [Each] with a [Fold] for a structure [S] to see all its foci [A]
   *
   * @receiver [Fold] with a focus in [S]
   * @return [Fold] with a focus in [A]
   */
  val <T> Fold<T, S>.every: Fold<T, A> get() = this.compose(each())

  companion object {

    /**
     * Lift an instance of [Each] using an [Iso]
     *
     * @param EA [Each] that can provide [Traversal] for a structure [A] with a focus in [B]
     * @param iso [Iso] that defines an isomorphism between [S] and [A]
     * @return [Each] to provide [Traversal] for structure [S] with focus in [B]
     */
    fun <S, A, B> fromIso(EA: Each<A, B>, iso: Iso<S, A>): Each<S, B> = object : Each<S, B> {
      override fun each(): Traversal<S, B> = iso compose EA.each()
    }

    /**
     * Create an instance of [Each] from a [Traverse]
     *
     * @param T [Traverse] to create [Each] instance from
     * @return [Each] that provides [Traversal] created from [Traverse]
     */
    fun <S, A> fromTraverse(T: Traverse<S>): Each<Kind<S, A>, A> = object : Each<Kind<S, A>, A> {
      override fun each(): Traversal<Kind<S, A>, A> = Traversal.fromTraversable(T)
    }
  }

}
