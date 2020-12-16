package arrow.core.extensions.validated.traverse

import arrow.Kind
import arrow.core.ForValidated
import arrow.core.Validated
import arrow.core.Validated.Companion
import arrow.core.extensions.ValidatedTraverse
import arrow.core.fix
import arrow.typeclasses.Applicative
import arrow.typeclasses.Monad
import kotlin.Any
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val traverse_singleton: ValidatedTraverse<Any?> = object : ValidatedTraverse<Any?> {}

@JvmName("traverse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated. Replace with traverse or traverseEither from arrow.core.*")
fun <E, G, A, B> Kind<Kind<ForValidated, E>, A>.traverse(
  arg1: Applicative<G>,
  arg2: Function1<A, Kind<G, B>>
): Kind<G, Kind<Kind<ForValidated, E>, B>> = arrow.core.Validated.traverse<E>().run {
  this@traverse.traverse<G, A, B>(arg1, arg2) as arrow.Kind<G,
    arrow.Kind<arrow.Kind<arrow.core.ForValidated, E>, B>>
}

@JvmName("sequence")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated. Replace with sequence or sequenceEither from arrow.core.*")
fun <E, G, A> Kind<Kind<ForValidated, E>, Kind<G, A>>.sequence(arg1: Applicative<G>): Kind<G,
  Kind<Kind<ForValidated, E>, A>> = arrow.core.Validated.traverse<E>().run {
  this@sequence.sequence<G, A>(arg1) as arrow.Kind<G, arrow.Kind<arrow.Kind<arrow.core.ForValidated,
    E>, A>>
}

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("map(arg1)"))
fun <E, A, B> Kind<Kind<ForValidated, E>, A>.map(arg1: Function1<A, B>): Validated<E, B> =
  fix().map(arg1)

@JvmName("flatTraverse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated. This signature is not valid for Validated.")
fun <E, G, A, B> Kind<Kind<ForValidated, E>, A>.flatTraverse(
  arg1: Monad<Kind<ForValidated, E>>,
  arg2: Applicative<G>,
  arg3: Function1<A, Kind<G, Kind<Kind<ForValidated, E>, B>>>
): Kind<G, Kind<Kind<ForValidated, E>, B>> = arrow.core.Validated.traverse<E>().run {
  this@flatTraverse.flatTraverse<G, A, B>(arg1, arg2, arg3) as arrow.Kind<G,
    arrow.Kind<arrow.Kind<arrow.core.ForValidated, E>, B>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Traverse typeclasses is deprecated. Use concrete methods on Validated")
inline fun <E> Companion.traverse(): ValidatedTraverse<E> = traverse_singleton as
  arrow.core.extensions.ValidatedTraverse<E>
