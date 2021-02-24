package arrow.core.extensions.validated.eqK

import arrow.Kind
import arrow.core.ForValidated
import arrow.core.Validated.Companion
import arrow.core.extensions.ValidatedEqK
import arrow.typeclasses.Eq
import kotlin.Boolean
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("eqK")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0")
fun <L, A> Kind<Kind<ForValidated, L>, A>.eqK(
  EQL: Eq<L>,
  arg1: Kind<Kind<ForValidated, L>, A>,
  arg2: Eq<A>
): Boolean = arrow.core.Validated.eqK<L>(EQL).run {
  this@eqK.eqK<A>(arg1, arg2) as kotlin.Boolean
}

@JvmName("liftEq")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0")
fun <L, A> liftEq(EQL: Eq<L>, arg0: Eq<A>): Eq<Kind<Kind<ForValidated, L>, A>> =
  arrow.core.Validated
    .eqK<L>(EQL)
    .liftEq<A>(arg0) as arrow.typeclasses.Eq<arrow.Kind<arrow.Kind<arrow.core.ForValidated, L>, A>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0")
inline fun <L> Companion.eqK(EQL: Eq<L>): ValidatedEqK<L> = object :
  arrow.core.extensions.ValidatedEqK<L> { override fun EQL(): arrow.typeclasses.Eq<L> = EQL }
