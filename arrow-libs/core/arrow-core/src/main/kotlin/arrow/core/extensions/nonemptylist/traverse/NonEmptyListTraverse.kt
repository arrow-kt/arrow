package arrow.core.extensions.nonemptylist.traverse

import arrow.Kind
import arrow.core.ForNonEmptyList
import arrow.core.NonEmptyList
import arrow.core.NonEmptyList.Companion
import arrow.core.extensions.NonEmptyListTraverse
import arrow.typeclasses.Applicative
import arrow.typeclasses.Monad
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val traverse_singleton: NonEmptyListTraverse = object :
    arrow.core.extensions.NonEmptyListTraverse {}

@JvmName("traverse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated, Replace with traverseEither or traverseValidated from arrow.core.*",
  level = DeprecationLevel.WARNING
)
fun <G, A, B> Kind<ForNonEmptyList, A>.traverse(
  arg1: Applicative<G>,
  arg2: Function1<A, Kind<G, B>>
): Kind<G, Kind<ForNonEmptyList, B>> = arrow.core.NonEmptyList.traverse().run {
  this@traverse.traverse<G, A, B>(arg1, arg2) as arrow.Kind<G,
    arrow.Kind<arrow.core.ForNonEmptyList, B>>
}

@JvmName("sequence")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated. Replace with sequenceEither or sequenceValidated from arrow.core.*",
  level = DeprecationLevel.WARNING
)
fun <G, A> Kind<ForNonEmptyList, Kind<G, A>>.sequence(arg1: Applicative<G>): Kind<G,
    Kind<ForNonEmptyList, A>> = arrow.core.NonEmptyList.traverse().run {
  this@sequence.sequence<G, A>(arg1) as arrow.Kind<G, arrow.Kind<arrow.core.ForNonEmptyList, A>>
}

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "fix().map(arg1)",
  "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForNonEmptyList, A>.map(arg1: Function1<A, B>): NonEmptyList<B> =
    arrow.core.NonEmptyList.traverse().run {
  this@map.map<A, B>(arg1) as arrow.core.NonEmptyList<B>
}

@JvmName("flatTraverse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated. Replace with flatTraverseEither or flatTraverseValidated from arrow.core.*",
  level = DeprecationLevel.WARNING
)
fun <G, A, B> Kind<ForNonEmptyList, A>.flatTraverse(
  arg1: Monad<ForNonEmptyList>,
  arg2: Applicative<G>,
  arg3: Function1<A, Kind<G, Kind<ForNonEmptyList, B>>>
): Kind<G, Kind<ForNonEmptyList, B>> = arrow.core.NonEmptyList.traverse().run {
  this@flatTraverse.flatTraverse<G, A, B>(arg1, arg2, arg3) as arrow.Kind<G,
    arrow.Kind<arrow.core.ForNonEmptyList, B>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Traverse typeclass is deprecated. Use concrete methods on NonEmptyList")
inline fun Companion.traverse(): NonEmptyListTraverse = traverse_singleton
