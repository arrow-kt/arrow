package arrow.optics

import arrow.data.SetKW

/**
 * [PIso] that defines the equality between a [Set] and a [arrow.SetKW]
 */
fun <A, B> pSetToSetKW(): PIso<Set<A>, Set<B>, SetKW<A>, SetKW<B>> = PIso(
        get = { it.k() },
        reverseGet = { it.set }
)

/**
 * [Iso] that defines the equality between a [Set] and a [arrow.SetKW]
 */
fun <A> setToSetKW(): Iso<Set<A>, SetKW<A>> = pSetToSetKW()