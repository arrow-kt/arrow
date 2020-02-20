package arrow.optics.extensions

import arrow.optics.Lens
import arrow.optics.PLens
import arrow.optics.typeclasses.At

/**
 * [At] instance definition for [Set].
 */
interface SetAt<A> : At<Set<A>, A, Boolean> {
  override fun at(i: A): Lens<Set<A>, Boolean> = PLens(
    get = { it.contains(i) },
    set = { s, b -> (if (b) s + i else s - i) }
  )

  companion object {
    /**
     * Operator overload to instantiate typeclass instance.
     *
     * @return [Index] instance for [String]
     */
    operator fun <A> invoke() = object : SetAt<A> {}
  }
}
