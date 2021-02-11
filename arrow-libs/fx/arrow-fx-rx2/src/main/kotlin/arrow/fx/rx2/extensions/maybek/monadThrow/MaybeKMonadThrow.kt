package arrow.fx.rx2.extensions.maybek.monadThrow

import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.MaybeK
import arrow.fx.rx2.MaybeK.Companion
import arrow.fx.rx2.extensions.MaybeKMonadThrow
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Throwable
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val monadThrow_singleton: MaybeKMonadThrow = object :
    arrow.fx.rx2.extensions.MaybeKMonadThrow {}

@JvmName("raiseNonFatal")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Throwable.raiseNonFatal(): MaybeK<A> = arrow.fx.rx2.MaybeK.monadThrow().run {
  this@raiseNonFatal.raiseNonFatal<A>() as arrow.fx.rx2.MaybeK<A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.monadThrow(): MaybeKMonadThrow = monadThrow_singleton
