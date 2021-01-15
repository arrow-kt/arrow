package arrow.core.extensions.sequencek.monadCombine

import arrow.Kind
import arrow.core.ForSequenceK
import arrow.core.SequenceK
import arrow.core.SequenceK.Companion
import arrow.core.Tuple2
import arrow.core.extensions.SequenceKMonadCombine
import arrow.typeclasses.Bifoldable
import arrow.typeclasses.Foldable
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

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
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "unite(arg1)",
  "arrow.core.unite"
  ),
  DeprecationLevel.WARNING
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
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "separate(arg1)",
  "arrow.core.separate"
  ),
  DeprecationLevel.WARNING
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
inline fun Companion.monadCombine(): SequenceKMonadCombine = monadCombine_singleton
