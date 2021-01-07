package arrow.optics.extensions

import arrow.optics.PLens
import arrow.optics.typeclasses.At

/**
 * [At] instance definition for [Set].
 */
fun <A> setAt(): At<Set<A>, A, Boolean> = At { i ->
  PLens(
    get = { it.contains(i) },
    set = { s, b -> (if (b) s + i else s - i) }
  )
}
