package arrow.optics

import arrow.core.Id
import arrow.core.IdOf
import arrow.core.value

/**
 * [PIso] that defines the equality between a [Id] wrapped type [A] and the type [A] itself.
 */
fun <A, B> pIdToType(): PIso<IdOf<A>, IdOf<B>, A, B> = PIso(
        get = { it.value() },
        reverseGet = ::Id
)

/**
 * [Iso] that defines the equality between a [Id] wrapped type [A] and the type [A] itself.
 */
fun <A> idToType(): Iso<IdOf<A>, A> = pIdToType()
