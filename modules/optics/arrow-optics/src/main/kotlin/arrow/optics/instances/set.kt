package arrow.optics.instances

import arrow.optics.Lens
import arrow.optics.PLens
import arrow.optics.typeclasses.At

interface SetAtInstance<A> : At<Set<A>, A, Boolean> {
  override fun at(i: A): Lens<Set<A>, Boolean> = PLens(
    get = { it.contains(i) },
    set = { b -> { (if (b) it + i else it - i) } }
  )

  companion object {
    operator fun <A> invoke() = object : SetAtInstance<A> {}
  }
}
