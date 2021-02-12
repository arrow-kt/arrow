package arrow.fx.extensions.io.effect

import arrow.Kind
import arrow.core.Either
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.IO.Companion
import arrow.fx.IODeprecation
import arrow.fx.extensions.IOEffect
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
internal val effect_singleton: IOEffect = object : arrow.fx.extensions.IOEffect {}

@JvmName("runAsync")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> Kind<ForIO, A>.runAsync(cb: Function1<Either<Throwable, A>, Kind<ForIO, Unit>>): IO<Unit> =
    arrow.fx.IO.effect().run {
  this@runAsync.runAsync<A>(cb) as arrow.fx.IO<kotlin.Unit>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun Companion.effect(): IOEffect = effect_singleton
