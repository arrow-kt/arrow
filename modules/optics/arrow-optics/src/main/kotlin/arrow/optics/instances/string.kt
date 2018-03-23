package arrow.optics.instances

import arrow.data.ListK
import arrow.optics.Optional
import arrow.optics.Traversal
import arrow.optics.listToListK
import arrow.optics.stringToList
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.optics.typeclasses.filterIndex

object StringFilterIndexInstance : FilterIndex<String, Int, Char> {
    override fun filter(p: (Int) -> Boolean): Traversal<String, Char> =
            stringToList compose listToListK() compose filterIndex<ListK<Char>, Int, Char>().filter(p)
}

object StringFilterIndexInstanceImplicits {
    @JvmStatic fun instance(): StringFilterIndexInstance = StringFilterIndexInstance
}

object StringIndexInstance : Index<String, Int, Char> {

    override fun index(i: Int): Optional<String, Char> =
            stringToList compose listToListK() compose arrow.optics.typeclasses.index<ListK<Char>, Int, Char>().index(i)
}

object StringIndexInstanceImplicits {
    @JvmStatic
    fun instance(): StringIndexInstance = StringIndexInstance
}