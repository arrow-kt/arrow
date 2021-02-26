package arrow.optics

import arrow.core.left
import arrow.core.right
import arrow.optics.typeclasses.Cons
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.optics.typeclasses.Snoc
import arrow.typeclasses.Monoid

private val stringToList: Iso<String, List<Char>> =
  Iso(
    get = CharSequence::toList,
    reverseGet = { it.joinToString(separator = "") }
  )

/**
 * [Iso] that defines equality between String and [List] of [Char]
 */
fun PIso.Companion.stringToList(): Iso<String, List<Char>> =
  stringToList

/**
 * [Traversal] for [String] that focuses in each [Char] of the source [String].
 *
 * @receiver [PTraversal.Companion] to make it statically available.
 * @return [Traversal] with source [String] and foci every [Char] in the source.
 */
fun PTraversal.Companion.string(): Traversal<String, Char> =
  Traversal { s, f -> s.map(f).joinToString(separator = "") }

fun Fold.Companion.string(): Fold<String, Char> =
  object : Fold<String, Char> {
    override fun <R> foldMap(M: Monoid<R>, source: String, map: (Char) -> R): R =
      M.run { source.map(map).fold(empty()) { acc, r -> acc.combine(r) } }
  }

fun PEvery.Companion.string(): Every<String, Char> =
  object : Every<String, Char> {
    override fun <R> foldMap(M: Monoid<R>, source: String, map: (Char) -> R): R =
      M.run { source.fold(empty()) { acc, r -> acc.combine(map(r)) } }

    override fun modify(source: String, map: (focus: Char) -> Char): String =
      source.map(map).joinToString(separator = "")
  }

/**
 * [FilterIndex] instance for [String].
 * It allows filtering of every [Char] in a [String] by its index's position.
 *
 * @receiver [FilterIndex.Companion] to make the instance statically available.
 * @return [FilterIndex] instance
 */
fun FilterIndex.Companion.string(): FilterIndex<String, Int, Char> =
  FilterIndex { p ->
    Iso.stringToList() compose FilterIndex.list<Char>().filter(p)
  }

/**
 * [Index] instance for [String].
 * It allows access to every [Char] in a [String] by its index's position.
 *
 * @receiver [Index.Companion] to make the instance statically available.
 * @return [Index] instance
 */
fun Index.Companion.string(): Index<String, Int, Char> =
  Index { i ->
    Iso.stringToList() compose Index.list<Char>().index(i)
  }

/**
 * [Cons] instance for [String].
 */
fun Cons.Companion.string(): Cons<String, Char> =
  Cons {
    Prism(
      getOrModify = { if (it.isNotEmpty()) Pair(it.first(), it.drop(1)).right() else it.left() },
      reverseGet = { (h, t) -> h + t }
    )
  }

/**
 * [Snoc] instance for [String].
 */
fun Snoc.Companion.string(): Snoc<String, Char> =
  Snoc {
    Prism(
      getOrModify = { if (it.isNotEmpty()) Pair(it.dropLast(1), it.last()).right() else it.left() },
      reverseGet = { (i, l) -> i + l }
    )
  }
