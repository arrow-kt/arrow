package arrow.optics

import arrow.data.SetK
import arrow.data.k
import arrow.core.SetInstances

/**
 * [PIso] that defines the equality between a [Set] and a [SetK]
 */
fun <A, B> SetInstances.toPSetK(): PIso<Set<A>, Set<B>, SetK<A>, SetK<B>> = PIso(
  get = Set<A>::k,
  reverseGet = SetK<B>::set
)

/**
 * [Iso] that defines the equality between a [Set] and a [SetK]
 */
fun <A> SetInstances.toSetK(): Iso<Set<A>, SetK<A>> = toPSetK()
