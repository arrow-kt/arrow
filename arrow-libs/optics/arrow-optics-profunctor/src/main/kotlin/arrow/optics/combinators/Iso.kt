package arrow.optics.combinators

import arrow.core.identity
import arrow.optics.Iso
import arrow.optics.Optic
import arrow.optics.iso

fun <S> Optic.Companion.id(): Iso<S, S> = Optic.iso(::identity, ::identity)
