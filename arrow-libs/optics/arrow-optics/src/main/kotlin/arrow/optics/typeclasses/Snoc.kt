package arrow.optics.typeclasses

import arrow.optics.Iso
import arrow.optics.Optional
import arrow.optics.PLens
import arrow.optics.Prism
import arrow.optics.pairFirst
import arrow.optics.pairSecond

typealias Conj<S, A> = Snoc<S, A>

/**
 * [Snoc] defines a [Prism] between a [S] and its [init] [S] and last element [A] and thus can be seen as the reverse of [Cons].
 * It provides a way to attach or detach elements on the end side of a structure.
 *
 * @param [S] source of [Prism] and init of [Prism] target.
 * @param [A] last of [Prism] focus, [A] is supposed to be unique for a given [S].
 */
fun interface Snoc<S, A> {

  /**
   * Provides a [Prism] between a [S] and its [init] [S] and last element [A].
   */
  fun snoc(): Prism<S, Pair<S, A>>

  /**
   * Provides an [Optional] between [S] and its init [S].
   */
  fun initOption(): Optional<S, S> = snoc() compose PLens.pairFirst()

  /**
   * Provides an [Optional] between [S] and its last element [A].
   */
  fun lastOption(): Optional<S, A> = snoc() compose PLens.pairSecond()

  /**
   * Selects all elements except the last.
   */
  val S.init: S?
    get() = initOption().getOrNull(this)

  /**
   * Append an element [A] to [S].
   */
  infix fun S.snoc(last: A): S =
    snoc().reverseGet(Pair(this, last))

  /**
   * Deconstruct an [S] between its [init] and last element.
   */
  fun S.unsnoc(): Pair<S, A>? =
    snoc().getOrNull(this)

  companion object {

    /**
     * Lift an instance of [Snoc] using an [Iso].
     */
    fun <S, A, B> fromIso(SS: Snoc<A, B>, iso: Iso<S, A>): Snoc<S, B> =
      Snoc { iso compose SS.snoc() compose iso.reverse().first() }

    /**
     * Construct a [Snoc] instance from a [Prism].
     */
    operator fun <S, A> invoke(prism: Prism<S, Pair<S, A>>): Snoc<S, A> =
      Snoc { prism }
  }
}
