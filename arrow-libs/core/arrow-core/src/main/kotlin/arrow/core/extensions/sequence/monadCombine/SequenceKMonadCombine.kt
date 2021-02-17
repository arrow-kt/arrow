package arrow.core.extensions.sequence.monadCombine

import arrow.Kind
import arrow.core.ForSequenceK
import arrow.core.Tuple2
import arrow.core.extensions.SequenceKMonadCombine
import arrow.typeclasses.Bifoldable
import arrow.typeclasses.Foldable
import kotlin.sequences.Sequence

@JvmName("unite")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "Foldable typeclass is deprecated. Replace with uniteEither or uniteValidated from arrow.core.*",
  level = DeprecationLevel.WARNING
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
  "Bifoldable typeclass is deprecated. Replace with separateEither or separateValidated from arrow.core.*",
  level = DeprecationLevel.WARNING
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
    "MonadCombine typeclass is deprecated. Use concrete methods on Sequence",
    level = DeprecationLevel.WARNING
  )
  inline fun monadCombine(): SequenceKMonadCombine = monadCombine_singleton
}
