package arrow.core.extensions.validated.applicative

import arrow.Kind
import arrow.core.ForValidated
import arrow.core.Validated
import arrow.core.Validated.Companion
import arrow.core.extensions.ValidatedApplicative
import arrow.core.fix
import arrow.core.valid
import arrow.core.replicate as _replicate
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import kotlin.Function1
import kotlin.Int
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("just1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("valid<A>()", "arrow.core.valid"))
fun <E, A> A.just(SE: Semigroup<E>): Validated<E, A> =
  valid()

@JvmName("unit")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Validated.unit<E>()", "arrow.core.Validated"))
fun <E> unit(SE: Semigroup<E>): Validated<E, Unit> =
  Validated.unit()

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("map(arg1)"))
fun <E, A, B> Kind<Kind<ForValidated, E>, A>.map(SE: Semigroup<E>, arg1: Function1<A, B>):
    Validated<E, B> = fix().map(arg1)

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("replicate(SE, arg1)", "arrow.core.replicate"))
fun <E, A> Kind<Kind<ForValidated, E>, A>.replicate(SE: Semigroup<E>, arg1: Int): Validated<E, List<A>> =
  fix()._replicate(SE, arg1)

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("replicate(SE, arg1, arg2)", "arrow.core.replicate"))
fun <E, A> Kind<Kind<ForValidated, E>, A>.replicate(
  SE: Semigroup<E>,
  arg1: Int,
  arg2: Monoid<A>
): Validated<E, A> = fix()._replicate(SE, arg1, arg2)

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Applicative typeclasses is deprecated. Use concrete methods on Validated")
inline fun <E> Companion.applicative(SE: Semigroup<E>): ValidatedApplicative<E> = object :
    arrow.core.extensions.ValidatedApplicative<E> { override fun SE():
    arrow.typeclasses.Semigroup<E> = SE }
