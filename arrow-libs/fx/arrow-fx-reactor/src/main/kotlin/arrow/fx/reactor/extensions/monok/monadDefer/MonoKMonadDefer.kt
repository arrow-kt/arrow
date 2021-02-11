package arrow.fx.reactor.extensions.monok.monadDefer

import arrow.Kind
import arrow.core.Either
import arrow.fx.Ref
import arrow.fx.reactor.DeprecateReactor
import arrow.fx.reactor.ForMonoK
import arrow.fx.reactor.MonoK
import arrow.fx.reactor.MonoK.Companion
import arrow.fx.reactor.extensions.MonoKMonadDefer
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
internal val monadDefer_singleton: MonoKMonadDefer = object :
    arrow.fx.reactor.extensions.MonoKMonadDefer {}

@JvmName("defer")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> defer(arg0: Function0<Kind<ForMonoK, A>>): MonoK<A> = arrow.fx.reactor.MonoK
   .monadDefer()
   .defer<A>(arg0) as arrow.fx.reactor.MonoK<A>

@JvmName("later")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> later(arg0: Function0<A>): MonoK<A> = arrow.fx.reactor.MonoK
   .monadDefer()
   .later<A>(arg0) as arrow.fx.reactor.MonoK<A>

@JvmName("later")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> later(arg0: Kind<ForMonoK, A>): MonoK<A> = arrow.fx.reactor.MonoK
   .monadDefer()
   .later<A>(arg0) as arrow.fx.reactor.MonoK<A>

@JvmName("lazy")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun lazy(): MonoK<Unit> = arrow.fx.reactor.MonoK
   .monadDefer()
   .lazy() as arrow.fx.reactor.MonoK<kotlin.Unit>

@JvmName("laterOrRaise")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> laterOrRaise(arg0: Function0<Either<Throwable, A>>): MonoK<A> = arrow.fx.reactor.MonoK
   .monadDefer()
   .laterOrRaise<A>(arg0) as arrow.fx.reactor.MonoK<A>

@JvmName("Ref")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Ref(arg0: A): MonoK<Ref<ForMonoK, A>> = arrow.fx.reactor.MonoK
   .monadDefer()
   .Ref<A>(arg0) as arrow.fx.reactor.MonoK<arrow.fx.Ref<arrow.fx.reactor.ForMonoK, A>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateReactor)
inline fun Companion.monadDefer(): MonoKMonadDefer = monadDefer_singleton
