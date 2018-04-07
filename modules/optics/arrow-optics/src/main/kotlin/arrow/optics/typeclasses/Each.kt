package arrow.optics.typeclasses

import arrow.Kind
import arrow.optics.Iso
import arrow.optics.Traversal
import arrow.typeclasses.Traverse

/**
 * [Each] provides a [Traversal] that can focus into a structure [S] to see all its foci [A].
 *
 * @param S source of the [Traversal].
 * @param A focus of [Traversal].
 */
interface Each<S, A> {

  /**
   * Get a [Traversal] for a structure [S] with focus in [A].
   */
  fun each(): Traversal<S, A>

  companion object {

    /**
     * Lift an instance of [Each] using an [Iso].
     */
    fun <S, A, B> fromIso(iso: Iso<S, A>, EA: Each<A, B>): Each<S, B> = object : Each<S, B> {
      override fun each(): Traversal<S, B> = iso compose EA.each()
    }

    /**
     * Create an instance of [Each] from a [Traverse].
     */
    fun <S, A> fromTraverse(T: Traverse<S>): Each<Kind<S, A>, A> = object : Each<Kind<S, A>, A> {
      override fun each(): Traversal<Kind<S, A>, A> = Traversal.fromTraversable(T)
    }
  }

}
