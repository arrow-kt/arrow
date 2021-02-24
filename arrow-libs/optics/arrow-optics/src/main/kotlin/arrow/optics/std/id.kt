package arrow.optics

import arrow.optics.typeclasses.Id

/**
 * [PIso] that defines the equality between a [Id] wrapped type [A] and the type [A] itself.
 */
internal fun <A, B> Id.Companion.toPValue(): PIso<Id<A>, Id<B>, A, B> = PIso(
  get = Id<A>::value,
  reverseGet = ::Id
)

/**
 * [Iso] that defines the equality between a [Id] wrapped type [A] and the type [A] itself.
 */
internal fun <A> Id.Companion.toValue(): Iso<Id<A>, A> = toPValue()
