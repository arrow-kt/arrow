package java_util

import arrow.optics.Lens
import arrow.optics.PLens
import arrow.optics.typeclasses.At

interface SetAtInstance<A> : At<Set<A>, A, Boolean> {
    override fun at(i: A): Lens<Set<A>, Boolean> = PLens(
            get = { it.contains(i) },
            set = { b -> { (if (b) it + i else it - i) } }
    )
}

object SetAtInstanceImplicits {
    @JvmStatic
    fun <A> instance(): At<Set<A>, A, Boolean> = object : SetAtInstance<A> {}
}