package arrow.fx.reactor.extensions.monok.effect

import arrow.Kind
import arrow.core.Either
import arrow.fx.reactor.DeprecateReactor
import arrow.fx.reactor.ForMonoK
import arrow.fx.reactor.MonoK
import arrow.fx.reactor.MonoK.Companion
import arrow.fx.reactor.extensions.MonoKEffect
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
internal val effect_singleton: MonoKEffect = object : arrow.fx.reactor.extensions.MonoKEffect {}

@JvmName("runAsync")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Kind<ForMonoK, A>.runAsync(arg1: Function1<Either<Throwable, A>, Kind<ForMonoK, Unit>>):
    MonoK<Unit> = arrow.fx.reactor.MonoK.effect().run {
  this@runAsync.runAsync<A>(arg1) as arrow.fx.reactor.MonoK<kotlin.Unit>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateReactor)
inline fun Companion.effect(): MonoKEffect = effect_singleton
