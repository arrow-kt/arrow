package arrow.core.extensions.ior.bitraverse

import arrow.Kind
import arrow.core.ForIor
import arrow.core.Ior
import arrow.core.Ior.Companion
import arrow.core.extensions.IorBitraverse
import arrow.typeclasses.Applicative

/**
 * cached extension
 */
@PublishedApi()
internal val bitraverse_singleton: IorBitraverse = object : arrow.core.extensions.IorBitraverse {}

@JvmName("bitraverse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated. Replace with bitraverse, bitraverseEither or bitraverseValidated from arrow.core.*",
  level = DeprecationLevel.WARNING
)
fun <G, A, B, C, D> Kind<Kind<ForIor, A>, B>.bitraverse(
  arg1: Applicative<G>,
  arg2: Function1<A, Kind<G, C>>,
  arg3: Function1<B, Kind<G, D>>
): Kind<G, Kind<Kind<ForIor, C>, D>> = arrow.core.Ior.bitraverse().run {
  this@bitraverse.bitraverse<G, A, B, C, D>(arg1, arg2, arg3) as arrow.Kind<G,
    arrow.Kind<arrow.Kind<arrow.core.ForIor, C>, D>>
}

@JvmName("bisequence")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated. Replace with bisequence, bisequenceEither or bisequenceValidated from arrow.core.*",
  level = DeprecationLevel.WARNING
)
fun <G, A, B> Kind<Kind<ForIor, Kind<G, A>>, Kind<G, B>>.bisequence(arg1: Applicative<G>): Kind<G,
    Kind<Kind<ForIor, A>, B>> = arrow.core.Ior.bitraverse().run {
  this@bisequence.bisequence<G, A, B>(arg1) as arrow.Kind<G,
    arrow.Kind<arrow.Kind<arrow.core.ForIor, A>, B>>
}

@JvmName("bimap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "this.bimap(arg1, arg2)"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D> Kind<Kind<ForIor, A>, B>.bimap(arg1: Function1<A, C>, arg2: Function1<B, D>):
    Ior<C, D> = arrow.core.Ior.bitraverse().run {
  this@bimap.bimap<A, B, C, D>(arg1, arg2) as arrow.core.Ior<C, D>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Bitraverse typeclass is deprecated. Use concrete methods on Ior",
  level = DeprecationLevel.WARNING
)
inline fun Companion.bitraverse(): IorBitraverse = bitraverse_singleton
