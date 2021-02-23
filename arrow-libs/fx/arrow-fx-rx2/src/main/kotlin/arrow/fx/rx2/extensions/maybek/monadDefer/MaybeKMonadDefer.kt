package arrow.fx.rx2.extensions.maybek.monadDefer

import arrow.Kind
import arrow.core.Either
import arrow.fx.Ref
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.ForMaybeK
import arrow.fx.rx2.MaybeK
import arrow.fx.rx2.MaybeK.Companion
import arrow.fx.rx2.extensions.MaybeKMonadDefer
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
internal val monadDefer_singleton: MaybeKMonadDefer = object :
  arrow.fx.rx2.extensions.MaybeKMonadDefer {}

@JvmName("defer")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> defer(arg0: Function0<Kind<ForMaybeK, A>>): MaybeK<A> = arrow.fx.rx2.MaybeK
  .monadDefer()
  .defer<A>(arg0) as arrow.fx.rx2.MaybeK<A>

@JvmName("later")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> later(arg0: Function0<A>): MaybeK<A> = arrow.fx.rx2.MaybeK
  .monadDefer()
  .later<A>(arg0) as arrow.fx.rx2.MaybeK<A>

@JvmName("later")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> later(arg0: Kind<ForMaybeK, A>): MaybeK<A> = arrow.fx.rx2.MaybeK
  .monadDefer()
  .later<A>(arg0) as arrow.fx.rx2.MaybeK<A>

@JvmName("lazy")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun lazy(): MaybeK<Unit> = arrow.fx.rx2.MaybeK
  .monadDefer()
  .lazy() as arrow.fx.rx2.MaybeK<kotlin.Unit>

@JvmName("laterOrRaise")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> laterOrRaise(arg0: Function0<Either<Throwable, A>>): MaybeK<A> = arrow.fx.rx2.MaybeK
  .monadDefer()
  .laterOrRaise<A>(arg0) as arrow.fx.rx2.MaybeK<A>

@JvmName("Ref")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Ref(arg0: A): MaybeK<Ref<ForMaybeK, A>> = arrow.fx.rx2.MaybeK
  .monadDefer()
  .Ref<A>(arg0) as arrow.fx.rx2.MaybeK<arrow.fx.Ref<arrow.fx.rx2.ForMaybeK, A>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.monadDefer(): MaybeKMonadDefer = monadDefer_singleton
