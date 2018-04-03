package arrow.optics

import arrow.core.identity
import arrow.data.SetK
import arrow.data.fix
import arrow.data.k

/**
 * [PIso] that defines the equality between a [arrow.SetK] and a [Set]
 */
fun <A, B> SetK.Companion.toPSet(): PIso<SetK<A>, SetK<B>, Set<A>, Set<B>> = PIso(
  get = ::identity,
  reverseGet = { it.k() }
)

fun <A> SetK.Companion.toSet(): Iso<SetK<A>, Set<A>> = toPSet()
