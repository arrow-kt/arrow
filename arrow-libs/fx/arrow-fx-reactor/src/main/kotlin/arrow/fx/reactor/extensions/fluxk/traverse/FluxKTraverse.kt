package arrow.fx.reactor.extensions.fluxk.traverse

import arrow.Kind
import arrow.fx.reactor.DeprecateReactor
import arrow.fx.reactor.FluxK
import arrow.fx.reactor.FluxK.Companion
import arrow.fx.reactor.ForFluxK
import arrow.fx.reactor.extensions.FluxKTraverse
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
internal val traverse_singleton: FluxKTraverse = object : arrow.fx.reactor.extensions.FluxKTraverse
{}

@JvmName("traverse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <G, A, B> Kind<ForFluxK, A>.traverse(arg1: Applicative<G>, arg2: Function1<A, Kind<G, B>>):
  Kind<G, Kind<ForFluxK, B>> = arrow.fx.reactor.FluxK.traverse().run {
    this@traverse.traverse<G, A, B>(arg1, arg2) as arrow.Kind<G, arrow.Kind<arrow.fx.reactor.ForFluxK,
        B>>
  }

@JvmName("sequence")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <G, A> Kind<ForFluxK, Kind<G, A>>.sequence(arg1: Applicative<G>): Kind<G, Kind<ForFluxK, A>> =
  arrow.fx.reactor.FluxK.traverse().run {
    this@sequence.sequence<G, A>(arg1) as arrow.Kind<G, arrow.Kind<arrow.fx.reactor.ForFluxK, A>>
  }

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForFluxK, A>.map(arg1: Function1<A, B>): FluxK<B> =
  arrow.fx.reactor.FluxK.traverse().run {
    this@map.map<A, B>(arg1) as arrow.fx.reactor.FluxK<B>
  }

@JvmName("flatTraverse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <G, A, B> Kind<ForFluxK, A>.flatTraverse(
  arg1: Monad<ForFluxK>,
  arg2: Applicative<G>,
  arg3: Function1<A, Kind<G, Kind<ForFluxK, B>>>
): Kind<G, Kind<ForFluxK, B>> = arrow.fx.reactor.FluxK.traverse().run {
  this@flatTraverse.flatTraverse<G, A, B>(arg1, arg2, arg3) as arrow.Kind<G,
    arrow.Kind<arrow.fx.reactor.ForFluxK, B>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateReactor)
inline fun Companion.traverse(): FluxKTraverse = traverse_singleton
