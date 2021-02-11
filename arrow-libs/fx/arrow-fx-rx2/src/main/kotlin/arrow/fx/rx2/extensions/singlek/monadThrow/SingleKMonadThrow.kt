package arrow.fx.rx2.extensions.singlek.monadThrow

import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.SingleK
import arrow.fx.rx2.SingleK.Companion
import arrow.fx.rx2.extensions.SingleKMonadThrow
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Throwable
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val monadThrow_singleton: SingleKMonadThrow = object :
    arrow.fx.rx2.extensions.SingleKMonadThrow {}

@JvmName("raiseNonFatal")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Throwable.raiseNonFatal(): SingleK<A> = arrow.fx.rx2.SingleK.monadThrow().run {
  this@raiseNonFatal.raiseNonFatal<A>() as arrow.fx.rx2.SingleK<A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.monadThrow(): SingleKMonadThrow = monadThrow_singleton
