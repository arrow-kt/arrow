package arrow.optics.typeclasses

import arrow.core.left
import arrow.core.right
import arrow.optics.Iso
import arrow.optics.Optional
import arrow.optics.PLens
import arrow.optics.PPrism
import arrow.optics.Prism
import kotlin.jvm.JvmStatic

/**
 * [Cons] provides a [Prism] between [S] and its first element [A] and tail [S].
 * It provides a convenient way to attach or detach elements to the left side of a structure [S].
 *
 * @param S source of [Prism] and tail of [Prism] focus.
 * @param A first element of [Prism] focus, [A] is supposed to be unique for a given [S].
 */
public fun interface Cons<S, A> {

  /**
   * Provides a [Prism] between [S] and its first element [A] and tail [S].
   */
  public fun cons(): Prism<S, Pair<A, S>>

  /**
   * Provides an [Optional] between [S] and its first element [A].
   */
  public fun firstOption(): Optional<S, A> =
    cons() compose PLens.pairFirst()

  /**
   * Provides an [Optional] between [S] and its tail [S].
   */
  public fun tailOption(): Optional<S, S> =
    cons() compose PLens.pairSecond()

  /**
   * Prepend an element [A] to the first element of [S].
   *
   * @receiver [A] element to prepend or attach on left side of [tail].
   */
  public infix fun A.cons(tail: S): S =
    cons().reverseGet(Pair(this, tail))

  /**
   * Deconstruct an [S] to its optional first element [A] and tail [S].
   *
   * @receiver [S] structure to uncons into its first element [A] and tail [S].
   */
  public fun S.uncons(): Pair<A, S>? =
    cons().getOrNull(this)

  public companion object {

    /**
     * Lift an instance of [Cons] using an [Iso].
     */
    public fun <S, A, B> fromIso(C: Cons<A, B>, iso: Iso<S, A>): Cons<S, B> =
      Cons { iso compose C.cons() compose iso.reverse().second() }

    public operator fun <S, A> invoke(prism: Prism<S, Pair<A, S>>): Cons<S, A> =
      Cons { prism }

    /**
     * [Cons] instance definition for [List].
     */
    @JvmStatic
    public fun <A> list(): Cons<List<A>, A> =
      Cons {
        PPrism(
          getOrModify = { list -> list.firstOrNull()?.let { Pair(it, list.drop(1)) }?.right() ?: list.left() },
          reverseGet = { (a, aas) -> listOf(a) + aas }
        )
      }

    /**
     * [Cons] instance for [String].
     */
    @JvmStatic
    public fun string(): Cons<String, Char> =
      Cons {
        Prism(
          getOrModify = { if (it.isNotEmpty()) Pair(it.first(), it.drop(1)).right() else it.left() },
          reverseGet = { (h, t) -> h + t }
        )
      }
  }
}
