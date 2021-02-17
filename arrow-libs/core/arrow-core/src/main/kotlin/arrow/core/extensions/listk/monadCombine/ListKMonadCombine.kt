package arrow.core.extensions.listk.monadCombine

import arrow.Kind
import arrow.core.ForListK
import arrow.core.ListK
import arrow.core.ListK.Companion
import arrow.core.Tuple2
import arrow.core.extensions.ListKMonadCombine
import arrow.typeclasses.Bifoldable
import arrow.typeclasses.Foldable
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val monadCombine_singleton: ListKMonadCombine = object :
  arrow.core.extensions.ListKMonadCombine {}

@JvmName("unite")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Foldable typeclasses is deprecated. Use concrete methods on List")
fun <G, A> Kind<ForListK, Kind<G, A>>.unite(arg1: Foldable<G>): ListK<A> =
  arrow.core.ListK.monadCombine().run {
    this@unite.unite<G, A>(arg1) as arrow.core.ListK<A>
  }

@JvmName("separate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Foldable typeclasses is deprecated. Use concrete methods on List")
fun <G, A, B> Kind<ForListK, Kind<Kind<G, A>, B>>.separate(arg1: Bifoldable<G>):
  Tuple2<Kind<ForListK, A>, Kind<ForListK, B>> = arrow.core.ListK.monadCombine().run {
    this@separate.separate<G, A, B>(arg1) as arrow.core.Tuple2<arrow.Kind<arrow.core.ForListK, A>,
      arrow.Kind<arrow.core.ForListK, B>>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("MonadCombine typeclasses is deprecated. Use concrete methods on List")
inline fun Companion.monadCombine(): ListKMonadCombine = monadCombine_singleton
