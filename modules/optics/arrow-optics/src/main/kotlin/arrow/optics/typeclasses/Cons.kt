package arrow.optics.typeclasses

import arrow.core.*
import arrow.optics.*

/**
 * [Cons] provides a [Prism] between [S] and its head [A] and tail [S].
 * It provides a convenient way to attach or detach elements to the left side of a structure [S].
 *
 * @param S source of [Prism] and tail of [Prism] focus.
 * @param A head of [Prism] focus, [A] is supposed to be unique for a given [S].
 */
interface Cons<S, A> {

  /**
   * Provides a [Prism] between [S] and its head [A] and tail [S].
   */
  fun cons(): Prism<S, Tuple2<A, S>>

  /**
   * Provides an [Optional] between [S] and its head [A].
   */
  fun headOption(): Optional<S, A> =
    cons() compose Tuple2.first()

  /**
   * Provides an [Optional] between [S] and its tail [S].
   */
  fun tailOption(): Optional<S, S> =
    cons() compose Tuple2.second()

  /**
   * Prepend an element [A] to the head of [S].
   *
   * @receiver [A] element to prepend or attach on left side of [tail].
   */
  infix fun A.cons(tail: S): S =
    cons().reverseGet(Tuple2(this, tail))

  /**
   * Deconstruct an [S] to its optional head  [A] and tail [S].
   *
   * @receiver [S] structure to uncons into its head [A] and tail [S].
   */
  fun S.uncons(): Option<Tuple2<A, S>> =
    cons().getOption(this)

  companion object {

    /**
     * Lift an instance of [Cons] using an [Iso].
     */
    fun <S, A, B> fromIso(C: Cons<A, B>, iso: Iso<S, A>): Cons<S, B> = object : Cons<S, B> {
      override fun cons(): Prism<S, Tuple2<B, S>> = iso compose C.cons() compose iso.reverse().second()
    }
  }

}
