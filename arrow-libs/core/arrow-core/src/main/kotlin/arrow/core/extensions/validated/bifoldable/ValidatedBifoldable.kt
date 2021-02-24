package arrow.core.extensions.validated.bifoldable

import arrow.Kind
import arrow.core.Eval
import arrow.core.ForValidated
import arrow.core.Validated.Companion
import arrow.core.extensions.ValidatedBifoldable
import arrow.core.fix
import arrow.typeclasses.Monoid
import kotlin.Function1
import kotlin.Function2
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val bifoldable_singleton: ValidatedBifoldable = object :
  arrow.core.extensions.ValidatedBifoldable {}

@JvmName("bifoldLeft")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)

@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("bifoldLeft(arg1, arg2, arg3)"))
fun <A, B, C> Kind<Kind<ForValidated, A>, B>.bifoldLeft(
  arg1: C,
  arg2: Function2<C, A, C>,
  arg3: Function2<C, B, C>
): C = fix().bifoldLeft(arg1, arg2, arg3)

@JvmName("bifoldRight")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("bifoldRight(arg1, arg2, arg3)"))
fun <A, B, C> Kind<Kind<ForValidated, A>, B>.bifoldRight(
  arg1: Eval<C>,
  arg2: Function2<A, Eval<C>, Eval<C>>,
  arg3: Function2<B, Eval<C>, Eval<C>>
): Eval<C> = fix().bifoldRight(arg1, arg2, arg3)

@JvmName("bifoldMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("bifoldMap(arg1, arg2, arg3)"))
fun <A, B, C> Kind<Kind<ForValidated, A>, B>.bifoldMap(
  arg1: Monoid<C>,
  arg2: Function1<A, C>,
  arg3: Function1<B, C>
): C = fix().bifoldMap(arg1, arg2, arg3)

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Bifoldable typeclasses is deprecated. Use concrete methods on Validated")
inline fun Companion.bifoldable(): ValidatedBifoldable = bifoldable_singleton
