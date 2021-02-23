package arrow.optics

import arrow.Kind
import arrow.core.ListExtensions
import arrow.core.ListK
import arrow.core.Tuple2
import arrow.core.k
import arrow.core.left
import arrow.core.right
import arrow.optics.typeclasses.Cons
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.optics.typeclasses.Snoc
import arrow.typeclasses.Applicative

private val stringToList: Iso<String, List<Char>> = Iso(
  get = CharSequence::toList,
  reverseGet = { it.joinToString(separator = "") }
)

/**
 * [Iso] that defines equality between String and [List] of [Char]
 */
@Deprecated(
  "The function has been moved to Iso's companion object.",
  ReplaceWith(
    "Iso.stringToList()",
    "arrow.optics.Iso", "arrow.optics.stringToList"
  ),
  DeprecationLevel.WARNING
)
fun String.Companion.toList(): Iso<String, List<Char>> =
  stringToList

/**
 * [Iso] that defines equality between String and [List] of [Char]
 */
fun PIso.Companion.stringToList(): Iso<String, List<Char>> =
  stringToList

/**
 * [Iso] that defines equality between String and [ListK] of [Char]
 */
@Deprecated(
  "ListK is being deprecated. Use the function defined for List from Iso's companion object.",
  ReplaceWith(
    "Iso.stringToList()",
    "arrow.optics.Iso", "arrow.optics.stringToList"
  ),
  DeprecationLevel.WARNING
)
fun String.Companion.toListK(): Iso<String, ListK<Char>> =
  stringToList compose ListExtensions.toListK()

/**
 * [Traversal] for [String] that focuses in each [Char] of the source [String].
 *
 * @receiver [PTraversal.Companion] to make it statically available.
 * @return [Traversal] with source [String] and foci every [Char] in the source.
 */
fun PTraversal.Companion.string(): Traversal<String, Char> = object : Traversal<String, Char> {
  override fun <F> modifyF(FA: Applicative<F>, s: String, f: (Char) -> Kind<F, Char>): Kind<F, String> = FA.run {
    s.toList().k().traverse(FA, f).map { it.joinToString(separator = "") }
  }
}

/**
 * [FilterIndex] instance for [String].
 * It allows filtering of every [Char] in a [String] by its index's position.
 *
 * @receiver [FilterIndex.Companion] to make the instance statically available.
 * @return [FilterIndex] instance
 */
fun FilterIndex.Companion.string(): FilterIndex<String, Int, Char> = FilterIndex { p ->
  Iso.stringToList() compose FilterIndex.list<Char>().filter(p)
}

/**
 * [Index] instance for [String].
 * It allows access to every [Char] in a [String] by its index's position.
 *
 * @receiver [Index.Companion] to make the instance statically available.
 * @return [Index] instance
 */
fun Index.Companion.string(): Index<String, Int, Char> = Index { i ->
  Iso.stringToList() compose Index.list<Char>().index(i)
}

/**
 * [Cons] instance for [String].
 */
fun Cons.Companion.string(): Cons<String, Char> = Cons {
  Prism(
    getOrModify = { if (it.isNotEmpty()) Tuple2(it.first(), it.drop(1)).right() else it.left() },
    reverseGet = { (h, t) -> h + t }
  )
}

/**
 * [Snoc] instance for [String].
 */
fun Snoc.Companion.string(): Snoc<String, Char> = Snoc {
  Prism(
    getOrModify = { if (it.isNotEmpty()) Tuple2(it.dropLast(1), it.last()).right() else it.left() },
    reverseGet = { (i, l) -> i + l }
  )
}
