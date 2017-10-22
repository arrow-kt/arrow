package kategory.optics

import kategory.Id
import kategory.IdKind
import kategory.value

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