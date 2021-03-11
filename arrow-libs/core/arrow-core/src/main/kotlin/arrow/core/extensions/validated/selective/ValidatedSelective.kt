package arrow.core.extensions.validated.selective

import arrow.Kind
import arrow.core.Either
import arrow.core.ForValidated
import arrow.core.Validated
import arrow.core.Validated.Companion
import arrow.core.extensions.ValidatedSelective
import arrow.core.fix
import arrow.typeclasses.SelectiveDeprecation
import arrow.typeclasses.Semigroup

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
): Validated<E, B> = arrow.core.Validated.selective(SE).run {
  fix().select<A, B>(arg1.fix()) as arrow.core.Validated<E, B>
}

@JvmName("branch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(SelectiveDeprecation)
fun <E, A, B, C> Kind<Kind<ForValidated, E>, Either<A, B>>.branch(
  SE: Semigroup<E>,
  arg1: Kind<Kind<ForValidated, E>, Function1<A, C>>,
  arg2: Kind<Kind<ForValidated, E>, Function1<B, C>>
): Validated<E, C> = arrow.core.Validated.selective(SE).run {
  fix().branch<A, B, C>(arg1.fix(), arg2.fix()) as arrow.core.Validated<E, C>
}

@JvmName("whenS")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(SelectiveDeprecation)
fun <E> Kind<Kind<ForValidated, E>, Boolean>.whenS(
  SE: Semigroup<E>,
  arg1: Kind<Kind<ForValidated, E>, Function0<Unit>>
): Validated<E, Unit> = arrow.core.Validated.selective(SE).run {
  fix().whenS<E>(arg1.fix()) as arrow.core.Validated<E, Unit>
}

@JvmName("ifS")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(SelectiveDeprecation)
fun <E, A> Kind<Kind<ForValidated, E>, Boolean>.ifS(
  SE: Semigroup<E>,
  arg1: Kind<Kind<ForValidated, E>, A>,
  arg2: Kind<Kind<ForValidated, E>, A>
): Validated<E, A> = arrow.core.Validated.selective(SE).run {
  fix().ifS(arg1.fix(), arg2.fix()) as arrow.core.Validated<E, A>
}

@JvmName("orS")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(SelectiveDeprecation)
fun <E, A> Kind<Kind<ForValidated, E>, Boolean>.orS(
  SE: Semigroup<E>,
  arg1: Kind<Kind<ForValidated, E>, Boolean>
): Validated<E, Boolean> = arrow.core.Validated.selective(SE).run {
  fix().orS<A>(arg1.fix()) as arrow.core.Validated<E, Boolean>
}

@JvmName("andS")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(SelectiveDeprecation)
fun <E, A> Kind<Kind<ForValidated, E>, Boolean>.andS(
  SE: Semigroup<E>,
  arg1: Kind<Kind<ForValidated,
    E>, Boolean>
): Validated<E, Boolean> = arrow.core.Validated.selective(SE).run {
  fix().andS<A>(arg1.fix()) as arrow.core.Validated<E, Boolean>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Selective typeclasses is deprecated. Use concrete methods on Validated")
inline fun <E> Companion.selective(SE: Semigroup<E>): ValidatedSelective<E> =
  object : arrow.core.extensions.ValidatedSelective<E> {
    override fun SE(): arrow.typeclasses.Semigroup<E> =
      SE
  }
