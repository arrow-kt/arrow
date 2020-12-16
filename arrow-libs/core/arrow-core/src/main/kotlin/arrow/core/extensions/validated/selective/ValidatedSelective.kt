package arrow.core.extensions.validated.selective

import arrow.Kind
import arrow.core.Either
import arrow.core.ForValidated
import arrow.core.Validated
import arrow.core.select
import arrow.core.branch
import arrow.core.Validated.Companion
import arrow.core.extensions.ValidatedSelective
import arrow.core.fix
import arrow.core.ifS
import arrow.core.andS
import arrow.core.orS
import arrow.core.whenS
import arrow.typeclasses.Semigroup
import kotlin.Boolean
import kotlin.Function0
import kotlin.Function1
import kotlin.Suppress
import kotlin.Unit
import kotlin.jvm.JvmName

@JvmName("select")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("select(arg1)", "arrow.core.select"))
fun <E, A, B> Kind<Kind<ForValidated, E>, Either<A, B>>.select(
  SE: Semigroup<E>,
  arg1: Kind<Kind<ForValidated, E>, Function1<A, B>>
): Validated<E, B> =
  fix().select(arg1.fix())

@JvmName("branch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("branch(arg1, arg2)", "arrow.core.branch"))
fun <E, A, B, C> Kind<Kind<ForValidated, E>, Either<A, B>>.branch(
  SE: Semigroup<E>,
  arg1: Kind<Kind<ForValidated, E>, Function1<A, C>>,
  arg2: Kind<Kind<ForValidated, E>, Function1<B, C>>
): Validated<E, C> =
  fix().branch(arg1.fix(), arg2.fix())

@JvmName("whenS")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("whenS(arg1)", "arrow.core.whenS"))
fun <E> Kind<Kind<ForValidated, E>, Boolean>.whenS(
  SE: Semigroup<E>,
  arg1: Kind<Kind<ForValidated, E>, Function0<Unit>>
): Validated<E, Unit> =
  fix().whenS(arg1.fix())

@JvmName("ifS")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ifS(arg1, arg2)", "arrow.core.ifS"))
fun <E, A> Kind<Kind<ForValidated, E>, Boolean>.ifS(
  SE: Semigroup<E>,
  arg1: Kind<Kind<ForValidated, E>, A>,
  arg2: Kind<Kind<ForValidated, E>, A>
): Validated<E, A> =
  fix().ifS(arg1.fix(), arg2.fix())

@JvmName("orS")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("orS(arg1)", "arrow.core.ifS"))
fun <E, A> Kind<Kind<ForValidated, E>, Boolean>.orS(
  SE: Semigroup<E>,
  arg1: Kind<Kind<ForValidated, E>, Boolean>
): Validated<E, Boolean> =
  fix().orS(arg1.fix())

@JvmName("andS")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <E, A> Kind<Kind<ForValidated, E>, Boolean>.andS(
  SE: Semigroup<E>,
  arg1: Kind<Kind<ForValidated,
    E>, Boolean>
): Validated<E, Boolean> =
  fix().andS(arg1.fix())

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Selective typeclasses is deprecated. Use concrete methods on Validated")
inline fun <E> Companion.selective(SE: Semigroup<E>): ValidatedSelective<E> = object :
  arrow.core.extensions.ValidatedSelective<E> {
  override fun SE(): arrow.typeclasses.Semigroup<E> =
    SE
}
