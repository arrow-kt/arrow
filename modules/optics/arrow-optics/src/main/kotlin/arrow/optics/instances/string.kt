package arrow.optics.instances

import arrow.*
import arrow.data.*
import arrow.typeclasses.*
import arrow.optics.*
import arrow.optics.typeclasses.*

/**
 * [Traversal] for [String] that focuses in each [Char] of the source [String].
 *
 * @receiver [String.Companion] to make it statically available.
 * @return [Traversal] with source [String] and foci every [Char] in the source.
 */
fun String.Companion.traversal(): Traversal<String, Char> = object : Traversal<String, Char> {
  override fun <F> modifyF(FA: Applicative<F>, s: String, f: (Char) -> Kind<F, Char>): Kind<F, String> = FA.run {
    s.toList().k().traverse(FA, f).map { it.joinToString(separator = "") }
  }
}

/**
 * [String]'s [Each] instance
 * @see StringEachInstance
 * @receiver [String.Companion] to make the instance statically available.
 * @return [Each] instance
 */
fun String.Companion.each(): Each<String, Char> = StringEachInstance()

/**
 * [Each] instance for [String].
 */
interface StringEachInstance : Each<String, Char> {

  override fun each(): Traversal<String, Char> =
    String.traversal()

  companion object {
    /**
     * Operator overload to instantiate typeclass instance.
     *
     * @return [FilterIndex] instance for [String]
     */
    operator fun invoke(): Each<String, Char> = object : StringEachInstance {}
  }
}

/**
 * [String]'s [FilterIndex] instance
 *
 * @see StringFilterIndexInstance
 * @receiver [String.Companion] to make the instance statically available.
 * @return [FilterIndex] instance
 */
fun String.Companion.filterIndex(): FilterIndex<String, Int, Char> = StringFilterIndexInstance()

/**
 * [FilterIndex] instance for [String].
 * It allows filtering of every [Char] in a [String] by its index's position.
 */
interface StringFilterIndexInstance : FilterIndex<String, Int, Char> {
  override fun filter(p: (Int) -> Boolean): Traversal<String, Char> =
    String.toListK() compose ListK.filterIndex<Char>().filter(p)

  companion object {
    /**
     * Operator overload to instantiate typeclass instance.
     *
     * @return [FilterIndex] instance for [String]
     */
    operator fun invoke(): FilterIndex<String, Int, Char> = object : StringFilterIndexInstance {}
  }
}

/**
 * [String]'s [Index] instance
 * It allows access to every [Char] in a [String] by its index's position.
 *
 * @see StringIndexInstance
 * @receiver [String.Companion] to make the instance statically available.
 * @return [Index] instance
 */
fun String.Companion.index(): Index<String, Int, Char> = StringIndexInstance()

/**
 * [Index] instance for [String].
 * It allows access to every [Char] in a [String] by its index's position.
 */
interface StringIndexInstance : Index<String, Int, Char> {

  override fun index(i: Int): Optional<String, Char> =
    String.toListK() compose ListK.index<Char>().index(i)

  companion object {
    /**
     * Operator overload to instantiate typeclass instance.
     *
     * @return [Index] instance for [String]
     */
    operator fun invoke(): Index<String, Int, Char> = object : StringIndexInstance {}
  }

}
