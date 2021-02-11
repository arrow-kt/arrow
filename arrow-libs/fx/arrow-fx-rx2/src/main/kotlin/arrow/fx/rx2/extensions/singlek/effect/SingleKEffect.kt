package arrow.fx.rx2.extensions.singlek.effect

import arrow.Kind
import arrow.core.Either
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.ForSingleK
import arrow.fx.rx2.SingleK
import arrow.fx.rx2.SingleK.Companion
import arrow.fx.rx2.extensions.SingleKEffect
import kotlin.Deprecated
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
internal val effect_singleton: SingleKEffect = object : arrow.fx.rx2.extensions.SingleKEffect {}

@JvmName("runAsync")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForSingleK, A>.runAsync(arg1: Function1<Either<Throwable, A>, Kind<ForSingleK, Unit>>):
    SingleK<Unit> = arrow.fx.rx2.SingleK.effect().run {
  this@runAsync.runAsync<A>(arg1) as arrow.fx.rx2.SingleK<kotlin.Unit>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.effect(): SingleKEffect = effect_singleton
