package arrow.fx.rx2.extensions.maybek.effect

import arrow.Kind
import arrow.core.Either
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.ForMaybeK
import arrow.fx.rx2.MaybeK
import arrow.fx.rx2.MaybeK.Companion
import arrow.fx.rx2.extensions.MaybeKEffect
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
internal val effect_singleton: MaybeKEffect = object : arrow.fx.rx2.extensions.MaybeKEffect {}

@JvmName("runAsync")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForMaybeK, A>.runAsync(arg1: Function1<Either<Throwable, A>, Kind<ForMaybeK, Unit>>):
    MaybeK<Unit> = arrow.fx.rx2.MaybeK.effect().run {
  this@runAsync.runAsync<A>(arg1) as arrow.fx.rx2.MaybeK<kotlin.Unit>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.effect(): MaybeKEffect = effect_singleton
