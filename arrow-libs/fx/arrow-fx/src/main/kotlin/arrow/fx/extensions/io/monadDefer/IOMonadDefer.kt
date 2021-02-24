package arrow.fx.extensions.io.monadDefer

import arrow.Kind
import arrow.core.Either
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.IO.Companion
import arrow.fx.IODeprecation
import arrow.fx.Ref
import arrow.fx.extensions.IOMonadDefer
import kotlin.Deprecated
import kotlin.Function0
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Throwable
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val monadDefer_singleton: IOMonadDefer = object : arrow.fx.extensions.IOMonadDefer {}

@JvmName("defer")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> defer(fa: Function0<Kind<ForIO, A>>): IO<A> = arrow.fx.IO
  .monadDefer()
  .defer<A>(fa) as arrow.fx.IO<A>

@JvmName("later")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> later(f: Function0<A>): IO<A> = arrow.fx.IO
  .monadDefer()
  .later<A>(f) as arrow.fx.IO<A>

@JvmName("later")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> later(fa: Kind<ForIO, A>): IO<A> = arrow.fx.IO
  .monadDefer()
  .later<A>(fa) as arrow.fx.IO<A>

@JvmName("laterOrRaise")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> laterOrRaise(f: Function0<Either<Throwable, A>>): IO<A> = arrow.fx.IO
  .monadDefer()
  .laterOrRaise<A>(f) as arrow.fx.IO<A>

@JvmName("Ref")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> Ref(a: A): IO<Ref<ForIO, A>> = arrow.fx.IO
  .monadDefer()
  .Ref<A>(a) as arrow.fx.IO<arrow.fx.Ref<arrow.fx.ForIO, A>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun Companion.monadDefer(): IOMonadDefer = monadDefer_singleton
