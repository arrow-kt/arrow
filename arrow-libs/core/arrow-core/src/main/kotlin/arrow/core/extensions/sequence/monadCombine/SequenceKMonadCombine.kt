package arrow.core.extensions.sequence.monadCombine

import arrow.Kind
import arrow.core.ForSequenceK
import arrow.core.Tuple2
import arrow.core.extensions.SequenceKMonadCombine
import arrow.typeclasses.Bifoldable
import arrow.typeclasses.Foldable
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName
import kotlin.sequences.Sequence

@JvmName("unite")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "unite(arg1)",
  "arrow.core.unite"
  ),
  DeprecationLevel.WARNING
)
fun <G, A> Sequence<Kind<G, A>>.unite(arg1: Foldable<G>): Sequence<A> =
    arrow.core.extensions.sequence.monadCombine.Sequence.monadCombine().run {
  arrow.core.SequenceK(this@unite).unite<G, A>(arg1) as kotlin.sequences.Sequence<A>
}

@JvmName("separate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "separate(arg1)",
  "arrow.core.separate"
  ),
  DeprecationLevel.WARNING
)
fun <G, A, B> Sequence<Kind<Kind<G, A>, B>>.separate(arg1: Bifoldable<G>): Tuple2<Kind<ForSequenceK,
    A>, Kind<ForSequenceK, B>> =
    arrow.core.extensions.sequence.monadCombine.Sequence.monadCombine().run {
  arrow.core.SequenceK(this@separate).separate<G, A, B>(arg1) as
    arrow.core.Tuple2<arrow.Kind<arrow.core.ForSequenceK, A>, arrow.Kind<arrow.core.ForSequenceK,
    B>>
}

/**
 * cached extension
 */
@PublishedApi()
internal val monadCombine_singleton: SequenceKMonadCombine = object :
    arrow.core.extensions.SequenceKMonadCombine {}

object Sequence {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun monadCombine(): SequenceKMonadCombine = monadCombine_singleton}
