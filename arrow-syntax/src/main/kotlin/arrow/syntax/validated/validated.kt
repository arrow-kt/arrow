package arrow.syntax.validated

import arrow.data.Invalid
import arrow.data.Valid
import arrow.data.ValidatedNel

fun <E, A> A.valid(): Validated<E, A> = Valid(this)

fun <E, A> E.invalid(): Validated<E, A> = Invalid(this)

fun <E, A> A.validNel(): ValidatedNel<E, A> = Validated.validNel(this)

fun <E, A> E.invalidNel(): ValidatedNel<E, A> = Validated.invalidNel(this)