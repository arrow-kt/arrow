package arrow.optics.instances

import arrow.data.ListK
import arrow.data.filterIndex
import arrow.data.index
import arrow.optics.Optional
import arrow.optics.Traversal
import arrow.optics.listToListK
import arrow.optics.stringToList
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index

interface StringFilterIndexInstance : FilterIndex<String, Int, Char> {
  override fun filter(p: (Int) -> Boolean): Traversal<String, Char> =
    stringToList compose listToListK() compose ListK.filterIndex<Char>().filter(p)

  companion object {
    operator fun invoke() = object : StringFilterIndexInstance {}
  }
}

interface StringIndexInstance : Index<String, Int, Char> {

  override fun index(i: Int): Optional<String, Char> =
    stringToList compose listToListK() compose ListK.index<Char>().index(i)

  companion object {
    operator fun invoke() = object : StringIndexInstance {}
  }
}
