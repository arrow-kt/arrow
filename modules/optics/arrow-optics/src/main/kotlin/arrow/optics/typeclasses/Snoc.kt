package arrow.optics.typeclasses

import arrow.core.*
import arrow.optics.*

typealias Conj<S, A> = Snoc<S, A>

/**
 * ank_macro_hierarchy(arrow.optics.typeclasses.Snoc)
 *
 * [Snoc] defines a [Prism] between a [S] and its [init] [S] and last element [A] and thus can be seen as the reverse of [Cons].
 * It provides a way to attach or detach elements on the end side of a structure.
 *
 * @param [S] source of [Prism] and init of [Prism] target.
 * @param [A] last of [Prism] focus, [A] is supposed to be unique for a given [S].
 */
interface Snoc<S, A> {

  /**
   * Provides a [Prism] between a [S] and its [init] [S] and last element [A].
   */
  fun snoc(): Prism<S, Tuple2<S, A>>

  /**
   * Provides an [Optional] between [S] and its init [S].
   */
  fun initOption(): Optional<S, S> = snoc() compose Tuple2.first()

  /**
   * Provides an [Optional] between [S] and its last element [A].
   */
  fun lastOption(): Optional<S, A> = snoc() compose Tuple2.second()

  /**
   * Selects all elements except the last.
   */
  val S.init: Option<S>
    get() = initOption().getOption(this)

  /**
   * Append an element [A] to [S].
   */
  infix fun S.snoc(last: A): S =
    snoc().reverseGet(Tuple2(this, last))

  /**
   * Deconstruct an [S] between its [init] and last element.
   */
  fun S.unsnoc(): Option<Tuple2<S, A>> =
    snoc().getOption(this)

  companion object {

    /**
     * Lift an instance of [Snoc] using an [Iso].
     */
    fun <S, A, B> fromIso(SS: Snoc<A, B>, iso: Iso<S, A>): Snoc<S, B> = object : Snoc<S, B> {
      override fun snoc(): Prism<S, Tuple2<S, B>> = iso compose SS.snoc() compose iso.reverse().first()
    }

    /**
     * Construct a [Snoc] instance from a [Prism].
     */
    operator fun <S, A> invoke(prism: Prism<S, Tuple2<S, A>>): Snoc<S, A> = object : Snoc<S, A> {
      override fun snoc(): Prism<S, Tuple2<S, A>> = prism
    }
  }

}
