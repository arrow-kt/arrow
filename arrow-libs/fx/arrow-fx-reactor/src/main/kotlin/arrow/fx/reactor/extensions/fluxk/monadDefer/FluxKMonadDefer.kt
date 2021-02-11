package arrow.fx.reactor.extensions.fluxk.monadDefer

import arrow.Kind
import arrow.core.Either
import arrow.fx.Ref
import arrow.fx.reactor.DeprecateReactor
import arrow.fx.reactor.FluxK
import arrow.fx.reactor.FluxK.Companion
import arrow.fx.reactor.ForFluxK
import arrow.fx.reactor.extensions.FluxKMonadDefer
import kotlin.Deprecated
import kotlin.Function0
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Throwable
import kotlin.Unit
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val monadDefer_singleton: FluxKMonadDefer = object :
    arrow.fx.reactor.extensions.FluxKMonadDefer {}

@JvmName("defer")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> defer(arg0: Function0<Kind<ForFluxK, A>>): FluxK<A> = arrow.fx.reactor.FluxK
   .monadDefer()
   .defer<A>(arg0) as arrow.fx.reactor.FluxK<A>

@JvmName("later")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> later(arg0: Function0<A>): FluxK<A> = arrow.fx.reactor.FluxK
   .monadDefer()
   .later<A>(arg0) as arrow.fx.reactor.FluxK<A>

@JvmName("later")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> later(arg0: Kind<ForFluxK, A>): FluxK<A> = arrow.fx.reactor.FluxK
   .monadDefer()
   .later<A>(arg0) as arrow.fx.reactor.FluxK<A>

@JvmName("lazy")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun lazy(): FluxK<Unit> = arrow.fx.reactor.FluxK
   .monadDefer()
   .lazy() as arrow.fx.reactor.FluxK<kotlin.Unit>

@JvmName("laterOrRaise")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> laterOrRaise(arg0: Function0<Either<Throwable, A>>): FluxK<A> = arrow.fx.reactor.FluxK
   .monadDefer()
   .laterOrRaise<A>(arg0) as arrow.fx.reactor.FluxK<A>

@JvmName("Ref")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Ref(arg0: A): FluxK<Ref<ForFluxK, A>> = arrow.fx.reactor.FluxK
   .monadDefer()
   .Ref<A>(arg0) as arrow.fx.reactor.FluxK<arrow.fx.Ref<arrow.fx.reactor.ForFluxK, A>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateReactor)
inline fun Companion.monadDefer(): FluxKMonadDefer = monadDefer_singleton
