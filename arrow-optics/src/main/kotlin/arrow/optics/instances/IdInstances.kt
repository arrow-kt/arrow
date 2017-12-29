package arrow.optics.instances

import arrow.core.Id
import arrow.core.IdKind
import arrow.core.value
import arrow.optics.Iso
import arrow.optics.PIso

/**
 * [PIso] that defines the equality between a [Id] wrapped type [A] and the type [A] itself.
 */
fun <A, B> pIdToType(): PIso<IdKind<A>, IdKind<B>, A, B> = PIso(
        get = { it.value() },
        reverseGet = ::Id
)

/**
 * [Iso] that defines the equality between a [Id] wrapped type [A] and the type [A] itself.
 */
fun <A> idToType(): Iso<IdKind<A>, A> = pIdToType()