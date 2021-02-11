package arrow.fx.rx2.extensions.flowablek.monadDefer

import arrow.Kind
import arrow.core.Either
import arrow.fx.Ref
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.FlowableK
import arrow.fx.rx2.FlowableK.Companion
import arrow.fx.rx2.ForFlowableK
import arrow.fx.rx2.extensions.FlowableKMonadDefer
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
internal val monadDefer_singleton: FlowableKMonadDefer = object :
    arrow.fx.rx2.extensions.FlowableKMonadDefer {}

@JvmName("defer")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> defer(arg0: Function0<Kind<ForFlowableK, A>>): FlowableK<A> = arrow.fx.rx2.FlowableK
   .monadDefer()
   .defer<A>(arg0) as arrow.fx.rx2.FlowableK<A>

@JvmName("later")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> later(arg0: Function0<A>): FlowableK<A> = arrow.fx.rx2.FlowableK
   .monadDefer()
   .later<A>(arg0) as arrow.fx.rx2.FlowableK<A>

@JvmName("later")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> later(arg0: Kind<ForFlowableK, A>): FlowableK<A> = arrow.fx.rx2.FlowableK
   .monadDefer()
   .later<A>(arg0) as arrow.fx.rx2.FlowableK<A>

@JvmName("lazy")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun lazy(): FlowableK<Unit> = arrow.fx.rx2.FlowableK
   .monadDefer()
   .lazy() as arrow.fx.rx2.FlowableK<kotlin.Unit>

@JvmName("laterOrRaise")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> laterOrRaise(arg0: Function0<Either<Throwable, A>>): FlowableK<A> = arrow.fx.rx2.FlowableK
   .monadDefer()
   .laterOrRaise<A>(arg0) as arrow.fx.rx2.FlowableK<A>

@JvmName("Ref")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Ref(arg0: A): FlowableK<Ref<ForFlowableK, A>> = arrow.fx.rx2.FlowableK
   .monadDefer()
   .Ref<A>(arg0) as arrow.fx.rx2.FlowableK<arrow.fx.Ref<arrow.fx.rx2.ForFlowableK, A>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.monadDefer(): FlowableKMonadDefer = monadDefer_singleton
