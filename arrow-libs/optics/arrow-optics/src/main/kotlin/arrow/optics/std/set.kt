package arrow.optics

import arrow.optics.typeclasses.At

/**
 * [At] instance definition for [Set].
 */
fun <A> At.Companion.set(): At<Set<A>, A, Boolean> =
  At { i ->
    PLens(
      get = { it.contains(i) },
      set = { s, b -> (if (b) s + i else s - i) }
    )
  }
