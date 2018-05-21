package arrow.optics.instances

import arrow.Kind
import arrow.data.ListK
import arrow.data.filterIndex
import arrow.data.index
import arrow.data.k
import arrow.data.traverse
import arrow.optics.Optional
import arrow.optics.Traversal
import arrow.optics.listToListK
import arrow.optics.stringToList
import arrow.optics.typeclasses.Each
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.typeclasses.Applicative

fun String.Companion.traversal(): Traversal<String, Char> = object : Traversal<String, Char> {
  override fun <F> modifyF(FA: Applicative<F>, s: String, f: (Char) -> Kind<F, Char>): Kind<F, String> = with(ListK.traverse()) {
    FA.run { s.toList().k().traverse(FA, f).map { it.joinToString(separator = "") } }
  }
}

fun String.Companion.each(): Each<String, Char> = StringEachInstance()

interface StringEachInstance : Each<String, Char> {
  override fun each(): Traversal<String, Char> =
    String.traversal()

  companion object {
    operator fun invoke(): Each<String, Char> = object : StringEachInstance {}
  }
}

fun String.Companion.filterIndex(): FilterIndex<String, Int, Char> = StringFilterIndexInstance()

interface StringFilterIndexInstance : FilterIndex<String, Int, Char> {
  override fun filter(p: (Int) -> Boolean): Traversal<String, Char> =
    stringToList compose listToListK() compose ListK.filterIndex<Char>().filter(p)

  companion object {
    operator fun invoke(): FilterIndex<String, Int, Char> = object : StringFilterIndexInstance {}
  }
}

fun String.Companion.index(): Index<String, Int, Char> = StringIndexInstance()

interface StringIndexInstance : Index<String, Int, Char> {

  override fun index(i: Int): Optional<String, Char> =
    stringToList compose listToListK() compose ListK.index<Char>().index(i)

  companion object {
    operator fun invoke(): Index<String, Int, Char> = object : StringIndexInstance {}
  }

}
