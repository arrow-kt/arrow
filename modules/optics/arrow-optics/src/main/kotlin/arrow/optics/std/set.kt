package arrow.optics

import arrow.data.SetK
import arrow.data.k
import arrow.core.SetExtensions
import arrow.core.identity

/**
 * [PIso] that defines the equality between a [Set] and a [SetK]
 */
fun <A, B> SetExtensions.toPSetK(): PIso<Set<A>, Set<B>, SetK<A>, SetK<B>> = PIso(
  get = Set<A>::k,
  reverseGet = ::identity
)

/**
 * [Iso] that defines the equality between a [Set] and a [SetK]
 */
fun <A> SetExtensions.toSetK(): Iso<Set<A>, SetK<A>> = toPSetK()
