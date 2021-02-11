package arrow.fx.rx2.extensions.observablek.monadThrow

import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.ObservableK
import arrow.fx.rx2.ObservableK.Companion
import arrow.fx.rx2.extensions.ObservableKMonadThrow
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Throwable
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val monadThrow_singleton: ObservableKMonadThrow = object :
    arrow.fx.rx2.extensions.ObservableKMonadThrow {}

@JvmName("raiseNonFatal")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Throwable.raiseNonFatal(): ObservableK<A> = arrow.fx.rx2.ObservableK.monadThrow().run {
  this@raiseNonFatal.raiseNonFatal<A>() as arrow.fx.rx2.ObservableK<A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.monadThrow(): ObservableKMonadThrow = monadThrow_singleton
