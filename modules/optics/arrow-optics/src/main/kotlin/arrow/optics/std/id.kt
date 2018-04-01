package arrow.optics

import arrow.core.Id
import arrow.core.value

/**
 * [PIso] that defines the equality between a [Id] wrapped type [A] and the type [A] itself.
 */
fun <A, B> pIdToType(): PIso<Id<A>, Id<B>, A, B> = PIso(
  get = { it.value() },
  reverseGet = ::Id
)

/**
 * [Iso] that defines the equality between a [Id] wrapped type [A] and the type [A] itself.
 */
fun <A> idToType(): Iso<Id<A>, A> = pIdToType()
