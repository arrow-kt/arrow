package arrow.fx.reactor.extensions.monok.monadThrow

import arrow.fx.reactor.DeprecateReactor
import arrow.fx.reactor.MonoK
import arrow.fx.reactor.MonoK.Companion
import arrow.fx.reactor.extensions.MonoKMonadThrow
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Throwable
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val monadThrow_singleton: MonoKMonadThrow = object :
  arrow.fx.reactor.extensions.MonoKMonadThrow {}

@JvmName("raiseNonFatal")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Throwable.raiseNonFatal(): MonoK<A> = arrow.fx.reactor.MonoK.monadThrow().run {
  this@raiseNonFatal.raiseNonFatal<A>() as arrow.fx.reactor.MonoK<A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateReactor)
inline fun Companion.monadThrow(): MonoKMonadThrow = monadThrow_singleton
