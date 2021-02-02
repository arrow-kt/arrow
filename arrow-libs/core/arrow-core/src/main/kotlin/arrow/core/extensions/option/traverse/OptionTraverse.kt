package arrow.core.extensions.option.traverse

import arrow.Kind
import arrow.core.ForOption
import arrow.core.Option
import arrow.core.Option.Companion
import arrow.core.extensions.OptionTraverse
import arrow.typeclasses.Applicative
import arrow.typeclasses.Monad

/**
 * cached extension
 */
@PublishedApi()
internal val traverse_singleton: OptionTraverse = object : arrow.core.extensions.OptionTraverse {}

@JvmName("traverse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "Applicative typeclass is deprecated, Replace with traverse, traverseEither or traverseValidated from arrow.core.*",
  level = DeprecationLevel.WARNING
)
fun <G, A, B> Kind<ForOption, A>.traverse(arg1: Applicative<G>, arg2: Function1<A, Kind<G, B>>):
    Kind<G, Kind<ForOption, B>> = arrow.core.Option.traverse().run {
  this@traverse.traverse<G, A, B>(arg1, arg2) as arrow.Kind<G, arrow.Kind<arrow.core.ForOption, B>>
}

@JvmName("sequence")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "Applicative typeclass is deprecated, Replace with sequence, sequenceEither or sequenceValidated from arrow.core.*",
  level = DeprecationLevel.WARNING
)
fun <G, A> Kind<ForOption, Kind<G, A>>.sequence(arg1: Applicative<G>): Kind<G, Kind<ForOption, A>> =
    arrow.core.Option.traverse().run {
  this@sequence.sequence<G, A>(arg1) as arrow.Kind<G, arrow.Kind<arrow.core.ForOption, A>>
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
  "map(arg1)"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForOption, A>.map(arg1: Function1<A, B>): Option<B> =
    arrow.core.Option.traverse().run {
  this@map.map<A, B>(arg1) as arrow.core.Option<B>
}

@JvmName("flatTraverse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "Applicative and Monad typeclasses are deprecated, Replace with flatTraverse, flatTraverseEither or flatTraverseValidated from arrow.core.*",
  level = DeprecationLevel.WARNING
)
fun <G, A, B> Kind<ForOption, A>.flatTraverse(
  arg1: Monad<ForOption>,
  arg2: Applicative<G>,
  arg3: Function1<A, Kind<G, Kind<ForOption, B>>>
): Kind<G, Kind<ForOption, B>> = arrow.core.Option.traverse().run {
  this@flatTraverse.flatTraverse<G, A, B>(arg1, arg2, arg3) as arrow.Kind<G,
    arrow.Kind<arrow.core.ForOption, B>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Traverse typeclass is deprecated. Use concrete methods on Option",
  level = DeprecationLevel.WARNING
)
inline fun Companion.traverse(): OptionTraverse = traverse_singleton
