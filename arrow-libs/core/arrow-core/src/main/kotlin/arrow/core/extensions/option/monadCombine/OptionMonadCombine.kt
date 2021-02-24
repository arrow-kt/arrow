package arrow.core.extensions.option.monadCombine

import arrow.Kind
import arrow.core.ForOption
import arrow.core.Option
import arrow.core.Option.Companion
import arrow.core.Tuple2
import arrow.core.extensions.OptionMonadCombine
import arrow.typeclasses.Bifoldable
import arrow.typeclasses.Foldable

/**
 * cached extension
 */
@PublishedApi()
internal val monadCombine_singleton: OptionMonadCombine = object :
  arrow.core.extensions.OptionMonadCombine {}

@JvmName("unite")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "Foldable typeclass is deprecated. Replace with unite, uniteEither or uniteValidated from arrow.core.*",
  level = DeprecationLevel.WARNING
)
fun <G, A> Kind<ForOption, Kind<G, A>>.unite(arg1: Foldable<G>): Option<A> =
  arrow.core.Option.monadCombine().run {
    this@unite.unite<G, A>(arg1) as arrow.core.Option<A>
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
fun <G, A, B> Kind<ForOption, Kind<Kind<G, A>, B>>.separate(arg1: Bifoldable<G>):
  Tuple2<Kind<ForOption, A>, Kind<ForOption, B>> = arrow.core.Option.monadCombine().run {
    this@separate.separate<G, A, B>(arg1) as arrow.core.Tuple2<arrow.Kind<arrow.core.ForOption, A>,
      arrow.Kind<arrow.core.ForOption, B>>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "MonadCombine typeclass is deprecated. Use concrete methods on Option",
  level = DeprecationLevel.WARNING
)
inline fun Companion.monadCombine(): OptionMonadCombine = monadCombine_singleton
