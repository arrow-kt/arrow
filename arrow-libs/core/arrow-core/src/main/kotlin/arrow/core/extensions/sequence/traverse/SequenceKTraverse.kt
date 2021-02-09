package arrow.core.extensions.sequence.traverse

import arrow.Kind
import arrow.core.ForSequenceK
import arrow.core.extensions.SequenceKTraverse
import arrow.typeclasses.Applicative
import arrow.typeclasses.Monad
import kotlin.sequences.Sequence

@JvmName("traverse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated. Replace with traverseEither or traverseValidated from arrow.core.*",
  level = DeprecationLevel.WARNING
)
fun <G, A, B> Sequence<A>.traverse(arg1: Applicative<G>, arg2: Function1<A, Kind<G, B>>): Kind<G,
    Kind<ForSequenceK, B>> = arrow.core.extensions.sequence.traverse.Sequence.traverse().run {
  arrow.core.SequenceK(this@traverse).traverse<G, A, B>(arg1, arg2) as arrow.Kind<G,
    arrow.Kind<arrow.core.ForSequenceK, B>>
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
fun <G, A> Sequence<Kind<G, A>>.sequence(arg1: Applicative<G>): Kind<G, Kind<ForSequenceK, A>> =
    arrow.core.extensions.sequence.traverse.Sequence.traverse().run {
  arrow.core.SequenceK(this@sequence).sequence<G, A>(arg1) as arrow.Kind<G,
    arrow.Kind<arrow.core.ForSequenceK, A>>
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
    "this.map(arg1)"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Sequence<A>.map(arg1: Function1<A, B>): Sequence<B> =
    arrow.core.extensions.sequence.traverse.Sequence.traverse().run {
  arrow.core.SequenceK(this@map).map<A, B>(arg1) as kotlin.sequences.Sequence<B>
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
fun <G, A, B> Sequence<A>.flatTraverse(
  arg1: Monad<ForSequenceK>,
  arg2: Applicative<G>,
  arg3: Function1<A, Kind<G, Kind<ForSequenceK, B>>>
): Kind<G, Kind<ForSequenceK, B>> =
    arrow.core.extensions.sequence.traverse.Sequence.traverse().run {
  arrow.core.SequenceK(this@flatTraverse).flatTraverse<G, A, B>(arg1, arg2, arg3) as arrow.Kind<G,
    arrow.Kind<arrow.core.ForSequenceK, B>>
}

/**
 * cached extension
 */
@PublishedApi()
internal val traverse_singleton: SequenceKTraverse = object :
    arrow.core.extensions.SequenceKTraverse {}

@Deprecated(
  "Receiver Sequence object is deprecated, prefer to turn Sequence functions into top-level functions",
  level = DeprecationLevel.WARNING
)
object Sequence {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated(
    "Traverse typeclass is deprecated. Use concrete methods on Sequence",
    level = DeprecationLevel.WARNING
  )
  inline fun traverse(): SequenceKTraverse = traverse_singleton}
