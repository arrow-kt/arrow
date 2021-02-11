package arrow.fx.rx2.extensions.observablek.monadDefer

import arrow.Kind
import arrow.core.Either
import arrow.fx.Ref
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.ForObservableK
import arrow.fx.rx2.ObservableK
import arrow.fx.rx2.ObservableK.Companion
import arrow.fx.rx2.extensions.ObservableKMonadDefer
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
internal val monadDefer_singleton: ObservableKMonadDefer = object :
    arrow.fx.rx2.extensions.ObservableKMonadDefer {}

@JvmName("defer")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> defer(arg0: Function0<Kind<ForObservableK, A>>): ObservableK<A> = arrow.fx.rx2.ObservableK
   .monadDefer()
   .defer<A>(arg0) as arrow.fx.rx2.ObservableK<A>

@JvmName("later")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> later(arg0: Function0<A>): ObservableK<A> = arrow.fx.rx2.ObservableK
   .monadDefer()
   .later<A>(arg0) as arrow.fx.rx2.ObservableK<A>

@JvmName("later")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> later(arg0: Kind<ForObservableK, A>): ObservableK<A> = arrow.fx.rx2.ObservableK
   .monadDefer()
   .later<A>(arg0) as arrow.fx.rx2.ObservableK<A>

@JvmName("lazy")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun lazy(): ObservableK<Unit> = arrow.fx.rx2.ObservableK
   .monadDefer()
   .lazy() as arrow.fx.rx2.ObservableK<kotlin.Unit>

@JvmName("laterOrRaise")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> laterOrRaise(arg0: Function0<Either<Throwable, A>>): ObservableK<A> =
    arrow.fx.rx2.ObservableK
   .monadDefer()
   .laterOrRaise<A>(arg0) as arrow.fx.rx2.ObservableK<A>

@JvmName("Ref")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Ref(arg0: A): ObservableK<Ref<ForObservableK, A>> = arrow.fx.rx2.ObservableK
   .monadDefer()
   .Ref<A>(arg0) as arrow.fx.rx2.ObservableK<arrow.fx.Ref<arrow.fx.rx2.ForObservableK, A>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.monadDefer(): ObservableKMonadDefer = monadDefer_singleton
