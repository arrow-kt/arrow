package arrow.fx.reactor.extensions.fluxk.applicativeError

import arrow.Kind
import arrow.core.Either
import arrow.core.ForOption
import arrow.fx.reactor.DeprecateReactor
import arrow.fx.reactor.FluxK
import arrow.fx.reactor.FluxK.Companion
import arrow.fx.reactor.ForFluxK
import arrow.fx.reactor.extensions.FluxKApplicativeError
import arrow.typeclasses.ApplicativeError
import kotlin.Deprecated
import kotlin.Function0
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Throwable
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val applicativeError_singleton: FluxKApplicativeError = object :
  arrow.fx.reactor.extensions.FluxKApplicativeError {}

@JvmName("handleErrorWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Kind<ForFluxK, A>.handleErrorWith(arg1: Function1<Throwable, Kind<ForFluxK, A>>): FluxK<A> =
  arrow.fx.reactor.FluxK.applicativeError().run {
    this@handleErrorWith.handleErrorWith<A>(arg1) as arrow.fx.reactor.FluxK<A>
  }

@JvmName("raiseError1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Throwable.raiseError(): FluxK<A> = arrow.fx.reactor.FluxK.applicativeError().run {
  this@raiseError.raiseError<A>() as arrow.fx.reactor.FluxK<A>
}

@JvmName("fromOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Kind<ForOption, A>.fromOption(arg1: Function0<Throwable>): FluxK<A> =
  arrow.fx.reactor.FluxK.applicativeError().run {
    this@fromOption.fromOption<A>(arg1) as arrow.fx.reactor.FluxK<A>
  }

@JvmName("fromEither")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, EE> Either<EE, A>.fromEither(arg1: Function1<EE, Throwable>): FluxK<A> =
  arrow.fx.reactor.FluxK.applicativeError().run {
    this@fromEither.fromEither<A, EE>(arg1) as arrow.fx.reactor.FluxK<A>
  }

@JvmName("handleError")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Kind<ForFluxK, A>.handleError(arg1: Function1<Throwable, A>): FluxK<A> =
  arrow.fx.reactor.FluxK.applicativeError().run {
    this@handleError.handleError<A>(arg1) as arrow.fx.reactor.FluxK<A>
  }

@JvmName("redeem")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForFluxK, A>.redeem(arg1: Function1<Throwable, B>, arg2: Function1<A, B>): FluxK<B> =
  arrow.fx.reactor.FluxK.applicativeError().run {
    this@redeem.redeem<A, B>(arg1, arg2) as arrow.fx.reactor.FluxK<B>
  }

@JvmName("attempt")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Kind<ForFluxK, A>.attempt(): FluxK<Either<Throwable, A>> =
  arrow.fx.reactor.FluxK.applicativeError().run {
    this@attempt.attempt<A>() as arrow.fx.reactor.FluxK<arrow.core.Either<kotlin.Throwable, A>>
  }

@JvmName("catch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> catch(arg0: Function1<Throwable, Throwable>, arg1: Function0<A>): FluxK<A> =
  arrow.fx.reactor.FluxK
    .applicativeError()
    .catch<A>(arg0, arg1) as arrow.fx.reactor.FluxK<A>

@JvmName("catch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> ApplicativeError<ForFluxK, Throwable>.catch(arg1: Function0<A>): FluxK<A> =
  arrow.fx.reactor.FluxK.applicativeError().run {
    this@catch.catch<A>(arg1) as arrow.fx.reactor.FluxK<A>
  }

@JvmName("effectCatch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
suspend fun <A> effectCatch(arg0: Function1<Throwable, Throwable>, arg1: suspend () -> A): FluxK<A> =
  arrow.fx.reactor.FluxK
    .applicativeError()
    .effectCatch<A>(arg0, arg1) as arrow.fx.reactor.FluxK<A>

@JvmName("effectCatch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
suspend fun <F, A> ApplicativeError<F, Throwable>.effectCatch(arg1: suspend () -> A): Kind<F, A> =
  arrow.fx.reactor.FluxK.applicativeError().run {
    this@effectCatch.effectCatch<F, A>(arg1) as arrow.Kind<F, A>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateReactor)
inline fun Companion.applicativeError(): FluxKApplicativeError = applicativeError_singleton
