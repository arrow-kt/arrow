package arrow.optics.extensions

import arrow.core.SetK
import arrow.core.k
import arrow.extension
import arrow.optics.Lens
import arrow.optics.PLens
import arrow.optics.typeclasses.At

/**
 * [At] instance definition for [SetK].
 */
@extension
interface SetKAt<A> : At<SetK<A>, A, Boolean> {
  override fun at(i: A): Lens<SetK<A>, Boolean> = PLens(
    get = { it.contains(i) },
    set = { s, b -> (if (b) s + i else s - i).k() }
  )
}
