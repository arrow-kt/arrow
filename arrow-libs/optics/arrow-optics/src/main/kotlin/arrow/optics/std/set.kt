package arrow.optics

import arrow.core.SetExtensions
import arrow.core.SetK
import arrow.core.identity
import arrow.core.k

/**
 * [PIso] that defines the equality between a [Set] and a [SetK]
 */
@Deprecated(
  message = "SetK is being deprecated, and it will be removed in 0.13.",
  level = DeprecationLevel.WARNING
)
fun <A, B> SetExtensions.toPSetK(): PIso<Set<A>, Set<B>, SetK<A>, SetK<B>> = PIso(
  get = Set<A>::k,
  reverseGet = ::identity
)

/**
 * [Iso] that defines the equality between a [Set] and a [SetK]
 */
@Deprecated(
  message = "SetK is being deprecated, and it will be removed in 0.13.",
  level = DeprecationLevel.WARNING
)
fun <A> SetExtensions.toSetK(): Iso<Set<A>, SetK<A>> = toPSetK()
