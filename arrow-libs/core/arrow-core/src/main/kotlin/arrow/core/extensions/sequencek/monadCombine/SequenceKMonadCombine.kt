package arrow.core.extensions.sequencek.monadCombine

import arrow.Kind
import arrow.core.ForSequenceK
import arrow.core.SequenceK
import arrow.core.SequenceK.Companion
import arrow.core.Tuple2
import arrow.core.extensions.SequenceKMonadCombine
import arrow.typeclasses.Bifoldable
import arrow.typeclasses.Foldable

/**
 * cached extension
 */
@PublishedApi()
internal val monadCombine_singleton: SequenceKMonadCombine = object :
    arrow.core.extensions.SequenceKMonadCombine {}

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
fun <G, A> Kind<ForSequenceK, Kind<G, A>>.unite(arg1: Foldable<G>): SequenceK<A> =
    arrow.core.SequenceK.monadCombine().run {
  this@unite.unite<G, A>(arg1) as arrow.core.SequenceK<A>
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
fun <G, A, B> Kind<ForSequenceK, Kind<Kind<G, A>, B>>.separate(arg1: Bifoldable<G>):
    Tuple2<Kind<ForSequenceK, A>, Kind<ForSequenceK, B>> = arrow.core.SequenceK.monadCombine().run {
  this@separate.separate<G, A, B>(arg1) as arrow.core.Tuple2<arrow.Kind<arrow.core.ForSequenceK, A>,
    arrow.Kind<arrow.core.ForSequenceK, B>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "MonadCombine typeclass is deprecated. Use concrete methods on Sequence",
  level = DeprecationLevel.WARNING
)
inline fun Companion.monadCombine(): SequenceKMonadCombine = monadCombine_singleton
