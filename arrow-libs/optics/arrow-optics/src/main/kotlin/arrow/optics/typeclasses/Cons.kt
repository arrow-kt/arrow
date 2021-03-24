package arrow.optics.typeclasses

import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.left
import arrow.core.right
import arrow.optics.Iso
import arrow.optics.Lens
import arrow.optics.Optional
import arrow.optics.PPrism
import arrow.optics.Prism

/**
 * [Cons] provides a [Prism] between [S] and its first element [A] and tail [S].
 * It provides a convenient way to attach or detach elements to the left side of a structure [S].
 *
 * @param S source of [Prism] and tail of [Prism] focus.
 * @param A first element of [Prism] focus, [A] is supposed to be unique for a given [S].
 */
fun interface Cons<S, A> {

  /**
   * Provides a [Prism] between [S] and its first element [A] and tail [S].
   */
  fun cons(): Prism<S, Tuple2<A, S>>

  /**
   * Provides an [Optional] between [S] and its first element [A].
   */
  fun firstOption(): Optional<S, A> =
    cons() compose Lens.tuple2First()

  /**
   * Provides an [Optional] between [S] and its tail [S].
   */
  fun tailOption(): Optional<S, S> =
    cons() compose Lens.tuple2Second()

  /**
   * Prepend an element [A] to the first element of [S].
   *
   * @receiver [A] element to prepend or attach on left side of [tail].
   */
  infix fun A.cons(tail: S): S =
    cons().reverseGet(Tuple2(this, tail))

  /**
   * Deconstruct an [S] to its optional first element [A] and tail [S].
   *
   * @receiver [S] structure to uncons into its first element [A] and tail [S].
   */
  fun S.uncons(): Option<Tuple2<A, S>> =
    cons().getOption(this)

  companion object {

    /**
     * Lift an instance of [Cons] using an [Iso].
     */
    fun <S, A, B> fromIso(C: Cons<A, B>, iso: Iso<S, A>): Cons<S, B> =
      Cons { iso compose C.cons() compose iso.reverse().second() }

    operator fun <S, A> invoke(prism: Prism<S, Tuple2<A, S>>): Cons<S, A> =
      Cons { prism }

    /**
     * [Cons] instance definition for [List].
     */
    @JvmStatic
    fun <A> list(): Cons<List<A>, A> =
      Cons {
        PPrism(
          getOrModify = { list -> list.firstOrNull()?.let { Tuple2(it, list.drop(1)) }?.right() ?: list.left() },
          reverseGet = { (a, aas) -> listOf(a) + aas }
        )
      }

    /**
     * [Cons] instance for [String].
     */
    @JvmStatic
    fun string(): Cons<String, Char> =
      Cons {
        Prism(
          getOrModify = { if (it.isNotEmpty()) Tuple2(it.first(), it.drop(1)).right() else it.left() },
          reverseGet = { (h, t) -> h + t }
        )
      }
  }
}
