package arrow.optics.instances

import arrow.data.SetK
import arrow.data.k
import arrow.instance
import arrow.optics.Lens
import arrow.optics.PLens
import arrow.optics.typeclasses.At

/**
 * [At] instance definition for [SetK].
 */
@instance(SetK::class)
interface SetKAtInstance<A> : At<SetK<A>, A, Boolean> {
  override fun at(i: A): Lens<SetK<A>, Boolean> = PLens(
    get = { it.contains(i) },
    set = { b -> { (if (b) it + i else it - i).k() } }
  )
}