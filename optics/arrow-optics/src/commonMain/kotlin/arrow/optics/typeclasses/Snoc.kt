package arrow.optics.typeclasses

import arrow.core.Either
import arrow.core.Nullable
import arrow.core.left
import arrow.core.right
import arrow.optics.Iso
import arrow.optics.Optional
import arrow.optics.PLens
import arrow.optics.Prism
import kotlin.jvm.JvmStatic

public typealias Conj<S, A> = Snoc<S, A>

/**
 * [Snoc] defines a [Prism] between a [S] and its [init] [S] and last element [A] and thus can be seen as the reverse of [Cons].
 * It provides a way to attach or detach elements on the end side of a structure.
 *
 * @param [S] source of [Prism] and init of [Prism] target.
 * @param [A] last of [Prism] focus, [A] is supposed to be unique for a given [S].
 */
public fun interface Snoc<S, A> {

  /**
   * Provides a [Prism] between a [S] and its [init] [S] and last element [A].
   */
  public fun snoc(): Prism<S, Pair<S, A>>

  /**
   * Provides an [Optional] between [S] and its init [S].
   */
  public fun initOption(): Optional<S, S> = snoc() compose PLens.pairFirst()

  /**
   * Provides an [Optional] between [S] and its last element [A].
   */
  public fun lastOption(): Optional<S, A> = snoc() compose PLens.pairSecond()

  /**
   * Selects all elements except the last.
   */
  public val S.init: S?
    get() = initOption().getOrNull(this)

  /**
   * Append an element [A] to [S].
   */
  public infix fun S.snoc(last: A): S =
    snoc().reverseGet(Pair(this, last))

  /**
   * Deconstruct an [S] between its [init] and last element.
   */
  public fun S.unsnoc(): Pair<S, A>? =
    snoc().getOrNull(this)

  public companion object {

    /**
     * Lift an instance of [Snoc] using an [Iso].
     */
    public fun <S, A, B> fromIso(SS: Snoc<A, B>, iso: Iso<S, A>): Snoc<S, B> =
      Snoc { iso compose SS.snoc() compose iso.reverse().first() }

    /**
     * Construct a [Snoc] instance from a [Prism].
     */
    public operator fun <S, A> invoke(prism: Prism<S, Pair<S, A>>): Snoc<S, A> =
      Snoc { prism }

    /**
     * [Snoc] instance definition for [List].
     */
    @JvmStatic
    public fun <A> list(): Snoc<List<A>, A> =
      Snoc {
        object : Prism<List<A>, Pair<List<A>, A>> {
          override fun getOrModify(s: List<A>): Either<List<A>, Pair<List<A>, A>> =
            Nullable.zip(s.dropLast(1), s.lastOrNull(), ::Pair)?.right() ?: s.left()

          override fun reverseGet(b: Pair<List<A>, A>): List<A> =
            b.first + b.second
        }
      }

    /**
     * [Snoc] instance for [String].
     */
    @JvmStatic
    public fun string(): Snoc<String, Char> =
      Snoc {
        Prism(
          getOrModify = { if (it.isNotEmpty()) Pair(it.dropLast(1), it.last()).right() else it.left() },
          reverseGet = { (i, l) -> i + l }
        )
      }
  }
}
