package arrow.fx.rx2.extensions.maybek.unsafeRun

import arrow.Kind
import arrow.core.Either
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.ForMaybeK
import arrow.fx.rx2.MaybeK.Companion
import arrow.fx.rx2.extensions.MaybeKUnsafeRun
import arrow.unsafe
import kotlin.Deprecated
import kotlin.Function0
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Throwable
import kotlin.Unit
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val unsafeRun_singleton: MaybeKUnsafeRun = object : arrow.fx.rx2.extensions.MaybeKUnsafeRun
{}

@JvmName("runBlocking")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
suspend fun <A> unsafe.runBlocking(arg1: Function0<Kind<ForMaybeK, A>>): A =
  arrow.fx.rx2.MaybeK.unsafeRun().run {
    this@runBlocking.runBlocking<A>(arg1) as A
  }

@JvmName("runNonBlocking")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
suspend fun <A> unsafe.runNonBlocking(
  arg1: Function0<Kind<ForMaybeK, A>>,
  arg2: Function1<Either<Throwable, A>, Unit>
): Unit = arrow.fx.rx2.MaybeK.unsafeRun().run {
  this@runNonBlocking.runNonBlocking<A>(arg1, arg2) as kotlin.Unit
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.unsafeRun(): MaybeKUnsafeRun = unsafeRun_singleton
