package arrow.fx.rx2.extensions.singlek.monadDefer

import arrow.Kind
import arrow.core.Either
import arrow.fx.Ref
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.ForSingleK
import arrow.fx.rx2.SingleK
import arrow.fx.rx2.SingleK.Companion
import arrow.fx.rx2.extensions.SingleKMonadDefer
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
internal val monadDefer_singleton: SingleKMonadDefer = object :
    arrow.fx.rx2.extensions.SingleKMonadDefer {}

@JvmName("defer")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> defer(arg0: Function0<Kind<ForSingleK, A>>): SingleK<A> = arrow.fx.rx2.SingleK
   .monadDefer()
   .defer<A>(arg0) as arrow.fx.rx2.SingleK<A>

@JvmName("later")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> later(arg0: Function0<A>): SingleK<A> = arrow.fx.rx2.SingleK
   .monadDefer()
   .later<A>(arg0) as arrow.fx.rx2.SingleK<A>

@JvmName("later")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> later(arg0: Kind<ForSingleK, A>): SingleK<A> = arrow.fx.rx2.SingleK
   .monadDefer()
   .later<A>(arg0) as arrow.fx.rx2.SingleK<A>

@JvmName("lazy")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun lazy(): SingleK<Unit> = arrow.fx.rx2.SingleK
   .monadDefer()
   .lazy() as arrow.fx.rx2.SingleK<kotlin.Unit>

@JvmName("laterOrRaise")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> laterOrRaise(arg0: Function0<Either<Throwable, A>>): SingleK<A> = arrow.fx.rx2.SingleK
   .monadDefer()
   .laterOrRaise<A>(arg0) as arrow.fx.rx2.SingleK<A>

@JvmName("Ref")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Ref(arg0: A): SingleK<Ref<ForSingleK, A>> = arrow.fx.rx2.SingleK
   .monadDefer()
   .Ref<A>(arg0) as arrow.fx.rx2.SingleK<arrow.fx.Ref<arrow.fx.rx2.ForSingleK, A>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.monadDefer(): SingleKMonadDefer = monadDefer_singleton
